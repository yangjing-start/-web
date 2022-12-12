package com.lt.feign.userproduct;

import com.lt.model.user.UserProduct;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Lhz
 */
@FeignClient(value = "lt-service-detail", configuration = DetailFeignConfig.class)
public interface IUserProductClient {

    @PostMapping("/detail/registration")
    void setUserDetail(@RequestBody UserProduct userProduct);

    @PostMapping("/detail/deleteUser")
    void deleteUserDetail(@RequestBody String username);
}
