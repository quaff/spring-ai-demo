package com.demo.ai.rerank;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ZhiPuAiRerankModelTests {

    @Test
    void test() {
        ZhiPuAiRerankApi api = ZhiPuAiRerankApi.builder("0800149a297f47c5aa67563c21686732.QEc5J8rmW5LNAtCc").build();
        ZhiPuAiRerankModel model = new ZhiPuAiRerankModel(api);

        RerankRequest request = new RerankRequest(
                new RerankInput("你好吗？", List.of("我今天吃饱了", "今天心情还不错", "今天天气不错", "我很好")),
                RerankOptions.builder().model("rerank").topN(2).build()
        );
        RerankResponse response = model.call(request);
        assertThat(response.getResult().getOutput().documents()).hasSize(2).first().isEqualTo("我很好");
    }

}
