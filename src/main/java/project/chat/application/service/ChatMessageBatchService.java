package project.chat.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import project.member.domain.exception.MemberExceptions;
import project.chat.adapter.in.websocket.response.ChatMessageResponse;
import project.chat.domain.ChatMessage;
import project.chat.domain.ChatRoom;
import project.member.domain.Member;
import project.chat.adapter.out.persistence.ChatRepositoryFacadeManager;
import project.member.adapter.out.persistence.MemberRepository;

import java.util.List;

@Slf4j
@Component
public class ChatMessageBatchService {

    private final ObjectMapper redisObjMapper;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChatRepositoryFacadeManager chatRepositoryFacade;

    public ChatMessageBatchService(@Qualifier("redisObjMapper") ObjectMapper redisObjMapper,
                                   MemberRepository memberRepository,
                                   RedisTemplate<String, Object> redisTemplate,
                                   ChatRepositoryFacadeManager chatRepositoryFacade) {
        this.redisObjMapper = redisObjMapper;
        this.memberRepository = memberRepository;
        this.redisTemplate = redisTemplate;
        this.chatRepositoryFacade = chatRepositoryFacade;
    }

    @Scheduled(fixedDelay = 30000)
    @Transactional
    public void flushMessagesToDB() {
        String queueKey = "chat:queue";
        String backupKey = "chat:queue:backup";

        if (!redisTemplate.hasKey(queueKey)) return;

        redisTemplate.rename(queueKey, backupKey);

        List<Object> rawMessages = redisTemplate.opsForList().range(backupKey, 0, -1);
        if (rawMessages == null || rawMessages.isEmpty()) return;

        List<ChatMessage> entities = rawMessages.stream()
                                                .map(obj -> redisObjMapper.convertValue(obj, ChatMessageResponse.class))
                                                .map(dto -> {
                                                    ChatRoom room = chatRepositoryFacade.getChatRoomByRoomId(dto.getRoomId());
                                                    Member writer = memberRepository.findById(dto.getSenderId())
                                                                                    .orElseThrow(() -> MemberExceptions.notFoundById(dto.getSenderId()));
                                                    return ChatMessage.create(room, writer, dto.getContent());
                                                })
                                                .toList();
        if (!entities.isEmpty()) {
            chatRepositoryFacade.saveAllChatMessages(entities);
            redisTemplate.delete(backupKey);
        }
    }
}
