//package com.lt.comment.transaction;
//
//
//import io.seata.rm.tcc.api.BusinessActionContext;
//import io.seata.rm.tcc.api.BusinessActionContextParameter;
//import io.seata.rm.tcc.api.LocalTCC;
//import io.seata.rm.tcc.api.TwoPhaseBusinessAction;
//
//
//@LocalTCC
//public interface PostTCCService {
//
//    @TwoPhaseBusinessAction(name = "updateFans", commitMethod = "confirm", rollbackMethod = "cancel")
//    void updateFans(@BusinessActionContextParameter(paramName = "username") String username,
//                    @BusinessActionContextParameter(paramName = "selfUserName")String selfUserName,
//                    @BusinessActionContextParameter(paramName = "nickName")String nickName,
//                    @BusinessActionContextParameter(paramName = "userImage")String userImage);
//
//    boolean confirm(BusinessActionContext context);
//
//    boolean cancel(BusinessActionContext context);
//}
