package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findBookingsByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findBookingsByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findBookingsByBookerIdAndStatusOrderByStartDesc(Long bookerId, Status status, Pageable pageable);

    List<Booking> findBookingsByItem_OwnerIdAndStatusOrderByStartDesc(Long bookerId, Status status, Pageable pageable);

    List<Booking> findBookingsByBookerIdAndStatusInAndEndBeforeOrderByStartDesc(Long bookerId, List<Status> status, LocalDateTime end, Pageable pageable);

    List<Booking> findBookingsByItem_OwnerIdAndStatusInAndEndBeforeOrderByStartDesc(Long bookerId, List<Status> status, LocalDateTime end, Pageable pageable);

    List<Booking> findBookingsByBookerIdAndStatusInAndStartAfterOrderByStartDesc(Long bookerId, List<Status> status, LocalDateTime start, Pageable pageable);

    List<Booking> findBookingsByItem_OwnerIdAndStatusInAndStartAfterOrderByStartDesc(Long bookerId, List<Status> status, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long ownerId, Pageable pageable);

    List<Booking> findBookingsByItem_OwnerIdOrderByStartDesc(@Param("ownerId") Long ownerId, Pageable pageable);

    List<Booking> findBookingsByBookerIdAndItemAndEndBeforeAndStatus(Long bookerId, Item item, LocalDateTime end, Status status);

    @Query("select b from Booking as b " +
            "where b.item.id = :itemId " +
            "and (b.status = ru.practicum.shareit.booking.model.Status.APPROVED or " +
            "b.status = ru.practicum.shareit.booking.model.Status.WAITING) " +
            "and b.start = " +
            "(select max(bMax.start) from Booking  as bMax " +
            "where bMax.item.id = :itemId " +
            "and bMax.start <= current_timestamp )")
    Optional<Booking> findLastBooking(long itemId);

    @Query("select b from Booking as b " +
            "where b.item.id = :itemId " +
            "and (b.status = ru.practicum.shareit.booking.model.Status.APPROVED or " +
            "b.status = ru.practicum.shareit.booking.model.Status.WAITING) " +
            "and b.start = " +
            "(select min(bMin.start)  from Booking as bMin " +
            "where bMin.item.id = :itemId " +
            "and bMin.start >= current_timestamp )")
    Optional<Booking> findNextBooking(long itemId);


}
