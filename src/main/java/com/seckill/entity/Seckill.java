package com.seckill.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author raoke007
 * @date 2018/9/11
 */
@Data
public class Seckill {

    private long seckillId;

    private String seckillName;

    private int seckillNumber;

    private Date startTime;

    private Date endTime;

    private Date createTime;
}
