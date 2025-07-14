package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import generator.domain.ChatMessage;
import generator.service.ChatMessageService;
import generator.mapper.ChatMessageMapper;
import org.springframework.stereotype.Service;

/**
* @author lenovo
* @description 针对表【chat_message(聊天消息表)】的数据库操作Service实现
* @createDate 2025-07-14 20:57:18
*/
@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage>
    implements ChatMessageService{

}




