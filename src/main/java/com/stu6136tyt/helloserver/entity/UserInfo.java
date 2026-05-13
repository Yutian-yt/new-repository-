package com.stu6136tyt.helloserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户详情实体类
 * 对应数据库表：user_info
 */
@Data
@TableName("user_info") // 绑定数据库表名
public class UserInfo {

    /**
     * 用户ID，也是主键，对应 sys_user 表的 id
     */
    @TableId(type = IdType.INPUT) // 注意：这里通常用 INPUT，因为ID是由 sys_user 生成的，不是自增
    private Long userId;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 性别 (0: 男, 1: 女)
     */
    private Integer gender;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 家庭住址
     */
    private String address;
}