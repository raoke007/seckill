package com.seckill.service.impl;

import com.seckill.dto.Exposer;
import com.seckill.dto.SeckillExcution;
import com.seckill.entity.Seckill;
import com.seckill.exception.RepeatKillException;
import com.seckill.exception.SeckillCloseException;
import com.seckill.exception.SeckillException;
import com.seckill.service.SeckillService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author raoke007
 * @Description
 * @date 2018/9/12
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"})
public class SeckillServiceImplTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSeckillList() {
        List<Seckill> seckillList = seckillService.getSeckillList();
        logger.info("list={}", seckillList);
    }

    @Test
    public void getById() {
        long seckillId = 1001L;
        Seckill seckill = seckillService.getById(seckillId);
        logger.info("seckill={}", seckill);
    }

    @Test
    public void testSeckillLogic() {
        long seckillId = 1001L;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);

        if (exposer.isExposed()) {
            logger.info("exposer={}", exposer);

            String md5 = exposer.getMd5();
            long userPhone = 19932963362L;
            try {
                SeckillExcution seckillExcution = seckillService.executeSeckill(seckillId, userPhone, md5);
                logger.info("seckillExcution={}", seckillExcution);
            } catch (RepeatKillException e) {
                logger.error(e.getMessage());
            } catch (SeckillCloseException e) {
                logger.error(e.getMessage());
            } catch (SeckillException e) {
                logger.error(e.getMessage());
            }

        } else {
            logger.warn("exposer={}", exposer);
        }
    }

    @Test
    public void testSeckillLogicPro() {
        long seckillId = 1003L;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);

        if (exposer.isExposed()) {
            logger.info("exposer={}", exposer);

            String md5 = exposer.getMd5();
            long userPhone = 19932963364L;
            try {
                SeckillExcution seckillExcution = seckillService.executeSeckillPro(seckillId, userPhone, md5);
                logger.info("seckillExcution={}", seckillExcution);
            } catch (RepeatKillException e) {
                logger.error(e.getMessage());
            } catch (SeckillCloseException e) {
                logger.error(e.getMessage());
            } catch (SeckillException e) {
                logger.error(e.getMessage());
            }

        } else {
            logger.warn("exposer={}", exposer);
        }
    }
}