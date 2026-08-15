package com.example.recovery.service.memoirs;

import com.example.recovery.common.exception.MemoirNotFoundException;
import com.example.recovery.domain.memoirs.Memoirs;
import com.example.recovery.repository.memoirs.MemoirRepository;
import com.example.recovery.request.FeedbackGenerateRequest;
import com.example.recovery.service.auth.AuthTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackService {
    private final AuthTokenService authTokenService;
    private final MemoirRepository memoirRepository;

    private final ChatModel chatModel;
    private final ObjectMapper objectMapper;

    @Transactional
    public void generateFeedback(FeedbackGenerateRequest request, Long memoirId) {
        Long userId = authTokenService.getCurrentUserId();

        Memoirs memoir = memoirRepository.findByIdAndUsersId(memoirId, userId)
                .orElseThrow(() -> new MemoirNotFoundException("해당 회고가 없습니다."));

        String prompt = createBatchPrompt(request);
        String result = requestFeedback(prompt);
        Map<String, Object> feedback = StringUtils.isBlank(result) ? createFailureResponse(request) : parseBatchFeedback(result, request);

        memoir.setFeedback(feedback);
    }

    private String createBatchPrompt(FeedbackGenerateRequest request) {
        String requestJson = serializeRequest(request);

        return """
                아래 입력 JSON의 키 구조를 그대로 유지해서 피드백 JSON을 생성해주세요. 출력은 JSON만 반환하고 코드블록이나 설명은 포함하지 마세요.
                출력 형식:
                {
                  "1": {
                    "feedback": "대주제 피드백",
                    "subFeedbacks": {
                      "1": {
                        "feedback": "소주제 피드백"
                      }
                    }
                  }
                }
                대주제 피드백은 2~3문장, 소주제 피드백은 1~2문장, 한국어로 작성해주세요.
                입력 JSON:
                """ + requestJson;
    }

    private String serializeRequest(FeedbackGenerateRequest request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (Exception exception) {
            log.error("피드백 요청 JSON 직렬화에 실패했습니다.", exception);
            return "{}";
        }
    }

    private Map<String, Object> parseBatchFeedback(String result, FeedbackGenerateRequest request) {
        try {
            return objectMapper.readValue(result, new TypeReference<>() {
            });
        } catch (Exception exception) {
            log.error("피드백 응답 JSON 파싱에 실패했습니다.", exception);
            return createFailureResponse(request);
        }
    }

    private Map<String, Object> createFailureResponse(FeedbackGenerateRequest request) {
        Map<String, Object> feedbacks = new LinkedHashMap<>();

        for (Map.Entry<String, FeedbackGenerateRequest.MemoirRequest> entry : request.getMemoirs().entrySet()) {
            Map<String, Object> subFeedbacks = new LinkedHashMap<>();

            for (String subKey : entry.getValue().getSubMemoirTitles().keySet()) {
                Map<String, Object> subFeedback = new LinkedHashMap<>();
                subFeedback.put("feedback", "피드백을 생성하지 못했습니다.");
                subFeedbacks.put(subKey, subFeedback);
            }

            Map<String, Object> feedback = new LinkedHashMap<>();
            feedback.put("feedback", "피드백을 생성하지 못했습니다.");
            feedback.put("subFeedbacks", subFeedbacks);

            feedbacks.put(entry.getKey(), feedback);
        }

        return feedbacks;
    }

    private String requestFeedback(String promptMessage) {
        ChatResponse response = chatModel.call(new Prompt(new UserMessage(promptMessage)));
        if (response == null
                || response.getResult() == null
                || response.getResult().getOutput() == null) {
            log.error("ChatModel 응답이 비어 있습니다.");
            return "";
        }

        String text = response.getResult().getOutput().getText();
        if (StringUtils.isBlank(text)) {
            log.error("ChatModel 응답 텍스트가 비어 있습니다.");
            return "";
        }

        String trimmed = text.trim();
        if (trimmed.startsWith("```") && trimmed.endsWith("```")) {
            int firstLineEnd = trimmed.indexOf('\n');
            if (firstLineEnd > -1 && trimmed.length() > firstLineEnd + 1) {
                return trimmed.substring(firstLineEnd + 1, trimmed.length() - 3).trim();
            }
        }

        return trimmed;
    }
}