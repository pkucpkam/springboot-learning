package example.userdemo.chat.dto;


import java.time.LocalDateTime;
import java.util.List;

public class ConversationDTO {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private List<MessageDTO> messages;

    // Constructors
    public ConversationDTO() {
    }

    public ConversationDTO(Long id, String name, LocalDateTime createdAt, List<MessageDTO> messages) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.messages = messages;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<MessageDTO> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageDTO> messages) {
        this.messages = messages;
    }
}