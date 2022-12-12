package com.lt.model.message.chat;

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
public class SingleChatMessage extends Message {

    private final Integer to;
    private final Integer from;
    private final String content;
    private final String time;

    @Override
    public byte getMessageType() {
        return SINGLE_CHAT_MESSAGE;
    }

}
