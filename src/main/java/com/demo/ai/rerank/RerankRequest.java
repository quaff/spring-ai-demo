package com.demo.ai.rerank;

import org.springframework.ai.model.ModelRequest;
import org.springframework.lang.Nullable;

public class RerankRequest implements ModelRequest<RerankInput> {

    private final RerankInput input;

    private final RerankOptions options;

    public RerankRequest(RerankInput input, RerankOptions options) {
        this.input = input;
        this.options = options;
    }

    @Override
    public RerankInput getInstructions() {
        return this.input;
    }

    @Override
    public RerankOptions getOptions() {
        return this.options;
    }
}
