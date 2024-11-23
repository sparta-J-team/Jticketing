package com.project.jticketing.domain.concert.service;

import com.project.jticketing.domain.concert.dto.request.ConcertRegisterRequestDto;
import com.project.jticketing.domain.concert.dto.response.ConcertListResponseDto;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository concertRepository;
    private final EventRepository eventRepository;
    private final PlaceRepository placeRepository;

    //Admin 인증 추가 예정
    @Transactional
    public ConcertRegisterResponseDto registerConcert(ConcertRegisterRequestDto requestDto) {

        Place place = placeRepository.findById(requestDto.getPlaceId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 장소 id입니다."));

        validateConcertConflict(requestDto.getPlaceId(), requestDto.getStartTime());

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

    public ConcertListResponseDto getAllConcerts() {
        List<Concert> concerts = concertRepository.findAll();

        List<ConcertListResponseDto.ConcertInfo> concertInfos = concerts.stream()
                .map(concert -> ConcertListResponseDto.ConcertInfo.builder()
                        .title(concert.getTitle())
                        .eventsDate(concert.getEvents().stream()
                                .map(event -> event.getConcertDate().toLocalDate().toString())
                                .collect(Collectors.toList()))
                        .startTime(concert.getStartTime())
                        .endTime(concert.getEndTime())
                        .place(concert.getPlace().getName())
                        .price(concert.getPrice())
                        .build())
                .collect(Collectors.toList());

        return ConcertListResponseDto.builder()
                .concertList(concertInfos)
                .build();

    }

    private void validateConcertConflict(Long placeId, String startTime) {
        concertRepository.findByPlaceAndStartTime(placeId, startTime)
                .ifPresent(concert -> {
                    throw new IllegalArgumentException("이미 해당 장소와 시작 시간에 등록된 콘서트가 존재합니다.");
                });
    }
}
