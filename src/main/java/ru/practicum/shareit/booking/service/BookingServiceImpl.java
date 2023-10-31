package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
    private final ItemService itemService;
    private final UserRepository userRepository;

    @Override
    public BookingDto createBooking(BookingRequest request, Long userId) {
        if (request.getItemId() == null) {
            throw new ErrorException("Ошибка");
        }
        Item item = itemService.getItemById(request.getItemId(), userId);
        User user = getOfUser(userId);
        if (userService.getUser(userId) == null || (!userRepository.existsById(userId))
                || (item.getOwnerId() == null)) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (!item.getAvailable() || item.getAvailable() == null) {
            throw new ErrorException();
        }
        if (itemService.getItemById(request.getItemId(), userId) == null) {
            throw new NotFoundException("Вещь не найдена");
        }
        checkValidationTime(request);
        if (request.getId() != null) {
            Booking booking = bookingRepository.findById(request.getId()).orElse(null);
            if (booking != null) {
                throw new NotFoundException("Ошибка бронирования");
            }
        }
        if (Objects.equals(item.getOwnerId(), userId)) {
            throw new NotFoundException("Ошибка! Владелец вещи не может забронировать свою вещь");
        }
        Booking booking = BookingMapper.toBooking(request, item, user.getId());
        bookingRepository.save(booking);
        return BookingMapper.mapBookingToBookingDto(booking, user);
    }

    @Override
    public BookingDto approveBooking(Long bookingId, Boolean approved, Long userId) {
        Booking booking = getBooking(bookingId, userId);
        Item item = itemService.getItemById(booking.getItem().getId(), userId);
        User user = userService.getUser(userId);
        if (!Objects.equals(item.getOwnerId(), userId)) {
            throw new NotFoundException("Только владелец вещи может подтвердить бронирование");
        }
        if (booking.getStatus().equals(APPROVED)) {
            throw new ErrorException("Бронирование уже было создано" + bookingId);
        }
        User booker = userService.getUser(booking.getBookerId());
        booking.setStatus(approved ? APPROVED : Status.REJECTED);
        return BookingMapper.mapBookingToBookingDto(bookingRepository.save(booking), booker);
    }


    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        User user = userService.getUser(userId);
        Booking booking = getBooking(bookingId, userId);
        User booker = userService.getUser(booking.getBookerId());
        if (!Objects.equals(booking.getBookerId(), user.getId()) && !Objects.equals(booking.getItem().getOwnerId(), user.getId())) {
            throw new NotFoundException("Booking was not booked by user and item owner is different");
        }
        return bookingRepository.findById(bookingId)
                .map(b -> BookingMapper.mapBookingToBookingDto(b, booker))
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
    }

    @Override
    public List<BookingDto> getAllBookings(BookingState state, Long userId) {
        List<Booking> bookings = new ArrayList<>();
        User user = userService.getUser(userId);
        switch (state) {
            case CURRENT:
                bookings.addAll(bookingRepository.findBookingsByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), LocalDateTime.now()));
                break;
            case REJECTED:
                bookings.addAll(bookingRepository.findBookingsByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED));
                break;
            case WAITING:
                bookings.addAll(bookingRepository.findBookingsByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING));
                break;
            case PAST:
                bookings.addAll(bookingRepository.findBookingsByBookerIdAndStatusInAndEndBeforeOrderByStartDesc(userId,
                        List.of(APPROVED, Status.WAITING), LocalDateTime.now()));
                break;
            case FUTURE:
                List<Booking> bookingList = bookingRepository.findBookingsByBookerIdAndStatusInAndStartAfterOrderByStartDesc(userId,
                        List.of(APPROVED, Status.WAITING), LocalDateTime.now());

                bookings.addAll(bookingList);
                break;
            default:
                List<BookingState> booking = List.of(BookingState.values());
                if (!booking.contains(state)) {
                    throw new ErrorException("Unknown state: UNSUPPORTED_STATUS");
                }
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
        }
        return bookings.stream()
                .map(b -> BookingMapper.mapBookingToBookingDto(b, user))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllOwnerBooking(BookingState state, Long userId) {
        List<Booking> booking = new ArrayList<>();
        User user = userService.getUser(userId);
        switch (state) {
            case CURRENT:
                booking.addAll(bookingRepository.findBookingsByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), LocalDateTime.now()));
                break;
            case REJECTED:
                booking.addAll(bookingRepository.findBookingsByItem_OwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED));
                break;
            case WAITING:
                booking.addAll(bookingRepository.findBookingsByItem_OwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING));
                break;
            case PAST:
                booking.addAll(bookingRepository.findBookingsByItem_OwnerIdAndStatusInAndEndBeforeOrderByStartDesc(userId,
                        List.of(APPROVED, Status.WAITING), LocalDateTime.now()));
                break;
            case FUTURE:
                booking.addAll(bookingRepository.findBookingsByItem_OwnerIdAndStatusInAndStartAfterOrderByStartDesc(userId,
                        List.of(APPROVED, Status.WAITING), LocalDateTime.now()));
                break;
            default:
                booking.addAll(bookingRepository.findBookingsByItem_OwnerIdOrderByStartDesc(user.getId()));
                break;
        }

        return booking.stream()
                .map(b -> BookingMapper.mapBookingToBookingDto(b, userService.getUser(b.getBookerId())))
                .collect(Collectors.toList());
    }

    public void checkValidationTime(BookingRequest bookingDto) {
        LocalDateTime time = LocalDateTime.now().minusSeconds(5);
        if ((bookingDto.getStart() == null || bookingDto.getEnd() == null) || (bookingDto.getStart().isBefore(time) ||
                bookingDto.getEnd().isBefore(time) || bookingDto.getEnd().equals(bookingDto.getStart())
                || bookingDto.getStart().isAfter(bookingDto.getEnd()))) {
            throw new TimeException("Ошибка времени");
        }
    }

    private User getOfUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    public Booking getBooking(Long bookingId, Long userId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование для пользователя " + "не найдено", userId));
    }

}
