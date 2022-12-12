package com.lt.match;

import io.netty.util.AttributeKey;

/**
 * @author Lhz
 */
public interface Attributes {
    AttributeKey<Integer> SESSION = AttributeKey.newInstance("session");
}
