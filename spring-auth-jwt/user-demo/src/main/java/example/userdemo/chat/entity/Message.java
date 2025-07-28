package example.userdemo.chat.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Sử dụng UUID để tạo ID dạng chuỗi
    private String id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String sender;

    @Column(nullable = false)
    private LocalDateTime sentAt;

    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "url", column = @Column(name = "file_url")),
            @AttributeOverride(name = "name", column = @Column(name = "file_name")),
            @AttributeOverride(name = "type", column = @Column(name = "file_type"))
    })
    private FileInfo file;

    // Constructors
    public Message() {
    }

    public Message(String id, String content, String sender, LocalDateTime sentAt, Conversation conversation, FileInfo file) {
        this.id = id;
        this.content = content;
        this.sender = sender;
        this.sentAt = sentAt;
        this.conversation = conversation;
        this.file = file;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public FileInfo getFile() {
        return file;
    }

    public void setFile(FileInfo file) {
        this.file = file;
    }

    // Embedded FileInfo class
    @Embeddable
    public static class FileInfo {
        private String url;
        private String name;
        private String type;

        // Constructors
        public FileInfo() {
        }

        public FileInfo(String url, String name, String type) {
            this.url = url;
            this.name = name;
            this.type = type;
        }

        // Getters and Setters
        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}