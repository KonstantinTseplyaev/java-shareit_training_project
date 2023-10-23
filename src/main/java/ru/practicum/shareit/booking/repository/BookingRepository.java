package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(long userId, Pageable pageable);

    List<Booking> findBookingsByUserIdAndStartAfter(long userId, LocalDateTime now, Pageable pageable);

    List<Booking> findByUserIdAndEndBefore(long userId, LocalDateTime now, Pageable pageable);

    List<Booking> findByUserIdAndStartBeforeAndEndAfterOrderByStartAsc(
            long userId, LocalDateTime now, LocalDateTime now2, Pageable pageable);

    List<Booking> findByUserIdAndState(long userId, State state, Pageable pageable);

    List<Booking> findByItemOwnerId(long userId, Pageable pageable);

    List<Booking> findBookingsByItemOwnerIdAndStartAfter(
            long userId, LocalDateTime now, Pageable pageable);

    List<Booking> findByItemOwnerIdAndEndBefore(long userId, LocalDateTime now, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfter(
            long userId, LocalDateTime now, LocalDateTime now2, Pageable pageable);

    List<Booking> findByItemOwnerIdAndState(long userId, State state, Pageable pageable);

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

    @Query(value = "select * from bookings as b " +
            "where b.item_id in ?1 " +
            "and b.time_from in " +
            "(select max_time from (select lb.item_id as item, MAX(lb.time_from) as max_time " +
            "from (select * from bookings where time_from <= now() and current_state = 'APPROVED') as lb " +
            "group by lb.item_id));", nativeQuery = true)
    List<Booking> findAllByItemIdAndLastBooking(Set<Long> itemId);

    @Query(value = "select * from bookings as b " +
            "where b.item_id in ?1 " +
            "and b.time_from in " +
            "(select max_time from (select lb.item_id as item, MIN(lb.time_from) as max_time " +
            "from (select * from bookings where time_from > now() and current_state = 'APPROVED') as lb " +
            "group by lb.item_id));", nativeQuery = true)
    List<Booking> findAllByItemIdAndNextBooking(Set<Long> itemId);

    boolean existsByUserIdAndItemIdAndEndBefore(long userId, long itemId, LocalDateTime now);
}
