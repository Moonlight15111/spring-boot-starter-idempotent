package org.moonlight.idempotent.exception;

/**
 * 数据幂等异常
 * @author Moonlight
 */
public class ServiceException extends RuntimeException {

    public ServiceException() {}

    public ServiceException(String msg) {
        super(msg);
    }

    public ServiceException(Throwable t) {
        super(t);
    }

    public ServiceException(String msg, Throwable t) {
        super(msg, t);
    }

}
