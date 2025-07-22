package example.userdemo.chat.service;

import example.userdemo.chat.dto.ConversationDTO;
import example.userdemo.chat.entity.Conversation;
import example.userdemo.chat.repository.ConversationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;



@Service
public class ConversationService {
    private final ConversationRepository conversationRepository;

    public ConversationService(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    public ConversationDTO createConversation(ConversationDTO conversationDTO) {
        Conversation conversation = new Conversation();
        conversation.setName(conversationDTO.getName());
        conversation.setCreatedAt(LocalDateTime.now());
        Conversation savedConversation = conversationRepository.save(conversation);

        ConversationDTO result = new ConversationDTO();
        result.setId(savedConversation.getId());
        result.setName(savedConversation.getName());
        result.setCreatedAt(savedConversation.getCreatedAt());
        return result;
    }

    public List<ConversationDTO> getAllConversations() {
        return conversationRepository.findAll().stream().map(conversation -> {
            ConversationDTO dto = new ConversationDTO();
            dto.setId(conversation.getId());
            dto.setName(conversation.getName());
            dto.setCreatedAt(conversation.getCreatedAt());
            return dto;
        }).collect(Collectors.toList());
    }


}