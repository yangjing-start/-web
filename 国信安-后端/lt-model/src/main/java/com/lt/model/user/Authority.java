package com.lt.model.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

/**
 * 角色
 *
 * @author Lhz
 * @date 2022/11/15
 */
@Data
@TableName("authority")
public class Authority implements GrantedAuthority {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 名称
     */
    private String name;

    @Override
    public String getAuthority() {
        return name;
    }
}
