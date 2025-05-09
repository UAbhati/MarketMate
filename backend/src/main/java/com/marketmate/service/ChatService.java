package com.marketmate.service;

import com.marketmate.entity.ChatMessage;
import com.marketmate.entity.ChatSession;
import com.marketmate.repository.ChatMessageRepository;
import com.marketmate.repository.ChatSessionRepository;
import com.marketmate.util.ContextBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ChatService {

    private final ChatMessageRepository messageRepo;
    private final ChatSessionRepository sessionRepo;
    private final RateLimitService rateLimitService;
    private final UsageRecordService usageRecordService;
    private final FinancialDataService financialDataService;
    private final LLMService llmService;

    public ChatService(ChatMessageRepository messageRepo,
            ChatSessionRepository sessionRepo,
            RateLimitService rateLimitService,
            UsageRecordService usageRecordService,
            FinancialDataService financialDataService,
            LLMService llmService) {
        this.messageRepo = messageRepo;
        this.sessionRepo = sessionRepo;
        this.rateLimitService = rateLimitService;
        this.usageRecordService = usageRecordService;
        this.financialDataService = financialDataService;
        this.llmService = llmService;
    }

    public String handleMessage(String sessionId, String userId, String prompt, String model, String tier) {
        UUID uuid = UUID.fromString(sessionId);
        ChatSession session = sessionRepo.findById(uuid)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        if (!session.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }

        List<ChatMessage> history = messageRepo.findBySession_IdOrderByCreatedAtAsc(session.getId());

        List<ChatMessage> context = ContextBuilder.buildWindow(session, history);
        context.addAll(financialDataService.getContext(prompt));

        rateLimitService.checkLimits(userId, model, tier, prompt);

        String llmResponse = llmService.ask(context, model);

        messageRepo.save(new ChatMessage(session, "user", prompt));
        messageRepo.save(new ChatMessage(session, "assistant", llmResponse));

        usageRecordService.recordUsage(userId,
                model,
                tier,
                0,
                0);
        return llmResponse;
    }
}
