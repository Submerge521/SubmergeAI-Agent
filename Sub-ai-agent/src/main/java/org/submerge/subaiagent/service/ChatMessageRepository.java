package org.submerge.subaiagent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.submerge.subaiagent.entity.ChatMessage;
import org.submerge.subaiagent.repository.ChatMessageMapper;

@Component
public class ChatMessageRepository extends CrudRepository<ChatMessageMapper, ChatMessage> {
    public void remove(LambdaQueryWrapper<generator.domain.ChatMessage> queryWrapper) {
    }
}
