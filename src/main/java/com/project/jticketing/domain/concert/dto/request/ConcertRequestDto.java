package com.project.jticketing.domain.concert.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConcertRequestDto {

    @NotBlank(message = "콘서트 제목을 입력하세요")
    private String title;

    @NotNull(message = "날짜를 입력하세요")
    @Size(min = 1, message = "최소한 하나의 콘서트 날짜를 입력하세요")
    //@Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "날짜는 yyyy-MM-dd 형식이어야 합니다.")
    @Valid
    private List<@Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "날짜는 yyyy-MM-dd 형식이어야 합니다.") String> eventsDate;

    @NotBlank(message = "시작 시간을 입력하세요")
    private String startTime;

    @NotBlank(message = "끝 시간을 입력하세요")
    private String endTime;

    @NotNull(message = "장소 id를 입력하세요")
    private Long placeId;

    @NotNull(message = "가격을 입력하세요")
    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    private Long price;
}
