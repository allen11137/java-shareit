package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.TimeException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.Status.APPROVED;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ItemService itemService;

    @Override
    @Transactional
    public BookingDto createBooking(BookingRequest request, Long userId) {
        User user = userService.getUser(userId);
        Item item = itemService.getItemById(request.getItemId(), userId);
        if (!item.getAvailable()) {
            throw new ErrorException(String.format("item %s not available", item.getId()));
        }
        if (Objects.equals(item.getOwnerId(), userId)) {
            throw new NotFoundException(String.format("user can not book its own item %s", item.getId()));
        }
        checkValidationTime(request);
        Booking booking = BookingMapper.toBooking(request, item, user.getId());
        bookingRepository.save(booking);
        return BookingMapper.mapBookingToBookingDto(booking, user);
    }

    @Override
    @Transactional
    public BookingDto approveBooking(Long bookingId, Boolean approved, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование для пользователя " + "не найдено", userId));;
        itemService.getItemById(booking.getItem().getId(), userId);
        if (!Objects.equals(booking.getItem().getOwnerId(), userId)) {
            throw new NotFoundException("Only item owner can approve booking");
        }
        if (booking.getStatus().equals(APPROVED)) {
            throw new ErrorException(String.format("booking %s is already approved", bookingId));
        }
        User booker = userService.getUser(booking.getBookerId());
        booking.setStatus(approved ? APPROVED : Status.REJECTED);
        return BookingMapper.mapBookingToBookingDto(bookingRepository.save(booking), booker);
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование для пользователя " + "не найдено", userId));;
        User booker = userService.getUser(booking.getBookerId());
        if (!Objects.equals(booking.getBookerId(), user.getId()) && !Objects.equals(booking.getItem().getOwnerId(), user.getId())) {
            throw new NotFoundException("Booking was not booked by user and item owner is different");
        }
        return bookingRepository.findById(bookingId)
                .map(b -> BookingMapper.mapBookingToBookingDto(b, booker))
                .orElse(null);
    }

    @Override
    public List<BookingDto> getAllBookings(BookingState state, Long userId, Integer from, Integer size) {
        List<Booking> bookings = new ArrayList<>();
        User user = userService.getUser(userId);

        Pageable pageable = getPageable(from, size, Sort.by("start").descending());
        switch (state) {
            case CURRENT:
                bookings.addAll(bookingRepository.findBookingsByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), LocalDateTime.now(), pageable));
                break;
            case REJECTED:
                bookings.addAll(bookingRepository.findBookingsByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, pageable));
                break;
            case WAITING:
                bookings.addAll(bookingRepository.findBookingsByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING, pageable));
                break;
            case PAST:
                bookings.addAll(bookingRepository.findBookingsByBookerIdAndStatusInAndEndBeforeOrderByStartDesc(userId,
                        List.of(APPROVED, Status.WAITING), LocalDateTime.now(), pageable));
                break;
            case FUTURE:
                List<Booking> bookingList = bookingRepository.findBookingsByBookerIdAndStatusInAndStartAfterOrderByStartDesc(userId,
                        List.of(APPROVED, Status.WAITING), LocalDateTime.now(), pageable);

                bookings.addAll(bookingList);
                break;
            default:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageable);
                break;
        }
        return bookings.stream()
                .map(b -> BookingMapper.mapBookingToBookingDto(b, user))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllOwnerBooking(BookingState state, Long userId, Integer from, Integer size) {
        List<Booking> booking = new ArrayList<>();
        User user = userService.getUser(userId);

        Pageable pageable = getPageable(from, size, Sort.by("start").descending());
        switch (state) {
            case CURRENT:
                booking.addAll(bookingRepository.findBookingsByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), LocalDateTime.now(), pageable));
                break;
            case REJECTED:
                booking.addAll(bookingRepository.findBookingsByItem_OwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, pageable));
                break;
            case WAITING:
                booking.addAll(bookingRepository.findBookingsByItem_OwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING, pageable));
                break;
            case PAST:
                booking.addAll(bookingRepository.findBookingsByItem_OwnerIdAndStatusInAndEndBeforeOrderByStartDesc(userId,
                        List.of(APPROVED, Status.WAITING), LocalDateTime.now(), pageable));
                break;
            case FUTURE:
                booking.addAll(bookingRepository.findBookingsByItem_OwnerIdAndStatusInAndStartAfterOrderByStartDesc(userId,
                        List.of(APPROVED, Status.WAITING), LocalDateTime.now(), pageable));
                break;
            default:
                booking.addAll(bookingRepository.findBookingsByItem_OwnerIdOrderByStartDesc(user.getId(), pageable));
                break;
        }

        return booking.stream()
                .map(b -> BookingMapper.mapBookingToBookingDto(b, userService.getUser(b.getBookerId())))
                .collect(Collectors.toList());
    }

    private void checkValidationTime(BookingRequest bookingDto) {
        LocalDateTime time = LocalDateTime.now().minusSeconds(5);
        if ((bookingDto.getStart() == null || bookingDto.getEnd() == null) || (bookingDto.getStart().isBefore(time) ||
                bookingDto.getEnd().isBefore(time) || bookingDto.getEnd().equals(bookingDto.getStart())
                || bookingDto.getStart().isAfter(bookingDto.getEnd()))) {
            throw new TimeException("Ошибка времени");
        }
    }

    private static Pageable getPageable(int from, int size, Sort sort) {
        return PageRequest.of(from / size, size, sort);
    }
}
