package com.demo.ai.rerank;

import org.springframework.ai.model.ModelResult;
import org.springframework.ai.model.ResultMetadata;

public class RerankResult implements ModelResult<RerankOutput> {

    private static ResultMetadata emptyMetadata = new ResultMetadata() {};

    private final RerankOutput output;

    public RerankResult(RerankOutput output) {
        this.output = output;
    }

    @Override
    public RerankOutput getOutput() {
        return this.output;
    }

    @Override
    public ResultMetadata getMetadata() {
        return emptyMetadata;
    }
}