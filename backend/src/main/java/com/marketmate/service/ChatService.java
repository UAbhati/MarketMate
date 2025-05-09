package com.marketmate.service;

import com.marketmate.entity.ChatMessage;
import com.marketmate.entity.ChatSession;
import com.marketmate.repository.ChatMessageRepository;
import com.marketmate.repository.ChatSessionRepository;
import com.marketmate.util.ContextBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    private final ChatMessageRepository messageRepo;
    private final ChatSessionRepository sessionRepo;
    private final RateLimitService rateLimitService;
    private final UsageTracker usageTracker;
    private final FinancialDataService financialDataService;
    private final LLMService llmService;

    public ChatService(ChatMessageRepository messageRepo,
            ChatSessionRepository sessionRepo,
            RateLimitService rateLimitService,
            UsageTracker usageTracker,
            FinancialDataService financialDataService,
            LLMService llmService) {
        this.messageRepo = messageRepo;
        this.sessionRepo = sessionRepo;
        this.rateLimitService = rateLimitService;
        this.usageTracker = usageTracker;
        this.financialDataService = financialDataService;
        this.llmService = llmService;
    }

    public String handleMessage(String sessionId, String userId, String prompt, String model, String tier) {
        ChatSession session = sessionRepo.findById(Long.parseLong(sessionId))
                .orElseThrow(() -> new RuntimeException("Session not found"));
        if (!session.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }

        List<ChatMessage> history = messageRepo.findBySession_IdOrderByCreatedAtAsc(session.getId());

        List<ChatMessage> context = ContextBuilder.buildWindow(session, history);
        context.addAll(financialDataService.getContext(prompt));

        rateLimitService.checkLimits(userId, model, tier, prompt);

        String reply = llmService.ask(context, model);

        messageRepo.save(new ChatMessage(session, "user", prompt));
        messageRepo.save(new ChatMessage(session, "assistant", reply));

        usageTracker.recordUsage(userId, model, tier, prompt, reply);
        return reply;
    }
}
