package com.stu6136tyt.helloserver.service.impl;

import com.stu6136tyt.helloserver.model.entity.ChatRecord;
import com.stu6136tyt.helloserver.service.SessionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存会话管理器（作为 Redis 的备用方案）
 * 使用 ConcurrentHashMap 存储会话记录
 */
@Service
public class InMemorySessionServiceImpl implements SessionService {

    /**
     * 会话存储
     * key: sessionId
     * value: 聊天记录列表（按时间顺序）
     */
    private final Map<String, List<ChatRecord>> sessionStore = new ConcurrentHashMap<>();

    /**
     * 保留最近对话轮数
     */
    private static final int MAX_HISTORY_ROUNDS = 3;

    @Override
    public void saveChatRecord(String sessionId, ChatRecord chatRecord) {
        sessionStore.compute(sessionId, (key, records) -> {
            if (records == null) {
                records = new ArrayList<>();
            }
            records.add(chatRecord);
            // 只保留最近3轮
            if (records.size() > MAX_HISTORY_ROUNDS) {
                return records.subList(records.size() - MAX_HISTORY_ROUNDS, records.size());
            }
            return records;
        });
    }

    @Override
    public List<ChatRecord> getChatRecords(String sessionId) {
        return sessionStore.getOrDefault(sessionId, new ArrayList<>());
    }

    @Override
    public void deleteSession(String sessionId) {
        sessionStore.remove(sessionId);
    }

    @Override
    public boolean existsSession(String sessionId) {
        return sessionStore.containsKey(sessionId);
    }
}