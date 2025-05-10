package com.marketmate.service;

import com.marketmate.entity.ChatMessage;
import com.marketmate.entity.ChatSession;
import com.marketmate.model.APIResponse;
import com.marketmate.repository.ChatMessageRepository;
import com.marketmate.repository.ChatSessionRepository;
import com.marketmate.util.ContextBuilder;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Async
    public void saveMessagesAsync(ChatSession session, String userText, String aiText) {
        // Save user message
        ChatMessage userMsg = new ChatMessage(session, "user", userText);
        messageRepo.save(userMsg);
        // Save AI reply message
        ChatMessage aiMsg = new ChatMessage(session, "assistant", aiText);
        messageRepo.save(aiMsg);
    }

    /**
     * Builds full message context and calls the LLM, returning APIResponse (with
     * token counts).
     * Does NOT save messages or usage.
     */
    public APIResponse buildContextAndCallLLM(
        UUID sessionId, 
        String userId, 
        String prompt, 
        String model,
        Boolean useRealLLM
    ) {
        ChatSession session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown session"));

        if (!session.getUserId().equals(userId)) {
            throw new SecurityException("Not your session");
        }

        // 1. Fetch chat history
        List<ChatMessage> history = messageRepo.findBySession_IdOrderByCreatedAtAsc(sessionId);
        List<ChatMessage> context = ContextBuilder.buildWindow(session, history);

        // 2. Get financial context
        List<ChatMessage> financialContext = financialDataService.getContext(prompt);
        if (!financialContext.isEmpty()) {
            ChatMessage combined = new ChatMessage(session, "assistant",
                    financialContext.stream()
                            .map(ChatMessage::getContent)
                            .reduce("", (a, b) -> a + "\n" + b).trim());
            return new APIResponse(combined, 0, 0);
        }

        // 3. Fall back to LLM
        context.addAll(financialContext);
        context.add(new ChatMessage(session, "user", prompt));
        APIResponse aiResp = llmService.askLLM(context, model, useRealLLM);
        return aiResp;
    }

    /**
     * Persists user and assistant messages, and records usage statistics.
     */
    @Transactional
    public void saveMessagesAndTrack(
            ChatSession session,
            String userText,
            String aiText,
            String userId,
            String model,
            String tier,
            int promptTokens,
            int completionTokens) {

        // Save user message
        ChatMessage userMsg = new ChatMessage(session, "user", userText);
        messageRepo.save(userMsg);

        // Save assistant message
        ChatMessage aiMsg = new ChatMessage(session, "assistant", aiText);
        messageRepo.save(aiMsg);

        // Record token usage
        usageTracker.recordUsage(userId, model, tier, promptTokens, completionTokens);
    }
}
