package com.seckill.dao;

import com.seckill.entity.SuccessSeckilled;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * @author raoke007
 * @date 2018/9/11
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessSeckilledDaoTest {

    @Resource
    private SuccessSeckilledDao successSeckilledDao;

    @Test
    public void insertSuccessSeckilled() {
        long seckillId = 1000L;
        long userPhone = 19999999999L;

        int result = successSeckilledDao.insertSuccessSeckilled(seckillId, userPhone);
        System.out.println(result);
    }

    @Test
    public void queryByIdWithSeckill() {
        long seckillId = 1000L;
        long userPhone = 19999999999L;

        SuccessSeckilled successSeckilled = successSeckilledDao.queryByIdWithSeckill(seckillId, userPhone);
        System.out.println(successSeckilled);
        System.out.println(successSeckilled.getSeckill());
    }
}