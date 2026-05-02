package com.example.recovery.service.memoirs;

import com.example.recovery.common.exception.MemoirNotFoundException;
import com.example.recovery.domain.memoirs.Memoirs;
import com.example.recovery.dto.MemoirSimple;
import com.example.recovery.repository.memoirs.MemoirRepository;
import com.example.recovery.request.MemoirCalenderBodyRequest;
import com.example.recovery.request.MemoirCalenderRequest;
import com.example.recovery.request.MemoirListBodyRequest;
import com.example.recovery.request.SimplePageRequest;
import com.example.recovery.response.MemoirCalenderResponse;
import com.example.recovery.response.MemoirSimpleResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class MemoirService {

    private final MemoirRepository memoirRepository;

    @Transactional(readOnly = true)
    public MemoirSimpleResponse getMemoirList(MemoirListBodyRequest request, SimplePageRequest simplePageRequest) {
        List<Memoirs> memoirs = memoirRepository.getMemoirsByRequest(request, simplePageRequest);

        List<MemoirSimple> memoirList = memoirs.stream()
                .map(memoir -> MemoirSimple.builder()
                        .id(memoir.getId())
                        .memoir(memoir.getMemoir())
                        .date(memoir.getDate())
                        .build())
                .toList();

        return new MemoirSimpleResponse(memoirList, memoirs.size());
    }

    @Transactional(readOnly = true)
    public MemoirCalenderResponse getMemoirCalender(MemoirCalenderBodyRequest request, MemoirCalenderRequest simplePageRequest) {
        Memoirs memoirs = memoirRepository.findByUsersIdAndDate(request.getUserId(), simplePageRequest.getDate())
                .orElseThrow(() -> new MemoirNotFoundException("해당 날짜의 회고가 없습니다."));

        MemoirSimple memoirSimple = MemoirSimple.builder()
                .id(memoirs.getId())
                .memoir(memoirs.getMemoir())
                .date(memoirs.getDate())
                .build();

        return new MemoirCalenderResponse(memoirSimple);
    }
}
