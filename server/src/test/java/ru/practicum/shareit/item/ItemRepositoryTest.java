package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ItemRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository repository;
    private User owner;
    private Item item1;
    private Item item2;
    private Item item3;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .name("John")
                .email("john@example.com")
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

        item3 = Item.builder()
                .name("Бейсбольный мяч")
                .description("неплохой мяч")
                .available(true)
                .build();
    }

    @Test
    void findByNameOrDescriptionContaining_whenTextIsBlank() {
        Pageable pageable = PageRequest.of(0, 5);
        User own = em.persist(owner);
        item1.setOwner(own);
        item2.setOwner(own);
        item3.setOwner(own);
        em.persist(item1);
        em.persist(item2);
        em.persist(item3);

        List<Item> result = repository.findByNameOrDescriptionContaining("     ", pageable);

        assertTrue(result.isEmpty());
    }

    @Test
    void findByNameOrDescriptionContaining_whenDescriptionContainingText() {
        Pageable pageable = PageRequest.of(0, 5);
        User own = em.persist(owner);
        item1.setOwner(own);
        item2.setOwner(own);
        item3.setOwner(own);
        Item i1 = em.persist(item1);
        Item i2 = em.persist(item2);
        em.persist(item3);

        List<Item> result = repository.findByNameOrDescriptionContaining("КЛАССНЫЙ", pageable);

        assertEquals(2, result.size());
        assertTrue(result.contains(i1));
        assertTrue(result.contains(i2));
    }

    @Test
    void findByNameOrDescriptionContaining_whenNameContainingText() {
        Pageable pageable = PageRequest.of(0, 5);
        User own = em.persist(owner);
        item1.setOwner(own);
        item2.setOwner(own);
        item3.setOwner(own);
        Item i1 = em.persist(item1);
        em.persist(item2);
        em.persist(item3);

        List<Item> result = repository.findByNameOrDescriptionContaining("АСКЕТ", pageable);

        assertEquals(1, result.size());
        assertTrue(result.contains(i1));
    }

    @Test
    void findByNameOrDescriptionContaining_whenPageSizeEqualOne() {
        Pageable pageable = PageRequest.of(0, 1);
        User own = em.persist(owner);
        item1.setOwner(own);
        item2.setOwner(own);
        item3.setOwner(own);
        Item i1 = em.persist(item1);
        em.persist(item2);
        em.persist(item3);

        List<Item> result = repository.findByNameOrDescriptionContaining("МЯЧ", pageable);

        assertEquals(1, result.size());
        assertTrue(result.contains(i1));
    }
}
