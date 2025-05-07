package com.marketmate.model;

import java.util.List;

public class APIRequest {
    private String provider;
    private String modelName;
    private double temperature;
    private int numCompletions;
    private List<ChatMessage> messages;

    // Getters and Setters
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public int getNumCompletions() { return numCompletions; }
    public void setNumCompletions(int numCompletions) { this.numCompletions = numCompletions; }

    public List<ChatMessage> getMessages() { return messages; }
    public void setMessages(List<ChatMessage> messages) { this.messages = messages; }
}
