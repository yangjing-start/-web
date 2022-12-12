package com.lt.detail.feign;


import com.lt.detail.service.UserProductService;

import com.lt.feign.userproduct.IUserProductClient;
import com.lt.model.user.UserProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lhz
 */
@RestController
@RequiredArgsConstructor
public class UserProductClient implements IUserProductClient {

    private final UserProductService userProductService;

    @Override
    @PostMapping("/detail/registration")
    public void setUserDetail(@RequestBody UserProduct userProduct) {
        userProductService.insertUser(userProduct);
    }

    @Override
    @PostMapping("/detail/deleteUser")
    public void deleteUserDetail(@RequestBody String username) {
        userProductService.deleteUser(username);
    }

}
