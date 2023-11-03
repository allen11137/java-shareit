package ru.practicum.shareit;


import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class Samples {
    public static UserDto getUser1() {
        return UserDto.builder()
                .name("user_1")
                .email("user1@email.ru")
                .build();
    }

    public static UserDto getUser2() {
        return UserDto.builder()
                .name("user_2")
                .email("user2@email.ru")
                .build();
    }

    public static ItemDto getItem1() {
        return ItemDto.builder()
                .name("Name item 1")
                .description("description item 1")
                .available(true)
                .build();
    }

    public static ItemDto getItem2() {
        return ItemDto.builder()
                .name("Name item 2")
                .description("description item 2")
                .available(true)
                .build();
    }

    public static ItemWithBookingDto getItemResponse1(long id) {
        ItemDto itemDto = getItem1();
        return ItemWithBookingDto.builder()
                .id(id)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .comments(Collections.emptyList())
                .available(itemDto.getAvailable())
                .build();
    }

    public static ItemWithBookingDto getItemResponse2(long id) {
        ItemDto itemDto = getItem2();
        return ItemWithBookingDto.builder()
                .id(id)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .comments(Collections.emptyList())
                .available(itemDto.getAvailable())
                .build();
    }

    public static CommentResponseDto getCommentResponse(long id, String userName, LocalDateTime time) {
        return CommentResponseDto.builder()
                .id(id)
                .authorName(userName)
                .created(time)
                .text(getComment_1().getText())
                .build();
    }

    public static CommentDto getComment_1() {
        return CommentDto.builder()
                .text("comment 1")
                .build();
    }

    public static BookingRequest getBooking(long itemId) {
        return BookingRequest.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
    }

    public static BookingDto getBookingResponse(long id, long itemId) {
        return BookingDto.builder()
                .id(id)
                .item(getItem1())
                .status(Status.WAITING)
                .booker(getUser1())
                .start(getBooking(itemId).getStart())
                .end(getBooking(itemId).getEnd())
                .build();
    }

    public static ItemRequestDtoResponse getItemRequestResponseDto(long id, List<ItemDto> itemDtos, LocalDateTime created) {
        return ItemRequestDtoResponse.builder()
                .id(id)
                .items(itemDtos)
                .created(created)
                .description(getItemRequestDto().getDescription())
                .build();
    }

    public static ItemRequestDto getItemRequestDto() {
        return ItemRequestDto.builder()
                .description("item 1 request")
                .build();
    }
}
