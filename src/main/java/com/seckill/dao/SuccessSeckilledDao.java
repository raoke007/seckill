package com.seckill.dao;

import com.seckill.entity.SuccessSeckilled;
import org.apache.ibatis.annotations.Param;

/**
 * @author raoke007
 * @date 2018/9/11
 */
public interface SuccessSeckilledDao {

    /**
     * 插入秒杀成功明细表
     * @param seckillId
     * @param userPhone
     * @return 返回受影响行数 =1插入成功，=0重复秒杀
     */
    int insertSuccessSeckilled(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);

    /**
     * 根据id查询秒杀成功明细并查询秒杀商品信息
     * @param seckillId
     * @return
     */
    SuccessSeckilled queryByIdWithSeckill(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);
}
