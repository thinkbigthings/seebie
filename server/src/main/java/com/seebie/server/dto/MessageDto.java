package com.seebie.server.dto;

import com.seebie.server.entity.MessageType;
import jakarta.validation.constraints.NotNull;

public record MessageDto(@NotNull String content, MessageType type) {
}
