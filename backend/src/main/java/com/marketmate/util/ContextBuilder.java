package com.marketmate.util;

import com.marketmate.entity.ChatMessage;
import com.marketmate.entity.ChatSession;

import java.util.ArrayList;
import java.util.List;

public class ContextBuilder {

    public static List<ChatMessage> buildWindow(ChatSession session, List<ChatMessage> history) {
        List<ChatMessage> context = new ArrayList<>();
        // context.add(new ChatMessage(session, "system", "You are a helpful financial assistant."));

        // if (session.getSummary() != null && !session.getSummary().isEmpty()) {
        //     context.add(new ChatMessage(session, "system", "Session Summary: " + session.getSummary()));
        // }

        // long count = history.stream().filter(m ->
        //         m.getRole().equals("user") || m.getRole().equals("assistant")).count();

        // context.addAll(history.stream()
        //         .filter(m -> m.getRole().equals("user") || m.getRole().equals("assistant"))
        //         .skip(Math.max(0, count - 10))
        //         .toList());

        return context;
    }
}
