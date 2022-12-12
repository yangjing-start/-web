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
public class SingleChatImageMessage extends Message {
    @Override
    public byte getMessageType() {
        return SINGLE_CHAT_IMAGE_MESSAGE;
    }
}
