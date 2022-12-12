//package com.lt.comment.transaction.impl;
//
//
//import com.lt.comment.service.PostMessageService;
//import com.lt.comment.transaction.PostTCCService;
//import com.lt.comment.transaction.mapper.PostMessageStateMapper;
//import com.lt.model.state.PostMessageState;
//import com.lt.model.user.PostMessage;
//import io.seata.core.context.RootContext;
//import io.seata.rm.tcc.api.BusinessActionContext;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.util.ObjectUtils;
//
///**
// * @author Lhz
// */
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class PostTCCServiceImpl implements PostTCCService {
//    //分支一
//    private final PostMessageService postMessageService;
//    private final PostMessageStateMapper postMessageStateMapper;
//
//    @Override
//    public void updateFans(String username, String selfUserName, String nickName, String userImage) {
//        //获取事务id
//        String xid = RootContext.getXID();
//        log.info("updateFans : {}",xid);
//        //判断 PostMessageState 中是否有记录标志，如果有，则一定是CANCEL执行过了，拒绝业务
//        PostMessageState postMessageState = postMessageStateMapper.selectById(xid);
//        if(!ObjectUtils.isEmpty(postMessageState)){
//            //已执行过Cancel 不能再次执行
//            return;
//        }
//
//        //插入一条 PostMessage信息
//        PostMessage postMessage = PostMessage.builder().message("您被" + nickName + "关注了")
//                .nickName(nickName).image(userImage).type(2).otherId(selfUserName)
//                .selfId(username).time(String.valueOf(System.currentTimeMillis())).build();
//        postMessageService.insertPostMessage(postMessage);
//
//        //标记现在为 Try 状态
//        PostMessageState postMessageStateNow = new PostMessageState();
//        postMessageStateNow.setState(PostMessageState.State.TRY);
//        postMessageStateNow.setXid(xid);
//        postMessageStateNow.setSelfUsername(selfUserName);
//        postMessageStateMapper.insert(postMessageStateNow);
//    }
//
//    @Override
//    public boolean confirm(BusinessActionContext context) {
//        // 1.获取事务id
//        String xid = context.getXid();
//        log.info("confirm : {}",xid);
//        // 2.根据事务id删除 标记
//        int count = postMessageStateMapper.deleteById(xid);
//        return count == 1;
//    }
//
//    @Override
//    public boolean cancel(BusinessActionContext context) {
//        //获取事务id
//        String xid = RootContext.getXID();
//        log.info("cancel : {}",xid);
//        //获取当前事务 查看是否完成try操作 ，若无则需执行空回滚
//        PostMessageState postMessageState = postMessageStateMapper.selectById(xid);
//
//        if(ObjectUtils.isEmpty(postMessageState)){
//            //执行空回滚
//            String selfUserName = context.getActionContext("selfUserName").toString();
//            postMessageState = new PostMessageState();
//            postMessageState.setState(PostMessageState.State.CANCEL);
//            postMessageState.setXid(xid);
//            postMessageState.setSelfUsername(selfUserName);
//            postMessageStateMapper.insert(postMessageState);
//            return true;
//        }
//        //幂等判断
//        if(postMessageState.getState() == PostMessageState.State.CANCEL){
//            // 已经执行过一次CANCEL，无需重复处理
//            return true;
//        }
//
//        String selfUserName = context.getActionContext("selfUserName").toString();
//        //执行与 try 相反的操作 删除消息
//        postMessageService.deletePostMessage(selfUserName);
//        //将 当前事务状态置为 Cancel
//        postMessageState.setState(PostMessageState.State.CANCEL);
//        int count = postMessageStateMapper.updateById(postMessageState);
//        return count == 1;
//    }
//}
