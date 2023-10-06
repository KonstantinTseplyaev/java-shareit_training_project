package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserIdOrderByStartDesc(long userId);

    List<Booking> findBookingsByUserIdAndStartAfterOrderByStartDesc(long userId, LocalDateTime now);

    List<Booking> findByUserIdAndEndBeforeOrderByStartDesc(long userId, LocalDateTime now);

    List<Booking> findByUserIdAndStartBeforeAndEndAfterOrderByStartAsc(
            long userId, LocalDateTime now, LocalDateTime now2);

    List<Booking> findByUserIdAndStateOrderByStartDesc(long userId, State state);

    List<Booking> findByItemOwnerIdOrderByStartDesc(long userId);

    List<Booking> findBookingsByItemOwnerIdAndStartAfterOrderByStartDesc(long userId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(long userId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            long userId, LocalDateTime now, LocalDateTime now2);

    List<Booking> findByItemOwnerIdAndStateOrderByStartDesc(long userId, State state);

    List<Booking> findByItemId(long itemId);

    @Query(value = "select * from bookings as b " +
            "where item_id = ?1 " +
            "and b.time_from <= now() " +
            "and b.current_state = 'APPROVED' " +
            "order by b.time_from desc " +
            "limit 1", nativeQuery = true)
    Optional<Booking> findByItemIdAndLastBooking(long itemId);

    @Query(value = "select * from bookings as b " +
            "where item_id = ?1 " +
            "and b.time_from > now() " +
            "and b.current_state = 'APPROVED' " +
            "order by b.time_from asc " +
            "limit 1", nativeQuery = true)
    Optional<Booking> findByItemIdAndNextBooking(long itemId);

    boolean existsByUserIdAndItemIdAndEndBefore(long userId, long itemId, LocalDateTime now);
}
