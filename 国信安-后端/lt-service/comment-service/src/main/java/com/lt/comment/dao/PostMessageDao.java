package com.lt.comment.dao;

import com.lt.model.user.PostMessage;

import java.util.List;

/**
 * @author Lhz
 */
public interface PostMessageDao {

    void insertPostMessage(PostMessage postMessage);

    void deletePostMessage(String username);

    List<PostMessage> getPostMessages(String username);


}
