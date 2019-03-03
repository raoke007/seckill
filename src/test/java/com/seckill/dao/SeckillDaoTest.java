package com.seckill.dao;

import com.seckill.entity.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 配置spring和junit, junit启动时加载springIOC容器
 * @author raoke007
 * @date 2018/9/11
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit加载的xml文件位置
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {

    @Resource
    private SeckillDao seckillDao;

    @Test
    public void reduceNumber() {
        long seckillId = 1000L;
        Date now = new Date();

        int result = seckillDao.reduceNumber(seckillId, now);
        System.out.println(result);
    }

    @Test
    public void queryById() {
        long seckillId = 1000L;
        Seckill seckill = seckillDao.queryById(seckillId);

        System.out.println(seckill.getSeckillName());
        System.out.println(seckill);
    }

    @Test
    public void queryAll() {
        int offset = 0, limit = 10;
        List<Seckill> seckills = seckillDao.queryAll(offset, limit);

        for (Seckill seckill : seckills) {
            System.out.println(seckill);
        }
    }
}