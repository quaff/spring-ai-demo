package com.demo.ai;

import java.util.List;
import java.util.Scanner;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.simple.JdbcClient;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		new SpringApplicationBuilder(Application.class).web(WebApplicationType.NONE).run(args);
	}

    @Autowired
    private ApplicationContext context;

    @PostConstruct
    private void init() {
        JdbcClient jdbcClient = context.getBean(JdbcClient.class);
        if (jdbcClient.sql("select count(*) from vector_store").query(Long.class).single() == 0) {
            VectorStore vectorStore = context.getBean(VectorStore.class);
            List<Document> documents = new TikaDocumentReader(new ClassPathResource("documents/MVP.pdf")).read();
            TextSplitter textSplitter = new TokenTextSplitter();
            documents = textSplitter.split(documents);
            vectorStore.add(documents);
        }
    }

	@Bean
	public CommandLineRunner cli(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {

		return args -> {
			// 2. Create the ChatClient with chat memory and RAG support
			var chatClient = chatClientBuilder
					.defaultSystem("You are useful assistant.") // Set the system prompt
					.defaultAdvisors(new SimpleLoggerAdvisor(), QuestionAnswerAdvisor.builder(vectorStore)
                            .searchRequest(SearchRequest.builder().build()).build())
					.build();

			// 3. Start the chat loop
			System.out.println("\nI am your assistant.\n");
			try (Scanner scanner = new Scanner(System.in)) {
				while (true) {
					System.out.print("\nUSER: ");
					System.out.println("\nASSISTANT: " +
							chatClient.prompt(scanner.nextLine()) // 谁是获得最优价值球员的国际球员？
									.call()
									.content());
				}
			}
		};
	}
}
