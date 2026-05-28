package com.stu6136tyt.helloserver.controller;

import com.stu6136tyt.helloserver.common.Result;
import com.stu6136tyt.helloserver.model.dto.ChatRequestDTO;
import com.stu6136tyt.helloserver.model.vo.ChatResponseVO;
import com.stu6136tyt.helloserver.service.ChatService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public Result<ChatResponseVO> chat(@RequestBody ChatRequestDTO requestDTO) {
        ChatResponseVO responseVO = chatService.chat(requestDTO);
        return Result.success(responseVO);
    }
}