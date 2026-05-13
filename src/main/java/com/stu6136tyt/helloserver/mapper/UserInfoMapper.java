package com.stu6136tyt.helloserver.mapper;


import com.stu6136tyt.helloserver.entity.UserInfo;
import com.stu6136tyt.helloserver.vo.UserDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    /**
     * 自定义 SQL：联查用户基础信息和扩展信息
     * @param userId 用户ID
     * @return UserDetailVO
     */
    @Select("""
        SELECT
            u.id AS userId,
            u.username,
            i.real_name AS realName,
            i.phone,
            i.address
        FROM sys_user u
        LEFT JOIN user_info i ON u.id = i.user_id
        WHERE u.id = #{userId}
        """)
    UserDetailVO getUserDetail(@Param("userId") Long userId);
}
