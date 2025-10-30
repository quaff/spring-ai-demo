package com.demo.ai.rerank;

public class ZhiPuAiRerankModel implements RerankModel {

    private final ZhiPuAiRerankApi rerankApi;

    public ZhiPuAiRerankModel(ZhiPuAiRerankApi rerankApi) {
        this.rerankApi = rerankApi;
    }

    @Override
    public RerankResponse call(RerankRequest request) {
        ZhiPuAiRerankApi.ZhiPuAiRerankRequest req = new ZhiPuAiRerankApi.ZhiPuAiRerankRequest(
                request.getInstructions().query(),
                request.getInstructions().documents(),
                request.getOptions().getModel(),
                request.getOptions().getTopN(),
                null,
                null,
                null,
                null
        );
        ZhiPuAiRerankApi.ZhiPuAiRerankResponse response = rerankApi.rerank(req);
        RerankResult result = new RerankResult(new RerankOutput(response.results().stream().map(ZhiPuAiRerankApi.Result::document).toList()));
        RerankResponseMetadata metadata = new RerankResponseMetadata(
                request.getOptions().getModel(),
                new RerankResponseMetadata.Usage(response.usage().promptTokens(), response.usage().totalTokens())
        );
        return new RerankResponse(result, metadata);
    }
}
