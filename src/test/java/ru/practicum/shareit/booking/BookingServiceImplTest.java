package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.Samples;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static ru.practicum.shareit.booking.model.BookingState.*;
import static ru.practicum.shareit.booking.model.Status.APPROVED;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class BookingServiceImplTest {
    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;
    private BookingRequest bookingRequest;
    private ItemDto itemDto;
    private UserDto userDto;
    private UserDto userDto2;

    @BeforeEach
    public void beforeEach() {
        userDto = userService.createUser(Samples.getUser1());
        userDto2 = userService.createUser(Samples.getUser2());
        itemDto = itemService.createItem(userDto2.getId(), Samples.getItem1());
        bookingRequest = Samples.getBooking(itemDto.getId());
    }

    @Test
    public void saveResponseIsValid() {
        BookingDto booking = bookingService.createBooking(bookingRequest, userDto.getId());
        assertThat(booking).hasFieldOrPropertyWithValue("item", itemDto)
                .hasFieldOrPropertyWithValue("booker", userDto)
                .hasFieldOrPropertyWithValue("start", bookingRequest.getStart())
                .hasFieldOrPropertyWithValue("end", bookingRequest.getEnd())
                .hasFieldOrPropertyWithValue("status", Status.WAITING);
    }

    @Test
    public void approveTrue() {
        BookingDto booking = bookingService.createBooking(bookingRequest, userDto.getId());
        BookingDto bookingDtoAfterApproval = bookingService.approveBooking(booking.getId(), true, userDto2.getId());
        assertThat(bookingDtoAfterApproval).hasFieldOrPropertyWithValue("status", APPROVED);
    }

    @Test
    public void approveFalse() {
        BookingDto booking = bookingService.createBooking(bookingRequest, userDto.getId());
        BookingDto bookingDtoAfterApproval = bookingService.approveBooking(booking.getId(), false, userDto2.getId());
        assertThat(bookingDtoAfterApproval).hasFieldOrPropertyWithValue("status", Status.REJECTED);
    }

    @Test
    public void getAllByBooker() {
        BookingDto booking = bookingService.createBooking(bookingRequest, userDto.getId());
        List<BookingDto> bookingDtosResponse =
                bookingService.getAllBookings(ALL, userDto.getId(), 0, Integer.MAX_VALUE);
        assertThat(bookingDtosResponse).isEqualTo(List.of(booking));
    }

    @Test
    public void getCurrentByBooker() {
        bookingRequest.setStart(LocalDateTime.now());
        bookingRequest.setEnd(LocalDateTime.now().plusDays(5));
        BookingDto bookingDtoSaved = bookingService.createBooking(bookingRequest, userDto.getId());
        List<BookingDto> bookingDtosResponse = bookingService.getAllBookings(CURRENT, userDto.getId(), 0, Integer.MAX_VALUE);
        assertThat(bookingDtosResponse).isEqualTo(List.of(bookingDtoSaved));
    }

    @Test
    public void getFutureByBooker() {
        bookingRequest.setStart(LocalDateTime.now().plusDays(5));
        bookingRequest.setEnd(LocalDateTime.now().plusDays(6));
        BookingDto bookingDtoSaved = bookingService.createBooking(bookingRequest, userDto.getId());
        List<BookingDto> bookingDtosResponse =
                bookingService.getAllBookings(FUTURE, userDto.getId(), 0, Integer.MAX_VALUE);
        assertThat(bookingDtosResponse).isEqualTo(List.of(bookingDtoSaved));
    }

    @Test
    public void getRejectedByBooker() {
        BookingDto bookingDtoSaved = bookingService.createBooking(bookingRequest, userDto.getId());
        bookingDtoSaved = bookingService.approveBooking(bookingDtoSaved.getId(), false, userDto2.getId());
        List<BookingDto> bookingDtosResponse =
                bookingService.getAllBookings(REJECTED, userDto.getId(), 0, Integer.MAX_VALUE);
        assertThat(bookingDtosResponse).isEqualTo(List.of(bookingDtoSaved));
    }

    @Test
    public void getWaitingByBooker() {
        BookingDto bookingDtoSaved = bookingService.createBooking(bookingRequest, userDto.getId());
        List<BookingDto> bookingDtosResponse =
                bookingService.getAllBookings(WAITING, userDto.getId(), 0, Integer.MAX_VALUE);
        assertThat(bookingDtosResponse).isEqualTo(List.of(bookingDtoSaved));
    }

    @Test
    public void getAllByItemOwner() {
        BookingDto bookingDtoSaved = bookingService.createBooking(bookingRequest, userDto.getId());
        List<BookingDto> bookingDtosResponse =
                bookingService.getAllOwnerBooking(ALL, userDto2.getId(), 0, Integer.MAX_VALUE);
        assertThat(bookingDtosResponse).isEqualTo(List.of(bookingDtoSaved));
    }

    @Test
    public void getCurrentByItemOwner() {
        bookingRequest.setStart(LocalDateTime.now());
        bookingRequest.setEnd(LocalDateTime.now().plusDays(5));
        BookingDto bookingDtoSaved = bookingService.createBooking(bookingRequest, userDto.getId());
        List<BookingDto> bookingDtosResponse =
                bookingService.getAllOwnerBooking(CURRENT, userDto2.getId(), 0, Integer.MAX_VALUE);
        assertThat(bookingDtosResponse).isEqualTo(List.of(bookingDtoSaved));
    }

    @Test
    public void getFutureByItemOwner() {
        bookingRequest.setStart(LocalDateTime.now().plusDays(5));
        bookingRequest.setEnd(LocalDateTime.now().plusDays(6));
        BookingDto bookingDtoSaved = bookingService.createBooking(bookingRequest, userDto.getId());
        List<BookingDto> bookingDtosResponse =
                bookingService.getAllOwnerBooking(FUTURE, userDto2.getId(), 0, Integer.MAX_VALUE);
        assertThat(bookingDtosResponse).isEqualTo(List.of(bookingDtoSaved));
    }

    @Test
    public void getRejectedByItemOwner() {
        BookingDto bookingDtoSaved = bookingService.createBooking(bookingRequest, userDto.getId());
        bookingDtoSaved = bookingService.approveBooking(bookingDtoSaved.getId(), false, userDto2.getId());
        List<BookingDto> bookingDtosResponse =
                bookingService.getAllOwnerBooking(REJECTED, userDto2.getId(), 0, Integer.MAX_VALUE);
        assertThat(bookingDtosResponse).isEqualTo(List.of(bookingDtoSaved));
    }

    @Test
    public void getWaitingByItemOwner() {
        BookingDto bookingDtoSaved = bookingService.createBooking(bookingRequest, userDto.getId());
        List<BookingDto> bookingDtosResponse =
                bookingService.getAllOwnerBooking(WAITING, userDto2.getId(), 0, Integer.MAX_VALUE);
        assertThat(bookingDtosResponse).isEqualTo(List.of(bookingDtoSaved));
    }
}