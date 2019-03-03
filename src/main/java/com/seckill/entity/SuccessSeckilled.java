package com.seckill.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author raoke007
 * @date 2018/9/11
 */
@Data
public class SuccessSeckilled {

    private long seckillId;

    private long userPhone;

    private short state;

    private Date createTime;

    private Seckill seckill;
}
