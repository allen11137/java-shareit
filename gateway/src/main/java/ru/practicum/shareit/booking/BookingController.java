package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingClient client;
    public static final String PAGE_FROM_DEFAULT = "0";
    public static final String PAGE_SIZE_DEFAULT = "10";

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestBody BookingRequest request,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.create(request, userId);
    }

    @PatchMapping("{bookingId}")
    public ResponseEntity<Object> approvedBooking(@PathVariable long bookingId,
                                                  @RequestParam boolean approved,
                                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.approve(bookingId, approved, userId);
    }

    @GetMapping("{bookingId}")
    public ResponseEntity<Object> getBooking(@PathVariable Long bookingId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.getById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBooking(@RequestParam(defaultValue = "ALL") BookingState state,
                                                @RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(defaultValue = PAGE_FROM_DEFAULT) @PositiveOrZero Integer from,
                                                @RequestParam(defaultValue = PAGE_SIZE_DEFAULT) @Positive Integer size) {
        return client.getAllByBookerId(state, userId, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getListOfBookingByOwner(@RequestParam(defaultValue = "ALL") BookingState state,
                                                          @RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @RequestParam(defaultValue = PAGE_FROM_DEFAULT) @PositiveOrZero Integer from,
                                                          @RequestParam(defaultValue = PAGE_SIZE_DEFAULT) @Positive Integer size) {
        return client.getAllByItemOwnerId(state, userId, from, size);
    }
}
