package com.stu6136tyt.helloserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stu6136tyt.helloserver.common.Result;
import com.stu6136tyt.helloserver.common.ResultCode;
import com.stu6136tyt.helloserver.dto.UserDTO;
import com.stu6136tyt.helloserver.entity.User;
import com.stu6136tyt.helloserver.mapper.UserMapper;
import com.stu6136tyt.helloserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result<String> register(UserDTO userDTO) {
        // 1. 查询该用户名是否已存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, userDTO.getUsername());
        User dbUser = userMapper.selectOne(queryWrapper);

        if (dbUser != null) {
            return Result.error(ResultCode.USER_HAS_EXISTED);
        }

        // 2. 组装实体对象
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());

        // 3. 插入数据库
        userMapper.insert(user);

        return Result.success("注册成功！");
    }

    @Override
    public Result<String> login(UserDTO userDTO) {
        // 1. 根据用户名查询数据库
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, userDTO.getUsername());
        User dbUser = userMapper.selectOne(queryWrapper);

        // 2. 校验用户是否存在
        if (dbUser == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }

        // （图片截断，以下为常规补全逻辑，可按需修改）
        // 3. 校验密码
        if (!userDTO.getPassword().equals(dbUser.getPassword())) {
            return Result.error(ResultCode.PASSWORD_ERROR);
        }

        // 4. 登录成功，返回token（示例）
        return Result.success("登录成功！");
    }

    @Override
    public Result<String> getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }
        return Result.success("查询成功，用户信息：" + user.toString());
    }

    @Override
    public Result<Object> getUserPage(Integer pageNum, Integer pageSize) {
        // 1. 创建分页对象 (当前页码, 每页显示条数)
        Page<User> pageParam = new Page<>(pageNum, pageSize);

        // 2. 执行分页查询
        // 参数1: 分页对象     参数2: 查询条件 (Wrapper), 这里传 null 代表无额外条件
        // 框架会自动执行 COUNT 语句查总数，再拼接 LIMIT 执行分页
        Page<User> resultPage = userMapper.selectPage(pageParam, null);

        // 3. 返回结果
        return Result.success(resultPage);
    }
}