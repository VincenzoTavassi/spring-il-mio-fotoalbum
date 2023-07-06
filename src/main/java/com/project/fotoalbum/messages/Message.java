package com.project.fotoalbum.messages;

public class Message {
    private final MessageType type;
    private final String message;

    public Message(MessageType type, String message) {
        this.type = type;
        this.message = message;
    }

    public MessageType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
