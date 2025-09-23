package com.demo.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class);
	}

    @RestController
    static class ChatController {

        private final ChatClient chatClient;

        private final ToolCallbackProvider tools;

        ChatController(ChatClient.Builder chatClientBuilder, ToolCallbackProvider tools) {
            chatClient = chatClientBuilder
                    .defaultSystem("You are useful assistant.") // Set the system prompt
                    // .defaultToolCallbacks(tools) // defer to LLM call since tools/list result may be different per user
                    .defaultAdvisors(new SimpleLoggerAdvisor())
                    .build();
            this.tools = tools;
        }

        @GetMapping("/chat")
        String chat(@RequestParam String message) {
            return chatClient.prompt(message).toolCallbacks(tools).call().content();
        }
    }
}
