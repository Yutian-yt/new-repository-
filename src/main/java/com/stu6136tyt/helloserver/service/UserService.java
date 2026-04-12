package com.stu6136tyt.helloserver.service;

import com.stu6136tyt.helloserver.common.Result;
import com.stu6136tyt.helloserver.dto.UserDTO;

public interface UserService {
    Result<String> register(UserDTO userDTO);
    Result<String> login(UserDTO userDTO);
    Result<String> getUserById(Long id);
    Result<Object> getUserPage(Integer pageNum, Integer pageSize);
}