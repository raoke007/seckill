package com.seckill.dto;

import com.seckill.entity.SuccessSeckilled;
import com.seckill.enums.SeckillStateEnum;
import lombok.Data;

/**
 * @author raoke007
 * @date 2018/9/12
 */
@Data
public class SeckillExcution {

    //秒杀单id
    private long seckillId;

    //秒杀执行结果状态
    private int state;

    //状态表示
    private String stateInfo;

    //成功后返回秒杀结果对象
    private SuccessSeckilled successSeckilled;

    public SeckillExcution(long seckillId, SeckillStateEnum stateEnum, SuccessSeckilled successSeckilled) {
        this.seckillId = seckillId;
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.successSeckilled = successSeckilled;
    }

    public SeckillExcution(long seckillId, SeckillStateEnum stateEnum) {
        this.seckillId = seckillId;
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
    }

    public SeckillExcution(long seckillId, String stateInfo) {
        this.seckillId = seckillId;
        this.stateInfo = stateInfo;
    }
}
