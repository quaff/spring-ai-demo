package com.demo.ai;

import java.util.Scanner;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		new SpringApplicationBuilder(Application.class).web(WebApplicationType.NONE).run(args);
	}

	@Bean
	public CommandLineRunner cli(ChatClient.Builder chatClientBuilder, ToolCallbackProvider tools) {

		return args -> {
			// 2. Create the ChatClient with chat memory and RAG support
			var chatClient = chatClientBuilder
					.defaultSystem("You are useful assistant.") // Set the system prompt
					.defaultToolCallbacks(tools)
					.defaultAdvisors(new SimpleLoggerAdvisor())
					.build();

			// 3. Start the chat loop
			System.out.println("\nI am your assistant.\n");
			try (Scanner scanner = new Scanner(System.in)) {
				while (true) {
					System.out.print("\nUSER: ");
					System.out.println("\nASSISTANT: " +
							chatClient.prompt(scanner.nextLine()) // Get the user input
									.call()
									.content());
				}
			}
		};
	}
}
