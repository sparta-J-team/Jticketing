package com.project.jticketing.domain.concert.service;

import com.project.jticketing.domain.concert.dto.request.ConcertRegisterRequestDto;
import com.project.jticketing.domain.concert.dto.response.ConcertRegisterResponseDto;
import com.project.jticketing.domain.concert.entity.Concert;
import com.project.jticketing.domain.concert.repository.ConcertRepository;
import com.project.jticketing.domain.event.entity.Event;
import com.project.jticketing.domain.event.repository.EventRepository;
import com.project.jticketing.domain.place.entity.Place;
import com.project.jticketing.domain.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository concertRepository;
    private EventRepository eventRepository;
    private PlaceRepository placeRepository;

    //Admin 인증 추가 예정
    @Transactional
    public ConcertRegisterResponseDto registerConcert(ConcertRegisterRequestDto requestDto) {
        //장소 존재 확인
        Place place = placeRepository.findById(requestDto.getPlaceId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 장소 id입니다."));

        Concert concert = Concert.builder()
                .title(requestDto.getTitle())
                .startTime(requestDto.getStartTime())
                .endTime(requestDto.getEndTime())
                .price(requestDto.getPrice())
                .place(place)
                .build();

        concertRepository.save(concert);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<Event> events = requestDto.getEventsDate().stream()
                .map(eventDate -> Event.builder()
                        .concertDate(LocalDateTime.from(LocalDate.parse(eventDate, formatter)))
                        .build())
                .toList();

        eventRepository.saveAll(events);

        return ConcertRegisterResponseDto.builder()
                .message("콘서트가 성공적으로 등록되었습니다.")
                .build();
    }
}
