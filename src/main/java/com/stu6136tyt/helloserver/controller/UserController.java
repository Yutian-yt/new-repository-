package com.stu6136tyt.helloserver.controller;

import com.stu6136tyt.helloserver.common.Result;
import com.stu6136tyt.helloserver.dto.UserDTO; // 1. 导入 DTO
import com.stu6136tyt.helloserver.entity.User;
import com.stu6136tyt.helloserver.service.UserService; // 2. 导入 Service
import org.springframework.beans.factory.annotation.Autowired; // 3. 导入注解
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    // --- 新增代码：注入 Service ---
    @Autowired
    private UserService userService;
    // --- 新增代码结束 ---

    // 1. 获取用户信息 (查) - 使用 @GetMapping 和 @PathVariable
    @GetMapping("/{id}")
    public Result<String> getUser(@PathVariable Long id) {
        String data = "查询成功，正在返回ID为" + id + "的用户信息";
        return Result.success(data);
    }

    // --- 新增代码：用户注册 (Register) ---
    // 路径: POST /api/users
    @PostMapping
    public Result<String> register(@RequestBody UserDTO userDTO) {
        // 直接调用 Service 层的 register 方法
        return userService.register(userDTO);
    }
    // --- 新增代码结束 ---

    // --- 新增代码：用户登录 (Login) ---
    // 路径: POST /api/users/login
    @PostMapping("/login")
    public Result<String> login(@RequestBody UserDTO userDTO) {
        // 直接调用 Service 层的 login 方法
        return userService.login(userDTO);
    }
    // --- 新增代码结束 ---

    // 2. 新增用户 (增) - 使用 @PostMapping 和 @RequestBody
    // 注意：这里可能会和上面的 register 产生路径冲突 (都是 POST /api/users)
    // 在实际开发中，通常 register 就是 create，或者 create 用于管理员后台。
    // 如果测试时有冲突，请暂时注释掉这个方法，或者修改路径为 /admin。
    @PostMapping("/admin") // 建议修改路径以避免冲突，或者暂时注释
    public String createUser(@RequestBody User user) {
        return "新增成功，接收到用户：" + user.getName() + "，年龄：" + user.getAge();
    }

    // 3. 全量更新用户信息 (改) - 使用 @PutMapping
    @PutMapping("/{id}")
    public String updateUser(@PathVariable Long id, @RequestBody User user) {
        return "更新成功，ID " + id + " 的用户已修改为：" + user.getName();
    }

    // 4. 删除用户 (删) - 使用 @DeleteMapping
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        return "删除成功，已移除ID为" + id + "的用户";
    }
}