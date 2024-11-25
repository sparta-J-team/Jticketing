package com.project.jticketing.domain.concert.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConcertDetailResponseDto {

    private String title;
    private List<String> eventsDate;
    private String startTime;
    private String endTime;
    private String place;
    private Long price;
}
