package com.project.jticketing.domain.concert.service;

import com.project.jticketing.config.security.UserDetailsImpl;
import com.project.jticketing.domain.concert.dto.request.ConcertRequestDto;
import com.project.jticketing.domain.concert.dto.response.ConcertDetailResponseDto;
import com.project.jticketing.domain.concert.dto.response.ConcertListResponseDto;
import com.project.jticketing.domain.concert.dto.response.ConcertResponseDto;
import com.project.jticketing.domain.concert.entity.Concert;
import com.project.jticketing.domain.concert.repository.ConcertRepository;
import com.project.jticketing.domain.event.entity.Event;
import com.project.jticketing.domain.event.repository.EventRepository;
import com.project.jticketing.domain.place.entity.Place;
import com.project.jticketing.domain.place.repository.PlaceRepository;
import com.project.jticketing.domain.user.enums.UserRole;
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

    @Transactional
    public ConcertResponseDto registerConcert(ConcertRequestDto requestDto, UserDetailsImpl userDetails) {

        if (isAdmin(userDetails)) {
            return new ConcertResponseDto("관리자 권한이 필요합니다.");
        }

        Place place = placeRepository.findById(requestDto.getPlaceId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 장소 id입니다."));

        validateConcert(requestDto.getPlaceId(), requestDto.getStartTime(), requestDto.getEventsDate());

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
                        .concertDate(LocalDate.parse(eventDate, formatter))
                        .concert(concert)
                        .build())
                .toList();

        eventRepository.saveAll(events);

        return ConcertResponseDto.builder()
                .message("콘서트가 성공적으로 등록되었습니다.")
                .build();
    }

    public ConcertListResponseDto getAllConcerts(UserDetailsImpl userDetails) {

        if (userDetails == null) {
            throw new IllegalArgumentException("콘서트를 보려면 사용자 인증을 받아야 합니다.");
        }

        List<Concert> concerts = concertRepository.findAll();

        List<ConcertListResponseDto.ConcertInfo> concertInfos = concerts.stream()
                .map(concert -> ConcertListResponseDto.ConcertInfo.builder()
                        .title(concert.getTitle())
                        .eventsDate(concert.getEvents().stream()
                                .map(event -> event.getConcertDate().toString())
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

    public ConcertDetailResponseDto getConcertDetail(Long concertId, UserDetailsImpl userDetails) {

        if (userDetails == null) {
            throw new IllegalArgumentException("콘서트를 보려면 사용자 인증을 받아야 합니다.");
        }

        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new IllegalArgumentException("해당 콘서트를 찾을 수 없습니다."));

        return ConcertDetailResponseDto.builder()
                .title(concert.getTitle())
                .eventsDate(concert.getEvents().stream()
                        .map(event -> event.getConcertDate().toString())
                        .collect(Collectors.toList()))
                .startTime(concert.getStartTime())
                .endTime(concert.getEndTime())
                .place(concert.getPlace().getName())
                .price(concert.getPrice())
                .build();
    }

    @Transactional
    public ConcertResponseDto updateConcert(Long concertId, ConcertRequestDto requestDto, UserDetailsImpl userDetails) {

        if (isAdmin(userDetails)) {
            return new ConcertResponseDto("관리자 권한이 필요합니다.");
        }

        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new IllegalArgumentException("해당 콘서트를 찾을 수 없습니다."));

        Place place = placeRepository.findById(requestDto.getPlaceId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 장소 id입니다."));

        validateConcert(requestDto.getPlaceId(), requestDto.getStartTime(), requestDto.getEventsDate());

        concert.update(
                requestDto.getTitle(),
                requestDto.getStartTime(),
                requestDto.getEndTime(),
                requestDto.getPrice(),
                place,
                requestDto.getEventsDate()
        );

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<Event> events = requestDto.getEventsDate().stream()
                .map(eventDate -> Event.builder()
                        .concertDate(LocalDate.from(LocalDate.parse(eventDate, formatter)))
                        .concert(concert)
                        .build())
                .collect(Collectors.toList());

        eventRepository.deleteAllByConcert(concert);
        eventRepository.saveAll(events);

        return ConcertResponseDto.builder()
                .message("콘서트의 상세 정보가 성공적으로 수정되었습니다.")
                .build();
    }

    @Transactional
    public ConcertResponseDto deleteConcert(Long concertId, UserDetailsImpl userDetails) {

        if (isAdmin(userDetails)) {
            return new ConcertResponseDto("관리자 권한이 필요합니다.");
        }

        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new IllegalArgumentException("해당 콘서트를 찾을 수 없습니다."));

        concertRepository.delete(concert);

        return ConcertResponseDto.builder()
                .message("해당 콘서트가 삭제되었습니다.")
                .build();
    }

    private boolean isAdmin(UserDetailsImpl userDetails) {
        return userDetails.getUser().getUserRole() != UserRole.ADMIN;
    }

    private void validateConcert(Long placeId, String startTime, List<String> eventsDate) {

        List<LocalDate> parsedDates = eventsDate.stream()
                .map(eventDate -> LocalDate.parse(eventDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .collect(Collectors.toList());

        concertRepository.findByPlaceAndEventDateIn(placeId, parsedDates)
                .ifPresent(concert -> {
                    throw new IllegalArgumentException("이미 해당 장소와 날짜에 등록된 콘서트가 존재합니다.");
                });

        concertRepository.findByPlaceIdAndStartTime(placeId, startTime)
                .ifPresent(concert -> {
                    throw new IllegalArgumentException("이미 해당 장소와 시작 시간에 등록된 콘서트가 존재합니다.");
                });
    }

}
