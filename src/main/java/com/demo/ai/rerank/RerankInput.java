package com.demo.ai.rerank;

import java.util.List;

public record RerankInput(String query, List<String> documents) {
}
