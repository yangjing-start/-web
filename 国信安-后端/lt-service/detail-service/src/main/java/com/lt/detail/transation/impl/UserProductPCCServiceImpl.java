//package com.lt.detail.transation.impl;
//
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.lt.detail.mapper.UserProductMapper;
//import com.lt.detail.transation.UserProductPCCService;
//import com.lt.detail.transation.mapper.UserProductStateMapper;
//import com.lt.model.state.UserProductState;
//import com.lt.model.user.User;
//import com.lt.model.user.UserProduct;
//import io.seata.core.context.RootContext;
//import io.seata.rm.tcc.api.BusinessActionContext;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
///**
// * @author Lhz
// */
//@Service("UserProductPCCServiceImpl")
//@RequiredArgsConstructor
//@Slf4j
//public class UserProductPCCServiceImpl implements UserProductPCCService {
//
//    private final UserProductStateMapper userProductStateMapper;
//    private final UserProductMapper userProductMapper;
//
//    @Override
//    public void insertUserProduct(UserProduct userProduct) {
//        String xid = RootContext.getXID();
//        System.out.println("insertUserProduct: "+ RootContext.getXID());
//        UserProductState userProductState = userProductStateMapper.selectById(xid);
//        if(userProductState != null){
//            return;
//        }
//
//        userProductMapper.insert(userProduct);
//
//        UserProductState now = new UserProductState();
//        now.setState(UserProductState.State.TRY);
//        now.setXid(xid);
//        now.setUsername(userProduct.getUsername());
//        userProductStateMapper.insert(now);
//    }
//
//    @Override
//    public Boolean confirm(BusinessActionContext context) {
//        String xid = RootContext.getXID();
//        log.debug("confirm : {}",xid);
//        System.out.println("confirm: "+ RootContext.getXID());
//        int count = userProductStateMapper.deleteById(xid);
//        return count == 1;
//    }
//
//    @Override
//    public Boolean cancel(BusinessActionContext context) {
//        String xid = RootContext.getXID();
//        log.debug("cancel : {}",xid);
//        System.out.println("cancel: "+ RootContext.getXID());
//        UserProductState userProductState = userProductStateMapper.selectById(xid);
//        if(userProductState.getState() == UserProductState.State.TRY){
//            UserProduct userProduct = (UserProduct) context.getActionContext("userProduct");
//            UserProductState now = new UserProductState();
//            now.setState(UserProductState.State.CANCEL);
//            now.setXid(xid);
//            now.setUsername(userProduct.getUsername());
//            userProductStateMapper.insert(now);
//            return true;
//        }
//        if(userProductState.getState() == UserProductState.State.CANCEL){
//            return true;
//        }
//        UserProduct userProduct = (UserProduct) context.getActionContext("userProduct");
//        LambdaQueryWrapper<UserProduct> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(UserProduct::getUsername, userProduct.getUsername());
//        int delete = userProductMapper.delete(lambdaQueryWrapper);
//        return delete == 1;
//    }
//}
