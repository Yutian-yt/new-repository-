
package com.stu6136tyt.helloserver. dto;
public class
UserDTO {
    private String username;
    private String password;

    // 必须提供 Getter 和 Setter，否则 Spring 无法解析 JSON
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}