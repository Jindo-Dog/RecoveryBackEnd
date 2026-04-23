package com.example.recovery.controller.memoirs;

import com.example.recovery.request.MemoirListRequest;
import com.example.recovery.response.MemoirSimpleResponse;
import com.example.recovery.service.memoirs.MemoirService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/memoirs")
@RequiredArgsConstructor
public class MemoirController {

    private final MemoirService memoirService;

    @GetMapping()
    public MemoirSimpleResponse viewMemoirsByList(MemoirListRequest request) {
        return memoirService.memoirList(request);
    }
}
