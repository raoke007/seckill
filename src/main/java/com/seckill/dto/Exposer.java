package com.seckill.dto;

import lombok.Data;

/**
 * @author raoke007
 * @date 2018/9/12
 */
@Data
public class Exposer {

    //秒杀是否开启
    private boolean exposed;

    //秒杀单id
    private long seckillId;

    //返回给用户的暴露地址
    private String md5;

    //当前时间
    private long now;

    //秒杀开始时间
    private long startTime;

    //秒杀结束时间
    private long endTime;

    public Exposer(boolean exposed, long seckillId, String md5) {
        this.exposed = exposed;
        this.seckillId = seckillId;
        this.md5 = md5;
    }

    public Exposer(boolean exposed, long seckillId, long now, long startTime, long endTime) {
        this.exposed = exposed;
        this.seckillId = seckillId;
        this.now = now;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Exposer(boolean exposed, long seckillId) {
        this.exposed = exposed;
        this.seckillId = seckillId;
    }
}
