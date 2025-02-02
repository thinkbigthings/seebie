package com.seebie.server.dto;

import com.seebie.server.entity.MessageType;

public record MessageDto(String content, MessageType messageType) {
}
