package com.demo.ai.rerank;

import org.springframework.ai.model.Model;

@FunctionalInterface
public interface RerankModel extends Model<RerankRequest, RerankResponse> {

    RerankResponse call(RerankRequest request);

}