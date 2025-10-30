/*
 * Copyright 2023-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.demo.ai.rerank.zhipuai.zhipuai;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.ai.retry.RetryUtils;
import org.springframework.ai.zhipuai.api.ZhiPuApiConstants;
import org.springframework.util.Assert;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;

/**
 * ZhiPuAI Rerank API.
 *
 * @author Yanming Zhou
 */
public class ZhiPuAiRerankApi {

    private final RestClient restClient;

    /**
     * Create a new ZhiPuAI Rerank API with the provided base URL.
     *
     * @param baseUrl              the base URL for the ZhiPuAI API.
     * @param apiKey               ZhiPuAI apiKey.
     * @param restClientBuilder    the rest client builder to use.
     * @param responseErrorHandler the response error handler to use.
     */
    private ZhiPuAiRerankApi(String apiKey, String baseUrl, RestClient.Builder restClientBuilder,
                             ResponseErrorHandler responseErrorHandler) {

        this.restClient = restClientBuilder.baseUrl(baseUrl).defaultHeaders(h -> h.setBearerAuth(apiKey)
        ).defaultStatusHandler(responseErrorHandler).build();
    }

    public ZhiPuAiRerankResponse rerank(ZhiPuAiRerankRequest request) {
        Assert.notNull(request, "Rerank request cannot be null.");
        Assert.hasLength(request.query(), "Query cannot be empty.");
        Assert.notEmpty(request.documents(), "Documents cannot be empty.");

        ZhiPuAiRerankResponse response = this.restClient.post()
                .uri("/v4/rerank")
                .body(request)
                .retrieve()
                .body(ZhiPuAiRerankResponse.class);
        if (response != null && (request.returnDocuments() == null || !request.returnDocuments())) {
            // fill documents
            List<Result> results = response.results.stream().map(r -> new Result(request.documents.get(r.index), r.index, r.relevanceScore)).toList();
            return new ZhiPuAiRerankResponse(response.id, response.created, results, response.requestId, response.usage);
        }
        return response;
    }

    public static Builder builder(String apiKey) {
        return new Builder(apiKey);
    }

    public static final class Builder {

        private final String apiKey;

        private String baseUrl = ZhiPuApiConstants.DEFAULT_BASE_URL;

        private RestClient.Builder restClientBuilder = RestClient.builder();

        private ResponseErrorHandler responseErrorHandler = RetryUtils.DEFAULT_RESPONSE_ERROR_HANDLER;

        private Builder(String apiKey) {
            this.apiKey = apiKey;
        }

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder restClientBuilder(RestClient.Builder restClientBuilder) {
            this.restClientBuilder = restClientBuilder;
            return this;
        }

        public Builder responseErrorHandler(ResponseErrorHandler responseErrorHandler) {
            this.responseErrorHandler = responseErrorHandler;
            return this;
        }

        public ZhiPuAiRerankApi build() {
            return new ZhiPuAiRerankApi(apiKey, baseUrl, restClientBuilder, responseErrorHandler);
        }
    }

    // @formatter:off
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record ZhiPuAiRerankRequest(
		@JsonProperty("query") String query,
		@JsonProperty("documents") List<String> documents,
        @JsonProperty("model") String model,
		@JsonProperty("top_n") Integer topN,
		@JsonProperty("return_documents") Boolean returnDocuments,
		@JsonProperty("return_raw_scores") Boolean returnRawScores,
        @JsonProperty("request_id") String requestId,
		@JsonProperty("user_id") String userId) {

		public ZhiPuAiRerankRequest(String query, List<String> documents) {
			this(query, documents,"rerank", null, null, null, null, null);
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record ZhiPuAiRerankResponse(
		@JsonProperty("id") String id,
		@JsonProperty("created") Long created,
		@JsonProperty("results") List<Result> results,
        @JsonProperty("request_id") String requestId,
        @JsonProperty("usage") Usage usage) {
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Result(
        @JsonProperty("document") String document,
        @JsonProperty("index") int index,
        @JsonProperty("relevance_score") double relevanceScore) {
	}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Usage(
        @JsonProperty("prompt_tokens") int promptTokens,
        @JsonProperty("total_tokens") int totalTokens) {
    }
    // @formatter:on

}
