package com.stu6136tyt.helloserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stu6136tyt.helloserver.common.Result;
import com.stu6136tyt.helloserver.common.ResultCode;
import com.stu6136tyt.helloserver.dto.UserDTO;
import com.stu6136tyt.helloserver.entity.User;
import com.stu6136tyt.helloserver.entity.UserInfo; // ✅ 修正：导入自定义的实体类
import com.stu6136tyt.helloserver.mapper.UserMapper;
import com.stu6136tyt.helloserver.mapper.UserInfoMapper; // ✅ 新增：注入 Mapper
import com.stu6136tyt.helloserver.security.JwtUtil;
import com.stu6136tyt.helloserver.service.UserService;
import com.stu6136tyt.helloserver.vo.UserDetailVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // ✅ 新增：事务支持

import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserInfoMapper userInfoMapper; // ✅ 注入 UserInfoMapper

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String USER_CACHE_PREFIX = "user:id:";
    private static final long CACHE_TTL = 30;

    @Override
    public Result<String> register(UserDTO userDTO) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, userDTO.getUsername());
        User dbUser = userMapper.selectOne(queryWrapper);

        if (dbUser != null) {
            return Result.error(ResultCode.USER_HAS_EXISTED);
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        userMapper.insert(user);

        return Result.success("注册成功！");
    }

    @Override
    public Result<String> login(UserDTO userDTO) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, userDTO.getUsername());
        User dbUser = userMapper.selectOne(queryWrapper);

        if (dbUser == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }

        if (!userDTO.getPassword().equals(dbUser.getPassword())) {
            return Result.error(ResultCode.PASSWORD_ERROR);
        }

        String jwt = jwtUtil.generateToken(userDTO.getUsername());
        return Result.success(jwt);
    }

    @Override
    public Result<User> getUserById(Long id) {
        String key = USER_CACHE_PREFIX + id;

        try {
            Object cacheObject = redisTemplate.opsForValue().get(key);
            if (cacheObject != null) {
                System.out.println("从 Redis 缓存中获取数据");
                return Result.success((User) cacheObject);
            }
        } catch (Exception e) {
            System.out.println("Redis 连接失败，直接查询数据库: " + e.getMessage());
        }

        System.out.println("Redis 未命中，查询数据库");
        User user = userMapper.selectById(id);

        if (user == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }

        try {
            redisTemplate.opsForValue().set(key, user, CACHE_TTL, TimeUnit.MINUTES);
        } catch (Exception e) {
            System.out.println("Redis 写入失败: " + e.getMessage());
        }
        return Result.success(user);
    }

    @Override
    public Result<Object> getUserPage(Integer pageNum, Integer pageSize) {
        Page<User> pageParam = new Page<>(pageNum, pageSize);
        Page<User> resultPage = userMapper.selectPage(pageParam, null);
        return Result.success(resultPage);
    }

    // ================== 重点修改区域 ==================

    /**
     * 更新用户扩展信息
     * 逻辑：根据 userId 判断是插入还是更新，并删除旧缓存
     */
    @Override
    @Transactional
    public Result<String> updateUserInfo(UserInfo userInfo) {
        if (userInfo.getUserId() == null) {
            return Result.error(ResultCode.USER_INFO_INVALID);
        }

        // 1. 判断数据库中是否存在该详情记录
        UserInfo dbInfo = userInfoMapper.selectById(userInfo.getUserId());

        if (dbInfo == null) {
            // 不存在则插入
            userInfoMapper.insert(userInfo);
        } else {
            // 存在则更新 (确保 ID 不被置空)
            userInfoMapper.updateById(userInfo);
        }

        // 2. 删除关联的用户缓存，保证数据一致性
        try {
            redisTemplate.delete(USER_CACHE_PREFIX + userInfo.getUserId());
        } catch (Exception e) {
            System.out.println("Redis 删除失败: " + e.getMessage());
        }

        return Result.success("更新成功");
    }

    /**
     * 查询用户详情 (多表联查 + Redis)
     */
    @Override
    public Result<UserDetailVO> getUserDetail(Long userId) {
        String key = "user:detail:" + userId;

        try {
            // 1. 先查 Redis
            Object cacheObject = redisTemplate.opsForValue().get(key);
            if (cacheObject != null) {
                System.out.println("详情：从 Redis 获取");
                return Result.success((UserDetailVO) cacheObject);
            }
        } catch (Exception e) {
            System.out.println("Redis 连接失败，直接查询数据库: " + e.getMessage());
        }

        // 2. Redis 没有，查数据库 (多表联查)
        System.out.println("详情：Redis 未命中，查询数据库");
        UserDetailVO vo = userInfoMapper.getUserDetail(userId);

        if (vo == null) {
            // 如果连基础用户都没有，返回错误
            User user = userMapper.selectById(userId);
            if (user == null) {
                return Result.error(ResultCode.USER_NOT_EXIST);
            }
            // 如果只有基础用户，没有详情，VO 可能为空，这里做个防御性处理或直接返回空 VO
            return Result.success(new UserDetailVO());
        }

        // 3. 写入 Redis
        redisTemplate.opsForValue().set(key, vo, CACHE_TTL, TimeUnit.MINUTES);
        return Result.success(vo);
    }

    /**
     * 删除用户 (逻辑删除或物理删除，这里演示物理删除并清理缓存)
     */
    @Override
    @Transactional
    public Result<String> deleteUser(Long userId) {
        // 1. 删除主表
        userMapper.deleteById(userId);
        // 2. 删除详情表
        userInfoMapper.delete(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getUserId, userId));

        // 3. 清理 Redis
        try {
            redisTemplate.delete(USER_CACHE_PREFIX + userId);
            redisTemplate.delete("user:detail:" + userId);
        } catch (Exception e) {
            System.out.println("Redis 删除失败: " + e.getMessage());
        }

        return Result.success("删除成功");
    }
}