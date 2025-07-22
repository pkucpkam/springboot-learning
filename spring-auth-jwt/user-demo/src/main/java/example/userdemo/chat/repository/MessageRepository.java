package example.userdemo.chat.repository;

import example.userdemo.chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    public List<Message> findByConversationId(Long conversationId);
}
