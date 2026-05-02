package com.example.recovery.controller.memoirs;

import com.example.recovery.request.MemoirBodyRequest;
import com.example.recovery.request.MemoirCalenderRequest;
import com.example.recovery.request.SimplePageRequest;
import com.example.recovery.response.MemoirCalenderResponse;
import com.example.recovery.response.MemoirResponse;
import com.example.recovery.response.MemoirSimpleResponse;
import com.example.recovery.service.memoirs.MemoirService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/memoirs")
@RequiredArgsConstructor
public class MemoirController {

    private final MemoirService memoirService;

    @PostMapping(params = "!date")
    public MemoirSimpleResponse viewMemoirsByList(@Validated @RequestBody MemoirBodyRequest request, @Validated @ModelAttribute SimplePageRequest simplePageRequest) {
        return memoirService.getMemoirList(request, simplePageRequest);
    }

    @PostMapping(params = "date")
    public MemoirCalenderResponse viewMemoirByCalender(@Validated @RequestBody MemoirBodyRequest request, @Validated @ModelAttribute MemoirCalenderRequest calenderRequest) {
        return memoirService.getMemoirCalender(request, calenderRequest);
    }

    @PostMapping("/{memoirId}")
    public MemoirResponse viewMemoir(@Validated @RequestBody MemoirBodyRequest request, @PathVariable Long memoirId) {
        return memoirService.getMemoir(request, memoirId);
    }
}
