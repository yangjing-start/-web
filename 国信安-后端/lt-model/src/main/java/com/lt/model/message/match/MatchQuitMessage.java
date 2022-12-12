package com.lt.model.message.match;

import com.lt.model.message.Message;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * {
 *   "userId": 89,
 *   "matchType": 58,
 *   "successId": "test_ce564969b84a"
 * }
 * @author Lhz
 */

@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class MatchQuitMessage extends Message {

    public final Integer username;

    /**
     *  0 : Talk
     *  1 : Listen
     */
    public final Integer matchType;

    public final String successId;
    @Override
    public byte getMessageType() {
        return HEART_QUIT_MESSAGE;
    }
}
