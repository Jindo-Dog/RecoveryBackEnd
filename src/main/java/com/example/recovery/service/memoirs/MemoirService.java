package com.example.recovery.service.memoirs;

import com.example.recovery.domain.memoirs.Memoirs;
import com.example.recovery.dto.MemoirSimple;
import com.example.recovery.repository.memoirs.MemoirRepository;
import com.example.recovery.request.MemoirListRequest;
import com.example.recovery.response.MemoirSimpleResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class MemoirService {

    private final MemoirRepository memoirRepository;

    public MemoirSimpleResponse memoirList(MemoirListRequest request) {
        List<Memoirs> memoirs = memoirRepository.getMemoirsByRequest(request);
        int page = request.getPage();
        int rowsPerPage = request.getRowsPerPage();

        List<MemoirSimple> memoirList = memoirs.stream()
                .map(memoir -> MemoirSimple.builder()
                        .id(memoir.getId())
                        .memoir(memoir.getMemoir())
                        .build())
                .skip((long) (page - 1) * rowsPerPage)
                .limit(rowsPerPage)
                .toList();

        return new MemoirSimpleResponse(memoirList, memoirs.size());
    }
}
