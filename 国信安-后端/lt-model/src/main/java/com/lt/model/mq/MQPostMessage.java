package com.lt.model.mq;

import lombok.Data;

/**
 * @author Lhz
 */
@Data
public class MQPostMessage {
    public String selfUsername;
    public String otherUsername;
    public String otherUserImage;
    public String otherNickname;
}
