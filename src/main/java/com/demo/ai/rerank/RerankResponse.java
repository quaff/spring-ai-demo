package com.demo.ai.rerank;

import org.springframework.ai.model.ModelResponse;
import org.springframework.ai.model.ResponseMetadata;

import java.util.List;

public class RerankResponse implements ModelResponse<RerankResult> {

    private final RerankResult result;

    private final RerankResponseMetadata metadata;

    public RerankResponse(RerankResult result, RerankResponseMetadata metadata) {
        this.result = result;
        this.metadata = metadata;
    }

    @Override
    public RerankResult getResult() {
        return this.result;
    }

    @Override
    public List<RerankResult> getResults() {
        throw new UnsupportedOperationException("Use getResult() instead");
    }

    @Override
    public ResponseMetadata getMetadata() {
        return this.metadata;
    }
}
