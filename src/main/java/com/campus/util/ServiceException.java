package com.campus.util;

/**
 * 业务异常。
 * Service 层在校验不通过或业务规则冲突时抛出，由 Servlet 捕获后向用户展示友好提示。
 */
public class ServiceException extends RuntimeException {
    public ServiceException(String message) {
        super(message);
    }
}
