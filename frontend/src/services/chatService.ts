import api from "../axios";

/**
 * Sends a chat message to the backend.
 * 
 * @param sessionId - UUID of the chat session
 * @param message - user input message
 * @param model - LLM model (e.g. "gpt-4o")
 * @param tier - subscription tier (e.g. "FREE", "TIER_1")
 * @param useRealLLM - whether to use real LLM or mock
 * @returns response message from assistant
 */
export async function sendMessage(sessionId: string, message: string, model: string, tier: string, useRealLLM: boolean) {
    const response = await api.post("/api/chat", null, {
        params: {
        sessionId,
        message,
        model,
        tier,
        useRealLLM,
        },
    });
    return response.data;
}
