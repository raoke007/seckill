package com.seckill.enums;

/**
 * @author raoke007
 * @date 2018/9/12
 */
public enum SeckillStateEnum {

    SUCCESS(1, "秒杀成功"),
    END(0, "秒杀结束"),
    REPEAT_KILL(-1, "重复秒杀"),
    DATA_REWRITE(-2, "数据篡改"),
    INNER_ERROR(-3, "系统异常");

    private int state;

    private String stateInfo;

    SeckillStateEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    public int getState() {
        return state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public static String stateOf(int state) {
        for (SeckillStateEnum stateEnum : values()) {
            if (stateEnum.getState() == state) {
                return stateEnum.getStateInfo();
            }
        }

        return null;
    }
}
