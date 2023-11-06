package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    public static final String PAGE_FROM_DEFAULT = "0";
    public static final String PAGE_SIZE_DEFAULT = "10";

    @PostMapping
    public ResponseEntity<BookingDto> addBooking(@RequestBody BookingRequest request,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ok(bookingService.createBooking(request, userId));
    }

    @PatchMapping("{bookingId}")
    public ResponseEntity<BookingDto> approvedBooking(@PathVariable long bookingId,
                                                      @RequestParam boolean approved,
                                                      @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ok(bookingService.approveBooking(bookingId, approved, userId));
    }

    @GetMapping("{bookingId}")
    public ResponseEntity<BookingDto> getBooking(@PathVariable Long bookingId,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ok(bookingService.getBookingById(userId, bookingId));
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getAllBooking(@RequestParam(defaultValue = "ALL") BookingState state,
                                                          @RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @RequestParam(defaultValue = PAGE_FROM_DEFAULT) Integer from,
                                                          @RequestParam(defaultValue = PAGE_SIZE_DEFAULT) Integer size) {
        return ok(bookingService.getAllBookings(state, userId, from, size));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getListOfBookingByOwner(@RequestParam(defaultValue = "ALL") BookingState state,
                                                                    @RequestHeader("X-Sharer-User-Id") Long userId,
                                                                    @RequestParam(defaultValue = PAGE_FROM_DEFAULT) Integer from,
                                                                    @RequestParam(defaultValue = PAGE_SIZE_DEFAULT) Integer size) {
        return ok(bookingService.getAllOwnerBooking(state, userId, from, size));
    }
}
