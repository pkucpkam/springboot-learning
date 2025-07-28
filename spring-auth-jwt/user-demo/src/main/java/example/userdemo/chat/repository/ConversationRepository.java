package example.userdemo.chat.repository;

import example.userdemo.chat.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, String> {

}
