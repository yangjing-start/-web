package com.lt.utils;

import lombok.Data;

import java.util.Date;

/**
 * @author Lhz
 */
@Data
public class Payload<T> {
    private String id;
    private T userInfo;
    private Date expiration;
}
