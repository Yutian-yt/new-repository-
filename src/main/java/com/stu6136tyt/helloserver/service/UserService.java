package com.stu6136tyt.helloserver.service;

import com.stu6136tyt.helloserver.entity.UserInfo;
import com.stu6136tyt.helloserver.common.Result;
import com.stu6136tyt.helloserver.dto.UserDTO;
import com.stu6136tyt.helloserver.entity.User;
import com.stu6136tyt.helloserver.vo.UserDetailVO;

public interface UserService {
    Result<String> register(UserDTO userDTO);
    Result<String> login(UserDTO userDTO);
    Result<User> getUserById(Long id);
    Result<Object> getUserPage(Integer pageNum, Integer pageSize);

    Result<UserDetailVO> getUserDetail(Long userId);

    Result<String> deleteUser(Long userId);

    Result<String> updateUserInfo(UserInfo userInfo);
}