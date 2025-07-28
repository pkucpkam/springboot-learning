package example.userdemo.chat.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ConversationDTO {
    private String id;
    private String name;
    private String lastMessage;
    private String avatar;
    private LocalDateTime createdAt;
    private List<MessageDTO> messages;

    // Constructors
    public ConversationDTO() {
    }

    public ConversationDTO(String id, String name, String lastMessage, String avatar, LocalDateTime createdAt, List<MessageDTO> messages) {
        this.id = id;
        this.name = name;
        this.lastMessage = lastMessage;
        this.avatar = avatar;
        this.createdAt = createdAt;
        this.messages = messages;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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