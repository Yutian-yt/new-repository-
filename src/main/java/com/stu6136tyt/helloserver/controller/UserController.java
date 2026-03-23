package com.stu6136tyt.helloserver.controller;
import com.stu6136tyt.helloserver.common.Result;
import com.stu6136tyt.helloserver.entity.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    // 1. 获取用户信息 (查) - 使用 @GetMapping 和 @PathVariable
    @GetMapping("/{id}")
    public Result<String> getUser(@PathVariable Long id) {
                String data= "查询成功，正在返回ID为" + id + "的用户信息";
                return Result.success(data);
    }

    // 2. 新增用户 (增) - 使用 @PostMapping 和 @RequestBody
    @PostMapping
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