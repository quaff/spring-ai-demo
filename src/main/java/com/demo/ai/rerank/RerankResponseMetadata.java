package com.demo.ai.rerank;

import org.springframework.ai.model.AbstractResponseMetadata;
import org.springframework.ai.model.ResponseMetadata;

import java.util.Map;

public class RerankResponseMetadata extends AbstractResponseMetadata implements ResponseMetadata {

    private String model;

    private Usage usage;

    public RerankResponseMetadata(String model, Usage usage) {
        this(model, usage, Map.of());
    }

    public RerankResponseMetadata(String model, Usage usage, Map<String, Object> metadata) {
        this.model = model;
        this.usage = usage;
        this.map.putAll(metadata);
    }

    public String getModel() {
        return this.model != null ? this.model : "";
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Usage getUsage() {
        return this.usage != null ? this.usage : new Usage(0, 0);
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }

    public record Usage(Integer promptTokens, Integer totalTokens) {

    }

}
