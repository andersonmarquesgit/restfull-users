package com.restusers.api.request;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PeriodoRequest {

    private LocalDateTime startDate;
    private LocalDateTime finishDate;
}
