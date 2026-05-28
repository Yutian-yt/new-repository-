package com.stu6136tyt.helloserver.service.impl;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.stu6136tyt.helloserver.model.dto.ChatRequestDTO;
import com.stu6136tyt.helloserver.model.entity.ChatRecord;
import com.stu6136tyt.helloserver.model.vo.ChatResponseVO;
import com.stu6136tyt.helloserver.service.ChatService;
import com.stu6136tyt.helloserver.service.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatServiceImpl.class);

    private final ChatClient chatClient;
    private final SessionService sessionService;

    public ChatServiceImpl(ChatClient.Builder chatClientBuilder,
                           SessionService sessionService) {
        this.chatClient = chatClientBuilder
                .defaultSystem("你是一名专业、友好、简洁的中文智能助手，请结合历史对话上下文，准确回答用户的问题。")
                .defaultOptions(
                        DashScopeChatOptions.builder()
                                .withTopP(0.7)
                                .build()
                )
                .build();
        this.sessionService = sessionService;
    }

    @Override
    public ChatResponseVO chat(ChatRequestDTO requestDTO) {
        String sessionId = requestDTO.getSessionId();
        String message = requestDTO.getMessage();

        // 如果没有 sessionId，直接进行单轮聊天
        if (sessionId == null || sessionId.isEmpty()) {
            String answer = chatClient.prompt(message)
                    .call()
                    .content();
            return new ChatResponseVO(message, answer);
        }

        // 1. 读取历史消息
        List<ChatRecord> historyRecords = sessionService.getChatRecords(sessionId);
        String historyText = "";

        if (!historyRecords.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ChatRecord record : historyRecords) {
                sb.append("用户：").append(record.getUserMessage()).append("\n");
                sb.append("助手：").append(record.getAssistantMessage()).append("\n");
            }
            historyText = sb.toString().trim();
        }

        // 2. 拼接上下文
        String finalPrompt = """
                以下是历史对话：
                %s
                
                当前用户问题：
                %s
                """.formatted(historyText, message);

        // 3. 调用模型
        String answer = chatClient.prompt(finalPrompt)
                .call()
                .content();

        // 4. 保存本轮记录
        ChatRecord newRecord = new ChatRecord();
        newRecord.setSessionId(sessionId);
        newRecord.setUserMessage(message);
        newRecord.setAssistantMessage(answer);
        newRecord.setCreateTime(LocalDateTime.now());
        sessionService.saveChatRecord(sessionId, newRecord);

        return new ChatResponseVO(message, answer);
    }
}