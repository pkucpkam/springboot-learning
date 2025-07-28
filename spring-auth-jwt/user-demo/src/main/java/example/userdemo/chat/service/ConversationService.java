package example.userdemo.chat.service;

import example.userdemo.chat.dto.ConversationDTO;
import example.userdemo.chat.dto.MessageDTO;
import example.userdemo.chat.entity.Conversation;
import example.userdemo.chat.repository.ConversationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConversationService {
    private final ConversationRepository conversationRepository;
    private final MessageService messageService;

    public ConversationService(ConversationRepository conversationRepository, MessageService messageService) {
        this.conversationRepository = conversationRepository;
        this.messageService = messageService;
    }

    public List<ConversationDTO> getAllConversations() {
        return conversationRepository.findAll().stream().map(conversation -> {
            ConversationDTO dto = new ConversationDTO();
            dto.setId(conversation.getId());
            dto.setName(conversation.getName());
            dto.setCreatedAt(conversation.getCreatedAt());
            dto.setAvatar(conversation.getAvatar());

            // Lấy lastMessage từ tin nhắn mới nhất
            List<MessageDTO> messages = messageService.getMessagesByConversationId(conversation.getId());
            if (!messages.isEmpty()) {
                MessageDTO lastMessage = messages.get(messages.size() - 1);
                dto.setLastMessage(lastMessage.getContent());
                dto.setMessages(messages);
            }

            return dto;
        }).collect(Collectors.toList());
    }

    public ConversationDTO createConversation(ConversationDTO conversationDTO) {
        try {
            System.out.println("Tạo cuộc hội thoại: name=" + conversationDTO.getName());
            Conversation conversation = new Conversation();
            conversation.setName(conversationDTO.getName());
            conversation.setCreatedAt(LocalDateTime.now());
            conversation.setAvatar(conversationDTO.getAvatar());

            Conversation savedConversation = conversationRepository.save(conversation);
            System.out.println("Đã lưu cuộc hội thoại: id=" + savedConversation.getId());

            ConversationDTO result = new ConversationDTO();
            result.setId(savedConversation.getId());
            result.setName(savedConversation.getName());
            result.setCreatedAt(savedConversation.getCreatedAt());
            result.setAvatar(savedConversation.getAvatar());
            // lastMessage và messages sẽ được cập nhật khi có tin nhắn
            result.setLastMessage(null);
            result.setMessages(List.of());

            return result;
        } catch (Exception e) {
            System.err.println("Lỗi khi tạo cuộc hội thoại: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}