package com.marketmate.controller;

import com.marketmate.service.ChatService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Operation(summary = "Send a message to the assistant", description = "Requires JWT. Returns assistant reply using context and financial data.")
    @ApiResponse(responseCode = "200", description = "Assistant reply returned")
    @PostMapping
    public String ask(@AuthenticationPrincipal UserDetails user,
            @RequestParam Long sessionId,
            @RequestParam String message,
            @RequestParam String model,
            @RequestParam String tier) {
        return chatService.handleMessage(String.valueOf(sessionId), user.getUsername(), message, model, tier);
    }
}
