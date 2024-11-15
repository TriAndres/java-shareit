package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByItemOrderByCreatedDesc(Item item);

    @Query("""
            select c
            from Comment as c
            join c.item.owner as o
            where o.id = ?1
            order by c.item.id, c.created desc
            """)
    List<Comment> findCommentsForItemsOfOwner(Long ownerId);
}
