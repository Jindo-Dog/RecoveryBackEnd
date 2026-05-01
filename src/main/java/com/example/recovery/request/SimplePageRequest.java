package com.example.recovery.request;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimplePageRequest {
    @Positive
    private int page = 1;

    @Positive
    private int rowsPerPage = 10;
}
