package com.stu6136tyt.helloserver.service;

import com.stu6136tyt.helloserver.model.dto.ChatRequestDTO;
import com.stu6136tyt.helloserver.model.vo.ChatResponseVO;

public interface ChatService {
    ChatResponseVO chat(ChatRequestDTO requestDTO);
}