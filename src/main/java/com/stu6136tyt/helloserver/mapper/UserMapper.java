package com.stu6136tyt.helloserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.stu6136tyt.helloserver.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserMapper extends BaseMapper<User> {
    UserDetails selectByUsername(String username);
}
