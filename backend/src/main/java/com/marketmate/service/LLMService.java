package com.marketmate.service;

import com.marketmate.entity.ChatMessage;
import com.marketmate.entity.ChatSession;
import com.marketmate.model.APIResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class LLMService {

    /**
     * Mocks an LLM call by echoing back the last user message
     * wrapped in an APIResponse.
     *
     * @param context the conversation so far (including system/user messages)
     * @param model   the name of the model to invoke
     * @return a fully‐populated APIResponse
     */
    public APIResponse askLLM(List<ChatMessage> context, String model) {
        // pull the session out of one of your context messages
        ChatSession session = context.get(0).getSession();
        // the user prompt was the last item in context
        ChatMessage lastUser = context.get(context.size() - 1);

        // build your assistant reply
        String reply = "🧠 Insight for: \"" + lastUser.getContent() + "\"";
        ChatMessage aiMessage = new ChatMessage(session, "assistant", reply);

        // now build your APIResponse
        APIResponse resp = new APIResponse();
        resp.setId(UUID.randomUUID().toString());
        resp.setPromptTokens(calculatePromptTokens(context));
        resp.setCompletionTokens(calculateCompletionTokens(reply));
        resp.setCreated(Instant.now());
        resp.setMessage(aiMessage);
        resp.setFinishReason("STOP");
        return resp;
    }

    // simple stub: count words as a proxy for token count
    private int calculatePromptTokens(List<ChatMessage> context) {
        return context.stream()
                .mapToInt(m -> m.getContent().split("\\s+").length)
                .sum();
    }

    // simple stub: count words in the reply
    private int calculateCompletionTokens(String reply) {
        return reply.split("\\s+").length;
    }
}
