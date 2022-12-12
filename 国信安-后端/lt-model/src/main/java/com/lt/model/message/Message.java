package com.lt.model.message;


import com.lt.model.message.chat.SingleChatMessage;
import com.lt.model.message.match.*;
import com.lt.model.message.user.LoginMessage;
import com.lt.model.message.user.LogoutMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lhz
 */
public abstract class Message {

    /**
     * 返回用户类消息
     *
     * @return 消息类型
     */
    abstract public byte getMessageType();

    /**
     * 用户登录消息
     */
    public static final Byte LOGIN_MESSAGE = 1;

    /**
     * 用户注销消息
     */
    public static final Byte LOGOUT_MESSAGE = 2;
    /**
     * 谈心发起消息
     */
    public static final Byte HEART_TALK_LAUNCH_MESSAGE = 3;
    /**
     * 谈心匹配成功
     */
    public static final Byte HEART_SUCCESS_MESSAGE = 4;
    /**
     * 谈心匹配失败
     */
    public static final Byte HEART_FAILED_MESSAGE = 5;
    /**
     * 谈心退出
     */
    public static final Byte HEART_QUIT_MESSAGE = 6;
    /**
     * 谈心匹配中断
     */
    public static final Byte HEART_INTERRUPT_MESSAGE = 7;
    /**
     * 单聊文字消息
     */
    public static final Byte SINGLE_CHAT_MESSAGE = 9;
    /**
     * 单聊图片消息
     */
    public static final Byte SINGLE_CHAT_IMAGE_MESSAGE = 8;

    public static final Map<Byte, Class<? extends Message>> MESSAGE_CLASSES = new HashMap<>();

    static {
        MESSAGE_CLASSES.put(LOGIN_MESSAGE, LoginMessage.class);
        MESSAGE_CLASSES.put(LOGOUT_MESSAGE, LogoutMessage.class);
        MESSAGE_CLASSES.put(HEART_TALK_LAUNCH_MESSAGE, MatchLaunchMessage.class);
        MESSAGE_CLASSES.put(HEART_SUCCESS_MESSAGE, MatchSuccessMessage.class);
        MESSAGE_CLASSES.put(HEART_QUIT_MESSAGE, MatchQuitMessage.class);
        MESSAGE_CLASSES.put(HEART_INTERRUPT_MESSAGE, MatchInterruptMessage.class);
        MESSAGE_CLASSES.put(SINGLE_CHAT_MESSAGE, SingleChatMessage.class);
        MESSAGE_CLASSES.put(SINGLE_CHAT_IMAGE_MESSAGE, MatchFailedMessage.class);
    }

}
