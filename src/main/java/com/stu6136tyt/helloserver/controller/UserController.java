package com.stu6136tyt.helloserver.controller;

import com.stu6136tyt.helloserver.common.Result;
import com.stu6136tyt.helloserver.dto.UserDTO;
import com.stu6136tyt.helloserver.entity.User;
import com.stu6136tyt.helloserver.entity.UserInfo;
import com.stu6136tyt.helloserver.service.UserService;
import com.stu6136tyt.helloserver.vo.UserDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 1. 新增用户（注册）
    @PostMapping
    public Result<String> register(@RequestBody UserDTO userDTO) {
        return userService.register(userDTO);
    }

    // 2. 用户登录
    @PostMapping("/login")
    public Result<String> login(@RequestBody UserDTO userDTO) {
        return userService.login(userDTO);
    }

    // 3. 根据 id 查询用户
    @GetMapping("/{id}")
    public Result<User> getUser(@PathVariable("id") Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/page")
    public Result<Object> getUserPage(
            // @RequestParam 设置默认值，防止前端不传参数时报错
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize) {
        return userService.getUserPage(pageNum, pageSize);
    }

    // 5. 查询用户详情 (多表联查 + Redis)
    @GetMapping("/{id}/detail")
    public Result<UserDetailVO> getUserDetail(@PathVariable("id") Long userId) {
        return userService.getUserDetail(userId);
    }

    // 6. 更新用户扩展信息
    @PutMapping("/{id}/detail")
    public Result<String> updateUserInfo(@PathVariable("id") Long userId,
                                         @RequestBody UserInfo userInfo) { // ✅ 现在是正确的 UserInfo
        userInfo.setUserId(userId);
        return userService.updateUserInfo(userInfo);
    }

    // 7. 删除用户
    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable("id") Long userId) {
        return userService.deleteUser(userId);
    }
}