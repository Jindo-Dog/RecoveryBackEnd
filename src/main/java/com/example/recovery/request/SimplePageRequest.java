package com.example.recovery.request;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimplePageRequest {
    @Positive
    int page = 1;

    @Positive
    int rowsPerPage = 10;
}
