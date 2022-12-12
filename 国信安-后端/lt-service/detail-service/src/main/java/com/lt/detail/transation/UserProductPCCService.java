//package com.lt.detail.transation;
//
//import com.lt.model.user.UserProduct;
//import io.seata.rm.tcc.api.BusinessActionContext;
//import io.seata.rm.tcc.api.BusinessActionContextParameter;
//import io.seata.rm.tcc.api.LocalTCC;
//import io.seata.rm.tcc.api.TwoPhaseBusinessAction;
//
///**
// * @author Lhz
// */
//@LocalTCC
//public interface UserProductPCCService {
//
//    @TwoPhaseBusinessAction(name = "insertUserProduct", commitMethod = "confirm", rollbackMethod = "cancel")
//    void insertUserProduct(@BusinessActionContextParameter(paramName = "userProduct")UserProduct userProduct);
//
//    Boolean confirm(BusinessActionContext context);
//
//    Boolean cancel(BusinessActionContext context);
//}
