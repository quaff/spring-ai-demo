package com.demo.ai.rerank;

import org.springframework.ai.model.ModelOptions;
import org.springframework.lang.Nullable;

public interface RerankOptions extends ModelOptions {

    String getModel();

    @Nullable
    Integer getTopN();

    static Builder builder() {
        return new DefaultRerankOptionsBuilder();
    }

    interface Builder {

        Builder model(String model);

        Builder topN(Integer topN);

        RerankOptions build();

    }

    class DefaultRerankOptionsBuilder implements Builder {

        private String model;

        private Integer topN;

        @Override
        public Builder model(String model) {
            this.model = model;
            return this;
        }

        @Override
        public Builder topN(Integer topN) {
            this.topN = topN;
            return this;
        }

        @Override
        public RerankOptions build() {
            return new RerankOptions() {
                @Override
                public String getModel() {
                    return DefaultRerankOptionsBuilder.this.model;
                }
                @Override
                public Integer getTopN() {
                    return DefaultRerankOptionsBuilder.this.topN;
                }
            };
        }
    }
}