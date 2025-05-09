// src/main/java/com/marketmate/service/ChatService.java
package com.marketmate.service;

import com.marketmate.entity.ChatMessage;
import com.marketmate.entity.ChatSession;
import com.marketmate.model.APIResponse;
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
    private final FinancialDataService financialDataService;
    private final LLMService llmService;
    private final UsageRecordService usageTracker;

    public ChatService(ChatMessageRepository messageRepo,
            ChatSessionRepository sessionRepo,
            RateLimitService rateLimitService,
            FinancialDataService financialDataService,
            LLMService llmService,
            UsageRecordService usageTracker) {
        this.messageRepo = messageRepo;
        this.sessionRepo = sessionRepo;
        this.rateLimitService = rateLimitService;
        this.financialDataService = financialDataService;
        this.llmService = llmService;
        this.usageTracker = usageTracker;
    }

    /**
     * Handles an incoming user message, injects system & financial context,
     * enforces rate‐limits, calls the LLM, persists both user & assistant
     * messages and records usage.
     */
    public String handleMessage(UUID sessionId,
            String userId,
            String prompt,
            String model,
            String tier) {

        // 1) load & authorize session
        ChatSession session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown session"));
        if (!session.getUserId().equals(userId)) {
            throw new SecurityException("Not your session");
        }

        // 2) rate limit
        rateLimitService.checkLimits(userId, model, tier, prompt);

        // 3) history → system window
        List<ChatMessage> history = messageRepo.findBySession_IdOrderByCreatedAtAsc(sessionId);
        List<ChatMessage> context = ContextBuilder.buildWindow(session, history);

        // 4) add financial‐data context
        context.addAll(financialDataService.getContext(prompt));

        // 5) call LLM
        APIResponse llmResp = llmService.ask(context, model);

        String answer = llmResp.getMessage().getContent();

        // 6) persist the two messages
        messageRepo.save(new ChatMessage(session, "user", prompt));
        messageRepo.save(new ChatMessage(session, "assistant", answer));

        // 7) record usage
        usageTracker.recordUsage(
                userId,
                model,
                tier,
                llmResp.getPromptTokens(),
                llmResp.getCompletionTokens());

        return answer;
    }
}
