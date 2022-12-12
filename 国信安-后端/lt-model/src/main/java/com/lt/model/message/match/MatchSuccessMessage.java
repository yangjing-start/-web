package com.lt.model.message.match;

import com.lt.model.message.Message;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * {
 *   "successId": "test_185eecab966a",
 *   "talkId": 73,
 *   "listenId": 13
 * }
 * @author Lhz
 */

@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class MatchSuccessMessage extends Message {

    public final String successId;

    public final Integer talkId;

    public final Integer listenId;

    @Override
    public byte getMessageType() {
        return HEART_SUCCESS_MESSAGE;
    }
}
