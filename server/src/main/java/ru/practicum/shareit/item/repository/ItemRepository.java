package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(value = "select * " +
            "from items as i " +
            "where upper(i.name) like %?1% " +
            "or upper(i.description) like %?1% " +
            "and i.available = true", nativeQuery = true)
    List<Item> findByNameOrDescriptionContaining(String text, Pageable pageable);

    Optional<Item> findByIdAndOwnerId(long itemId, long ownerId);

    List<Item> findByOwnerId(long ownerId, Pageable pageable);

    List<Item> findAllByRequestIdIn(Set<Long> requestsId);

    List<Item> findByRequestId(long requestId);
}
