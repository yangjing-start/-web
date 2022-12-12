package com.lt.model.message.match;

import com.lt.model.message.Message;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 中断消息
 * {
 *   "userId": 92,
 *   "matchType": 22
 * }
 * @author Lhz
 * @date 2022/11/15
 */

@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class MatchInterruptMessage extends Message {

    /**
     * 用户id
     */
    public final Integer username;

    /**
     * 匹配类型
     */
    public final Integer matchType;

    /**
     * 得到消息类型
     *
     * @return byte
     */
    @Override
    public byte getMessageType() {
        return HEART_INTERRUPT_MESSAGE;
    }
}
