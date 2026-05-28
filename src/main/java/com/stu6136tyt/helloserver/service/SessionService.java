package com.stu6136tyt.helloserver.service;

import com.stu6136tyt.helloserver.model.entity.ChatRecord;

import java.util.List;

/**
 * 会话管理服务接口
 */
public interface SessionService {

    /**
     * 保存聊天记录到会话
     *
     * @param sessionId      会话ID
     * @param chatRecord     聊天记录
     */
    void saveChatRecord(String sessionId, ChatRecord chatRecord);

    /**
     * 获取会话的所有聊天记录
     *
     * @param sessionId      会话ID
     * @return               聊天记录列表
     */
    List<ChatRecord> getChatRecords(String sessionId);

    /**
     * 删除会话
     *
     * @param sessionId      会话ID
     */
    void deleteSession(String sessionId);

    /**
     * 检查会话是否存在
     *
     * @param sessionId      会话ID
     * @return               是否存在
     */
    boolean existsSession(String sessionId);
}