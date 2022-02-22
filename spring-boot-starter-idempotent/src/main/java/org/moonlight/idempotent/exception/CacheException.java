package org.moonlight.idempotent.exception;

/**
 *
 * @author Moonlight
 */
public class CacheException extends RuntimeException {

    public CacheException(String msg) {
        super(msg);
    }

    public CacheException(Throwable t) {
        super(t);
    }

    public CacheException(String msg, Throwable t) {
        super(msg, t);
    }

}