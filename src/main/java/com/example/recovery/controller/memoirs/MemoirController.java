package com.example.recovery.controller.memoirs;

import com.example.recovery.request.MemoirListRequest;
import com.example.recovery.request.SimplePageRequest;
import com.example.recovery.response.MemoirSimpleResponse;
import com.example.recovery.service.memoirs.MemoirService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/memoirs")
@RequiredArgsConstructor
public class MemoirController {

    private final MemoirService memoirService;

    @PostMapping()
    public MemoirSimpleResponse viewMemoirsByList(@RequestBody MemoirListRequest request, @Validated SimplePageRequest simplePageRequest) {
        return memoirService.memoirList(request, simplePageRequest);
    }
}
