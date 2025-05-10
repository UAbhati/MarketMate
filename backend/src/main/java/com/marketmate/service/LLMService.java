package com.marketmate.service;

import com.marketmate.entity.ChatMessage;
import com.marketmate.entity.ChatSession;
import com.marketmate.model.APIResponse;

import org.json.JSONArray; // for parsing Hugging Face JSON response
import org.json.JSONObject; // for constructing request payload
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*; // for headers, HTTP entity, response types
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate; // for making HTTP POST requests

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class LLMService {
    @Value("${openrouter.api.token}")
    private String openRouterToken;

    private final String MODEL_URL = "https://openrouter.ai/api/v1/chat/completions";

    /**
     * Mocks an LLM call by echoing back the last user message
     * wrapped in an APIResponse.
     *
     * @param context the conversation so far (including system/user messages)
     * @param model   the name of the model to invoke
     * @param useRealLLM for integrating hugging face api
     * @return a fully‐populated APIResponse
     */
    public APIResponse askLLM(
        List<ChatMessage> context, 
        String model,
        Boolean useRealLLM
    ) {
        // pull the session out of one of your context messages
        ChatSession session = context.get(0).getSession();
        // the user prompt was the last item in context
        ChatMessage lastUser = context.get(context.size() - 1);
        // Mock reply
        if (!useRealLLM) {
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
        
        // Real openroute LLM call
        // Build OpenAI-style message array
        JSONArray messagesArray = new JSONArray();
        // messagesArray.put(new JSONObject()
        //         .put("role", "system")
        //         .put("content", "You are a helpful assistant specialized in financial market questions."));

        for (ChatMessage msg : context) {
            JSONObject msgJson = new JSONObject();
            msgJson.put("role", msg.getRole());
            msgJson.put("content", msg.getContent());
            messagesArray.put(msgJson);
        }

        String responseText = "LLM call failed.";
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + openRouterToken); // pulled from application.yml

            JSONObject requestJson = new JSONObject();
            requestJson.put("model", "deepseek/deepseek-chat-v3-0324:free");
            requestJson.put("messages", messagesArray);
            requestJson.put("stream", false);

            HttpEntity<String> entity = new HttpEntity<>(requestJson.toString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(MODEL_URL, entity, String.class);

            System.out.println("🧠 OpenRouter response code: " + response.getStatusCode());
            System.out.println("📨 OpenRouter request body: " + entity);

            if (response.getStatusCode().is2xxSuccessful()) {
                JSONObject result = new JSONObject(response.getBody());
                responseText = result.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");
            } else {
                responseText = "⚠️ LLM API call failed: " + response.getStatusCode();
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseText = "💥 Exception during LLM call: " + e.getMessage();
        }

        // Wrap the response in your internal model
        ChatMessage aiMessage = new ChatMessage(session, "assistant", responseText);
        APIResponse resp = new APIResponse();
        resp.setId(UUID.randomUUID().toString());
        resp.setPromptTokens(calculatePromptTokens(context));
        resp.setCompletionTokens(calculateCompletionTokens(responseText));
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
