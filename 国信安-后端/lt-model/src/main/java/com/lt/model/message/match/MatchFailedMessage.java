package com.lt.model.message.match;

import com.lt.model.message.Message;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Lhz
 */

@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class MatchFailedMessage extends Message {
    @Override
    public byte getMessageType() {
        return HEART_FAILED_MESSAGE;
    }
}
