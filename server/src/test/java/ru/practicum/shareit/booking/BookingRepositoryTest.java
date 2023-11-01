/*package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@DataJpaTest
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookingRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository repository;
    private Booking booking1;
    private Booking booking2;
    private Booking booking3;
    private Booking booking4;
    private User owner;
    private User user;
    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .name("John")
                .email("john@example.com")
                .build();

        user = User.builder()
                .name("Jane")
                .email("jane@example.com")
                .build();

        item1 = Item.builder()
                .name("Баскетбольный мяч")
                .description("классный мяч")
                .available(true)
                .build();

        item2 = Item.builder()
                .name("Волейбольный мяч")
                .description("классный мяч")
                .available(true)
                .build();

        booking1 = Booking.builder()
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(2))
                .state(State.APPROVED)
                .build();

        booking2 = Booking.builder()
                .start(LocalDateTime.now().plusHours(3))
                .end(LocalDateTime.now().plusHours(5))
                .state(State.APPROVED)
                .build();

        booking3 = Booking.builder()
                .start(LocalDateTime.now().minusHours(3))
                .end(LocalDateTime.now().minusMinutes(1))
                .state(State.APPROVED)
                .build();

        booking4 = Booking.builder()
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().plusMinutes(2))
                .state(State.APPROVED)
                .build();
    }

    @Test
    void findByItemIdAndLastAndNextBookings_likeOptional() {
        User ow = em.persist(owner);
        User us = em.persist(user);
        item1.setOwner(ow);
        item2.setOwner(ow);
        Item i1 = em.persist(item1);
        Item i2 = em.persist(item2);
        booking1.setUser(us);
        booking2.setUser(us);
        booking3.setUser(us);
        booking4.setUser(us);
        booking1.setItem(i1);
        booking2.setItem(i1);
        booking3.setItem(i2);
        booking4.setItem(i2);
        booking1.setItemOwnerId(ow.getId());
        booking2.setItemOwnerId(ow.getId());
        booking3.setItemOwnerId(ow.getId());
        booking4.setItemOwnerId(ow.getId());
        Booking b1 = em.persist(booking1);
        Booking b2 = em.persist(booking2);
        Booking b3 = em.persist(booking3);
        Booking b4 = em.persist(booking4);

        Optional<Booking> lastIt1 = repository.findByItemIdAndLastBooking(i1.getId());

        Optional<Booking> nextIt1 = repository.findByItemIdAndNextBooking(i1.getId());

        Optional<Booking> lastIt2 = repository.findByItemIdAndLastBooking(i2.getId());

        Optional<Booking> nextIt2 = repository.findByItemIdAndNextBooking(i2.getId());

        assertEquals(b1, lastIt1.get());

        assertEquals(b2, nextIt1.get());

        assertEquals(b3, lastIt2.get());

        assertEquals(b4, nextIt2.get());
    }

    @Test
    void findByItemIdAndLastAndNextBookings_likeList() {
        User ow = em.persist(owner);
        User us = em.persist(user);
        item1.setOwner(ow);
        item2.setOwner(ow);
        Item i1 = em.persist(item1);
        Item i2 = em.persist(item2);
        booking1.setUser(us);
        booking2.setUser(us);
        booking3.setUser(us);
        booking4.setUser(us);
        booking1.setItem(i1);
        booking2.setItem(i1);
        booking3.setItem(i2);
        booking4.setItem(i2);
        booking1.setItemOwnerId(ow.getId());
        booking2.setItemOwnerId(ow.getId());
        booking3.setItemOwnerId(ow.getId());
        booking4.setItemOwnerId(ow.getId());
        Booking b1 = em.persist(booking1);
        Booking b2 = em.persist(booking2);
        Booking b3 = em.persist(booking3);
        Booking b4 = em.persist(booking4);

        List<Booking> lastList = repository.findAllByItemIdAndLastBooking(Set.of(i1.getId(), i2.getId()));

        List<Booking> nextList = repository.findAllByItemIdAndNextBooking(Set.of(i1.getId(), i2.getId()));

        assertEquals(2, lastList.size());
        assertTrue(lastList.contains(b1));
        assertTrue(lastList.contains(b3));

        assertEquals(2, nextList.size());
        assertTrue(nextList.contains(b2));
        assertTrue(nextList.contains(b4));
    }
}*/
