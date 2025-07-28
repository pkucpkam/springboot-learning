package example.userdemo.chat.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class MessageDTO implements Serializable {
    private String id;
    private String content;
    private String sender;
    private String timestamp; // Đổi sang String để khớp với định dạng yêu cầu
    private String conversationId;
    private FileInfo file;

    // Constructors
    public MessageDTO() {
    }

    public MessageDTO(String id, String content, String sender, String timestamp, String conversationId, FileInfo file) {
        this.id = id;
        this.content = content;
        this.sender = sender;
        this.timestamp = timestamp;
        this.conversationId = conversationId;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public FileInfo getFile() {
        return file;
    }

    public void setFile(FileInfo file) {
        this.file = file;
    }

    // Nested FileInfo class
    public static class FileInfo implements Serializable {
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