package com.lt.model.message.user;

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
public class LoginMessage extends Message {

    private final Integer username;

    @Override
    public byte getMessageType() {
        return LOGIN_MESSAGE;
    }
}
