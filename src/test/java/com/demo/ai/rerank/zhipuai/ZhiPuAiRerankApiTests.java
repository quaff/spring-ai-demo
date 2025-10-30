package com.demo.ai.rerank.zhipuai;

import com.demo.ai.rerank.zhipuai.zhipuai.ZhiPuAiRerankApi;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ZhiPuAiRerankApiTests {

    @Test
    void test() {
        ZhiPuAiRerankApi api = ZhiPuAiRerankApi.builder("0800149a297f47c5aa67563c21686732.QEc5J8rmW5LNAtCc").build();
        ZhiPuAiRerankApi.ZhiPuAiRerankRequest request = new ZhiPuAiRerankApi.ZhiPuAiRerankRequest("你好吗？", List.of("我今天吃饱了", "今天心情还不错", "今天天气不错", "我很好"));
        ZhiPuAiRerankApi.ZhiPuAiRerankResponse response = api.rerank(request);
        assertThat(response.results()).hasSize(4).first().extracting(ZhiPuAiRerankApi.Result::document).isEqualTo("我很好");
    }

}
