package com.example.recovery.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class FeedbackGenerateRequest {

    @NotNull(message = "memoirs는 필수 항목입니다.")
    private Map<String, MemoirRequest> memoirs;

    @NotNull(message = "improvements는 필수 항목입니다.")
    private Map<String, ImprovementRequest> improvements;

    @Getter
    @Setter
    public static class MemoirRequest {
        @NotNull(message = "title은 필수 항목입니다.")
        private String title;
        @NotNull(message = "subMemoirTitles는 필수 항목입니다.")
        private Map<String, MemoirSubRequest> subMemoirTitles;
    }

    @Getter
    @Setter
    public static class MemoirSubRequest {
        @NotNull(message = "title은 필수 항목입니다.")
        private String title;
    }

    @Getter
    @Setter
    public static class ImprovementRequest {
        @NotNull(message = "improvement는 필수 항목입니다.")
        private String improvement;
        @NotNull(message = "subImprovements는 필수 항목입니다.")
        private Map<String, ImprovementSubRequest> subImprovements;
    }

    @Getter
    @Setter
    public static class ImprovementSubRequest {
        @NotNull(message = "improvement는 필수 항목입니다.")
        private String improvement;
    }
}