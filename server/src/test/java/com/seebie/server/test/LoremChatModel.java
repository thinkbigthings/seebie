package com.seebie.server.test;

import net.datafaker.Faker;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;

import java.util.List;
import java.util.Random;

public class LoremChatModel implements ChatModel {

    private static final Random random = new Random();
    private final Faker faker = new Faker();

    @Override
    public ChatResponse call(Prompt prompt) {

        // return 1 to 3 sentences
        int numSentences = 1 + random.nextInt(4);
        var message = new AssistantMessage(faker.lorem().sentence(numSentences));
        return new ChatResponse(List.of(new Generation(message)));
    }
}
