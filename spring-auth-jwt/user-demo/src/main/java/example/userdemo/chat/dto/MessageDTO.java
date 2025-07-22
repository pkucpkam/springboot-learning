package example.userdemo.chat.dto;

import java.time.LocalDateTime;

public class MessageDTO {
    private Long id;
    private String content;
    private String sender;
    private LocalDateTime sentAt;
    private Long conversationId;
    private String receiver;

    // Constructors
    public MessageDTO() {
    }

    public MessageDTO(Long id, String content, String sender, LocalDateTime sentAt, Long conversationId) {
        this.id = id;
        this.content = content;
        this.sender = sender;
        this.sentAt = sentAt;
        this.conversationId = conversationId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}