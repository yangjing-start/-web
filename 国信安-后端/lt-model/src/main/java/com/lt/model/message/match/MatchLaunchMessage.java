package com.lt.model.message.match;

import com.lt.model.message.Message;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 匹配发起消息
 *{
 *   "userId": 41,
 *   "matchType": 24
 * }
 * @author Lhz
 * @date 2022/11/15
 */

@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class MatchLaunchMessage extends Message {

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
        return HEART_TALK_LAUNCH_MESSAGE;
    }
}
