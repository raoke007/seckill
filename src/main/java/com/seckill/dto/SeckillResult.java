package com.seckill.dto;

import lombok.Data;

/**
 * <p>Description</p> 封装所有ajax返回类型，json形式，增加此类的原因是最好返回前端统一类型的结果
 * @author raoke007
 * @date 2018/9/13
 */
@Data
public class SeckillResult<T> {

    private boolean success;

    private T data;

    private String error;

    public SeckillResult(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public SeckillResult(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

}
