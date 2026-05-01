package com.example.recovery.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Map;

@Getter
@Builder
public class MemoirSimple {
    long id;
    Map<String, Object> memoir;
    LocalDate date;
}
