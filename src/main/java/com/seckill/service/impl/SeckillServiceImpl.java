package com.seckill.service.impl;

import com.seckill.dao.SeckillDao;
import com.seckill.dao.SuccessSeckilledDao;
import com.seckill.dao.cache.RedisDao;
import com.seckill.dto.Exposer;
import com.seckill.dto.SeckillExcution;
import com.seckill.entity.Seckill;
import com.seckill.entity.SuccessSeckilled;
import com.seckill.enums.SeckillStateEnum;
import com.seckill.exception.RepeatKillException;
import com.seckill.exception.SeckillCloseException;
import com.seckill.exception.SeckillException;
import com.seckill.service.SeckillService;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import redis.clients.jedis.Jedis;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author raoke007
 * @date 2018/9/12
 */
@Service
public class SeckillServiceImpl implements SeckillService {

    private final Logger logger = LoggerFactory.getLogger(SeckillServiceImpl.class);

    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private SuccessSeckilledDao successSeckilledDao;

    @Autowired
    private RedisDao redisDao;

    //md5盐值字符串，用于混淆md5算法规律
    private final String slat = ":><?HFUYH&^$%@D!!#@#$$123*&(&(&";

    @Override
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0,5);
    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        Seckill seckill = redisDao.getSeckill(seckillId);

        if (redisDao.getSeckill(seckillId) == null) {
            seckill = seckillDao.queryById(seckillId);

            if (seckill == null) {
                return new Exposer(false, seckillId);
            } else {
                redisDao.putSeckill(seckill);
            }
        }

        long startTime = seckill.getStartTime().getTime();
        long endTime = seckill.getEndTime().getTime();
        long now = new Date().getTime();

        if (now < startTime || now > endTime) {
            return new Exposer(false, seckillId, now, startTime, endTime);
        } else {
            String md5 = getMD5(seckillId);
            return new Exposer(true, seckillId, md5);
        }
    }

    private String getMD5(long seckillId) {
        String base = seckillId + "/" + slat;
        return DigestUtils.md5DigestAsHex(base.getBytes());
    }

    /** 思路
     * 1.判断用户传递的md5是否与重新生成的md5值是否一致，不一致视为篡改，直接抛出异常（SeckillException)
     * 2.执行reduceNumber方法，验证返回值是否为1，为1插入秒杀成功明细，为0视为秒杀结束（实际情况可能包括库存没有等情况）则抛出异常（SeckillCloseException)
     * 3.判断插入秒杀明细返回值，为1则秒杀成功，为0视为重复秒杀，抛出异常（RepeatKillException)
     */
    @Override
    @Transactional
    /**
     * 使用注解控制事务方法的优点：
     * 1.达成约定，形成开发风格（如果使用tx：advice方式，在编程时会偶尔忘记是否是事务方法）。
     * 2.保证事务方法的执行时间尽可能短（事务开启，提交/回滚都会增加运行时间）。
     * 3.编写的addXXX不一定是事务方法，可能是编写错误，而且query方法不需要事务控制。
     */
    public SeckillExcution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException {
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            throw new SeckillException("data rewrite");
        } else {
            try {
                //先插入再update，减少一倍由update行级锁导致的网络延迟和GC
                int insertResult = successSeckilledDao.insertSuccessSeckilled(seckillId, userPhone);
                if (insertResult == 1) {
                    int updateResult = seckillDao.reduceNumber(seckillId, new Date());
                    if (updateResult == 1) {
                        SuccessSeckilled successSeckilled = successSeckilledDao.queryByIdWithSeckill(seckillId, userPhone);
                        return new SeckillExcution(seckillId, SeckillStateEnum.SUCCESS, successSeckilled);
                    } else {
                        throw new SeckillCloseException("seckill is closed");
                    }
                } else {// 重复秒杀
                    throw new RepeatKillException("seckill repeated");
                }
            } catch (SeckillCloseException e1) {
                throw e1;
            } catch (RepeatKillException e2) {
                throw e2;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                //编译期异常转化为运行期异常，此方法为spring声明式事务方法，而spring声明式事务只接受运行期异常
                throw new SeckillException("seckill inner error" + e.getMessage());
            }
        }
    }

    @Override
    public SeckillExcution executeSeckillPro(long seckillId, long userPhone, String md5) {
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            throw new SeckillException("data rewrite");
        } else {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("seckillId", seckillId);
            paramMap.put("userPhone", userPhone);
            paramMap.put("killTime", new Date());
            paramMap.put("result", null);

            try {
                seckillDao.killByProcedure(paramMap);
                int result = MapUtils.getInteger(paramMap, "result", -3);
                if (result == 1) {
                    SuccessSeckilled successSeckilled = successSeckilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExcution(seckillId, SeckillStateEnum.SUCCESS, successSeckilled);
                } else {
                    return new SeckillExcution(seckillId, SeckillStateEnum.stateOf(result));
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return new SeckillExcution(seckillId, SeckillStateEnum.INNER_ERROR);
            }
        }
    }
}
