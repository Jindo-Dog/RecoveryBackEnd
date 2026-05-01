package com.example.recovery.service.memoirs;

import com.example.recovery.domain.memoirs.Memoirs;
import com.example.recovery.dto.MemoirSimple;
import com.example.recovery.repository.memoirs.MemoirRepository;
import com.example.recovery.request.MemoirListRequest;
import com.example.recovery.request.SimplePageRequest;
import com.example.recovery.response.MemoirSimpleResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
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
    void memoirList_returnsPagedMemoirs() {
        // given
        Memoirs memoir1 = new Memoirs();
        memoir1.setId(1L);
        memoir1.setMemoir(Map.of("title", "기록1", "content", "내용1"));
        memoir1.setDate(OffsetDateTime.parse("2026-01-01T12:30:00+09:00"));

        Memoirs memoir2 = new Memoirs();
        memoir2.setId(1L);
        memoir2.setMemoir(Map.of("title", "기록2", "content", "내용2"));
        memoir2.setDate(OffsetDateTime.parse("2026-01-02T12:30:00+09:00"));

        MemoirListRequest request = new MemoirListRequest();
        SimplePageRequest simplePageRequest = new SimplePageRequest();

        given(memoirRepository.getMemoirsByRequest(request, simplePageRequest)).willReturn(List.of(memoir1, memoir2));

        // when
        MemoirSimpleResponse response = memoirService.memoirList(request, simplePageRequest);

        // then
        assertEquals(2, response.getTotal());
        assertEquals(2, response.getList().size());

        MemoirSimple result = response.getList().getFirst();
        assertEquals(1L, result.getId());
        assertEquals("기록1", result.getMemoir().get("title"));
        assertEquals(OffsetDateTime.parse("2026-01-01T12:30:00+09:00"), result.getDate());

        MemoirSimple result2 = response.getList().get(1);
        assertEquals(1L, result2.getId());
        assertEquals("기록2", result2.getMemoir().get("title"));
        assertEquals(OffsetDateTime.parse("2026-01-02T12:30:00+09:00"), result2.getDate());
    }
}