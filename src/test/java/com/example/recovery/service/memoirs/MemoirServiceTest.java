package com.example.recovery.service.memoirs;

import com.example.recovery.common.exception.MemoirNotFoundException;
import com.example.recovery.domain.memoirs.Memoirs;
import com.example.recovery.dto.MemoirSimple;
import com.example.recovery.repository.memoirs.MemoirRepository;
import com.example.recovery.request.MemoirBodyRequest;
import com.example.recovery.request.MemoirCalenderRequest;
import com.example.recovery.request.SimplePageRequest;
import com.example.recovery.response.MemoirCalenderResponse;
import com.example.recovery.response.MemoirResponse;
import com.example.recovery.response.MemoirSimpleResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MemoirServiceTest {

    @Mock
    private MemoirRepository memoirRepository;

    @InjectMocks
    private MemoirService memoirService;

    @Test
    @DisplayName("회고 목록 조회 서비스 - 페이징 처리")
    void getMemoirList_returnsPagedMemoirs() {
        // given
        Memoirs memoir1 = new Memoirs();
        memoir1.setId(1L);
        memoir1.setMemoir(Map.of("title", "기록1", "content", "내용1"));
        memoir1.setDate(LocalDate.parse("2026-01-01"));

        Memoirs memoir2 = new Memoirs();
        memoir2.setId(1L);
        memoir2.setMemoir(Map.of("title", "기록2", "content", "내용2"));
        memoir2.setDate(LocalDate.parse("2026-01-02"));

        MemoirBodyRequest request = new MemoirBodyRequest();
        SimplePageRequest simplePageRequest = new SimplePageRequest();

        given(memoirRepository.getMemoirsByRequest(request, simplePageRequest)).willReturn(List.of(memoir1, memoir2));

        // when
        MemoirSimpleResponse response = memoirService.getMemoirList(request, simplePageRequest);

        // then
        assertEquals(2, response.getTotal());
        assertEquals(2, response.getList().size());

        MemoirSimple result = response.getList().getFirst();
        assertEquals(1L, result.getId());
        assertEquals("기록1", result.getMemoir().get("title"));
        assertEquals(LocalDate.parse("2026-01-01"), result.getDate());

        MemoirSimple result2 = response.getList().get(1);
        assertEquals(1L, result2.getId());
        assertEquals("기록2", result2.getMemoir().get("title"));
        assertEquals(LocalDate.parse("2026-01-02"), result2.getDate());
    }

    @Test
    @DisplayName("회고 캘린더 조회 서비스 - 정상 케이스")
    void getMemoirCalender_returnsMemoir() {
        // given
        Memoirs memoir = new Memoirs();
        memoir.setId(1L);
        memoir.setMemoir(Map.of("title", "캘린더 회고", "content", "내용"));
        memoir.setDate(LocalDate.parse("2026-01-01"));

        MemoirBodyRequest bodyRequest = new MemoirBodyRequest();
        bodyRequest.setUserId(1L);
        MemoirCalenderRequest calenderRequest = new MemoirCalenderRequest();
        calenderRequest.setDate(LocalDate.parse("2026-05-02"));

        given(memoirRepository.findByUsersIdAndDate(1L, calenderRequest.getDate())).willReturn(java.util.Optional.of(memoir));

        // when
        MemoirCalenderResponse response = memoirService.getMemoirCalender(bodyRequest, calenderRequest);

        // then
        assertNotNull(response);
        assertNotNull(response.getMemoirSimple());
        assertEquals(1L, response.getMemoirSimple().getId());
        assertEquals("캘린더 회고", response.getMemoirSimple().getMemoir().get("title"));
        assertEquals(LocalDate.parse("2026-01-01"), response.getMemoirSimple().getDate());
    }

    @Test
    @DisplayName("회고 캘린더 조회 서비스 - 해당 날짜 회고 없음 예외")
    void getMemoirCalender_throwsWhenNotFound() {
        // given
        MemoirBodyRequest bodyRequest = new MemoirBodyRequest();
        bodyRequest.setUserId(99L);
        MemoirCalenderRequest calenderRequest = new MemoirCalenderRequest();
        calenderRequest.setDate(LocalDate.parse("2026-05-03"));

        given(memoirRepository.findByUsersIdAndDate(99L, calenderRequest.getDate())).willReturn(java.util.Optional.empty());

        // when / then
        assertThrows(MemoirNotFoundException.class, () -> memoirService.getMemoirCalender(bodyRequest, calenderRequest));
    }

    @Test
    @DisplayName("회고 단건 조회 서비스 - 정상 케이스")
    void getMemoir_returnsMemoirResponse() {
        // given
        Memoirs memoir = new Memoirs();
        memoir.setId(1L);
        memoir.setMemoir(Map.of("title", "회고", "content", "내용"));
        memoir.setImprovement(Map.of("title", "개선", "content", "개선 내용"));
        memoir.setFeedback(Map.of("title", "피드백", "content", "피드백 내용"));

        MemoirBodyRequest request = new MemoirBodyRequest();
        request.setUserId(1L);

        given(memoirRepository.findByIdAndUsersId(1L, 1L)).willReturn(java.util.Optional.of(memoir));

        // when
        MemoirResponse response = memoirService.getMemoir(request, 1L);

        // then
        assertNotNull(response);
        assertEquals("회고", response.getMemoir().get("title"));
        assertEquals("개선", response.getImprovement().get("title"));
        assertEquals("피드백", response.getFeedback().get("title"));
    }

    @Test
    @DisplayName("회고 단건 조회 서비스 - 회고 없음 예외")
    void getMemoir_throwsWhenNotFound() {
        // given
        MemoirBodyRequest request = new MemoirBodyRequest();
        request.setUserId(1L);

        given(memoirRepository.findByIdAndUsersId(1L, 1L)).willReturn(java.util.Optional.empty());

        // when / then
        assertThrows(MemoirNotFoundException.class, () -> memoirService.getMemoir(request, 1L));
    }
}