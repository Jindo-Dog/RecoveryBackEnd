package com.example.recovery.response;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class FeedbackGenerateResponse {

    private String feedback;
    private Map<String, SubFeedbackResponse> subFeedbacks;

    @Getter
    @Builder
    public static class SubFeedbackResponse {
        private String feedback;
    }
}