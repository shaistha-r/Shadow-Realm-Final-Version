package com.shadowrealm.backend.dto;

public class SecretResponse {
    private String type;
    private String value;
    private int round;

    public SecretResponse() {}

    public SecretResponse(String type, String value, int round) {
        this.type = type;
        this.value = value;
        this.round = round;
    }

    public String getType() { return type; }
    public String getValue() { return value; }
    public int getRound() { return round; }
}
