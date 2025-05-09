package com.marketmate.util;

import com.marketmate.entity.ChatMessage;
import com.marketmate.entity.ChatSession;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ContextBuilder {

    public static List<ChatMessage> buildWindow(ChatSession session, List<ChatMessage> history) {
        List<ChatMessage> context = new ArrayList<>();

        context.add(new ChatMessage(session, "system", "You are a helpful financial assistant."));

        if (session.getSummary() != null && !session.getSummary().isBlank()) {
            context.add(new ChatMessage(session, "system", "Session summary: " + session.getSummary()));
        }

        List<ChatMessage> filtered = history.stream()
                .filter(m -> "user".equals(m.getRole()) || "assistant".equals(m.getRole()))
                .sorted((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()))
                .collect(Collectors.toList());

        int fromIndex = Math.max(0, filtered.size() - 5);
        context.addAll(filtered.subList(fromIndex, filtered.size()));
        return context;
    }
}
