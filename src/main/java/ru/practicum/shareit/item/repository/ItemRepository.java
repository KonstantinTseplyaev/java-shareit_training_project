package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByUserIdOrderById(long userId);

    @Query(value = "select * " +
            "from items as i " +
            "where upper(i.name) like %?1% " +
            "or upper(i.description) like %?1% " +
            "and i.available = true", nativeQuery = true)
    List<Item> findByNameOrDescriptionContaining(String text);
}
