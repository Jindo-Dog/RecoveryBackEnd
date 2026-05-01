package com.example.recovery.controller.memoirs;

import com.example.recovery.request.MemoirCalenderBodyRequest;
import com.example.recovery.request.MemoirCalenderRequest;
import com.example.recovery.request.MemoirListBodyRequest;
import com.example.recovery.request.SimplePageRequest;
import com.example.recovery.response.MemoirCalenderResponse;
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
    public MemoirSimpleResponse viewMemoirsByList(@RequestBody MemoirListBodyRequest request, @Validated @ModelAttribute SimplePageRequest simplePageRequest) {
        return memoirService.memoirList(request, simplePageRequest);
    }

    @PostMapping(params = "date")
    public MemoirCalenderResponse viewMemoirByCalender(@RequestBody MemoirCalenderBodyRequest request, @ModelAttribute MemoirCalenderRequest calenderRequest) {
        return memoirService.memoirCalender(request, calenderRequest);
    }
}
