package example.userdemo.chat.service;
import example.userdemo.chat.dto.MessageDTO;
import example.userdemo.chat.entity.Conversation;
import example.userdemo.chat.entity.Message;
import example.userdemo.chat.repository.ConversationRepository;
import example.userdemo.chat.repository.MessageRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;



@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageService(MessageRepository messageRepository, ConversationRepository conversationRepository, SimpMessagingTemplate messagingTemplate) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public MessageDTO createMessage(MessageDTO messageDTO) {
        System.out.println("Nhận tin nhắn: content=" + messageDTO.getContent() + ", conversationId=" + messageDTO.getConversationId());
        Conversation conversation = conversationRepository.findById(messageDTO.getConversationId())
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        System.out.println("Tìm thấy cuộc hội thoại: id=" + conversation.getId());

        Message messageEntity = new Message();
        messageEntity.setContent(messageDTO.getContent());
        messageEntity.setSender(messageDTO.getSender());
        messageEntity.setSentAt(LocalDateTime.now());
        messageEntity.setConversation(conversation);

        Message savedMessage = messageRepository.save(messageEntity);
        System.out.println("Đã lưu tin nhắn: id=" + savedMessage.getId());

        MessageDTO result = new MessageDTO();
        result.setId(savedMessage.getId());
        result.setContent(savedMessage.getContent());
        result.setSender(savedMessage.getSender());
        result.setSentAt(savedMessage.getSentAt());
        result.setConversationId(savedMessage.getConversation().getId());

        System.out.println("Gửi tin nhắn qua WebSocket đến /topic/conversation/" + messageDTO.getConversationId());
        messagingTemplate.convertAndSend("/topic/conversation/" + messageDTO.getConversationId(), result);
        return result;
    }

    public List<MessageDTO> getMessagesByConversationId(Long conversationId) {
        return messageRepository.findByConversationId(conversationId).stream().map(message -> {
            MessageDTO dto = new MessageDTO();
            dto.setId(message.getId());
            dto.setContent(message.getContent());
            dto.setSender(message.getSender());
            dto.setSentAt(message.getSentAt());
            dto.setConversationId(message.getConversation().getId());
            return dto;
        }).collect(Collectors.toList());
    }
}