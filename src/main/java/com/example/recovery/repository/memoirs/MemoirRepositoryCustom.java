package com.example.recovery.repository.memoirs;

import com.example.recovery.domain.memoirs.Memoirs;
import com.example.recovery.request.MemoirListRequest;
import com.example.recovery.request.SimplePageRequest;

import java.util.List;

public interface MemoirRepositoryCustom {
    List<Memoirs> getMemoirsByRequest(MemoirListRequest request, SimplePageRequest simplePageRequest);

}
