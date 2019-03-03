package com.seckill.exception;

/**
 * @author raoke007
 * @date 2018/9/12
 */
public class SeckillException extends RuntimeException {
    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
