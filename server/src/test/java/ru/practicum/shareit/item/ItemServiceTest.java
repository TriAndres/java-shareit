package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.AddCommentRqDto;
import ru.practicum.shareit.item.dto.AddItemRqDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithRelatedDataDto;
import ru.practicum.shareit.item.dto.UpdateItemRqDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("test")
@Transactional
public class ItemServiceTest {
    Item item;
    User owner;
    User requester;
    User booker;
    ItemRequest request;
    Booking booking;

    private final EntityManager em;
    private final ItemService service;

    @BeforeEach
    void setUp() {
        owner = new User(null, "Owner Иванов", "some@email.com");
        em.persist(owner);
        requester = new User(null, "Requester Петров", "someone@email.com");
        em.persist(requester);
        request = new ItemRequest(null, "Description 1", requester, LocalDateTime.now());
        em.persist(request);
        item = new Item(null, "Item", "Description", true, owner, request);
        em.persist(item);
        booker = new User(null, "Booker Сидоров", "someoneelse@email.com");
        em.persist(booker);
        booking = new Booking(null, LocalDateTime.now().minusDays(1), LocalDateTime.now(), BookingStatus.APPROVED, booker, item);
        em.persist(booking);

        em.flush();
    }

    @Test
    void addNewItem() {
        // given
        AddItemRqDto itemDto = new AddItemRqDto("Item1", "Description 1", true, request.getId());
        // when
        ItemDto savedDto = service.addNewItem(owner.getId(), itemDto);
        // then
        assertThat(savedDto.getId(), notNullValue());
        assertThat(savedDto.getName(), equalTo(itemDto.getName()));
        assertThat(savedDto.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(savedDto.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(savedDto.getRequestId(), equalTo(request.getId()));
        assertThat(savedDto.getOwnerId(), equalTo(owner.getId()));
    }

    @Test
    void updateItemById() {
        // given
        AddItemRqDto itemDto = new AddItemRqDto("Item1", "Description 1", true, request.getId());
        ItemDto savedDto = service.addNewItem(owner.getId(), itemDto);
        // when
        UpdateItemRqDto updateItemRqDto = new UpdateItemRqDto("Updated Item1", "Updated Description 1", false);
        ItemDto updatedItem = service.updateItemById(owner.getId(), savedDto.getId(), updateItemRqDto);
        // then
        assertThat(updatedItem.getId(), notNullValue());
        assertThat(updatedItem.getName(), equalTo(updateItemRqDto.getName()));
        assertThat(updatedItem.getDescription(), equalTo(updateItemRqDto.getDescription()));
        assertThat(updatedItem.getAvailable(), equalTo(updateItemRqDto.getAvailable()));
        assertThat(updatedItem.getRequestId(), equalTo(request.getId()));
        assertThat(updatedItem.getOwnerId(), equalTo(owner.getId()));
    }

    @Test
    void findById() {
        // given
        AddItemRqDto itemDto = new AddItemRqDto("Item1", "Description 1", true, request.getId());
        ItemDto savedDto = service.addNewItem(owner.getId(), itemDto);
        // when
        ItemWithRelatedDataDto foundDto = service.findById(owner.getId(), savedDto.getId());
        // then
        assertThat(foundDto.getId(), notNullValue());
        assertThat(foundDto.getName(), equalTo(itemDto.getName()));
        assertThat(foundDto.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(foundDto.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(foundDto.getRequestId(), equalTo(request.getId()));
        assertThat(foundDto.getOwnerId(), equalTo(owner.getId()));
    }

    @Test
    void findAllWithRelatedDataByOwner() {
        // given
        AddItemRqDto itemDto = new AddItemRqDto("Item1", "Description 1", true, request.getId());
        service.addNewItem(owner.getId(), itemDto);
        // when
        List<ItemWithRelatedDataDto> foundDto = service.findAllWithRelatedDataByOwner(owner.getId());
        // then
        assertThat(foundDto.size(), equalTo(2));
        assertThat(foundDto.getFirst().getId(), notNullValue());
        assertThat(foundDto.getFirst().getName(), equalTo(item.getName()));
        assertThat(foundDto.getFirst().getDescription(), equalTo(item.getDescription()));
        assertThat(foundDto.getFirst().getAvailable(), equalTo(item.getAvailable()));
        assertThat(foundDto.getFirst().getRequestId(), equalTo(request.getId()));
        assertThat(foundDto.getFirst().getOwnerId(), equalTo(owner.getId()));
    }

    @Test
    void search() {
        // given
        AddItemRqDto itemDto = new AddItemRqDto("Item1", "Description 1", true, request.getId());
        service.addNewItem(owner.getId(), itemDto);
        // when
        List<ItemDto> foundDto = service.search(owner.getId(), "DESC%1");
        // then
        assertThat(foundDto.getFirst().getId(), notNullValue());
        assertThat(foundDto.getFirst().getName(), equalTo(itemDto.getName()));
        assertThat(foundDto.getFirst().getDescription(), equalTo(itemDto.getDescription()));
        assertThat(foundDto.getFirst().getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(foundDto.getFirst().getRequestId(), equalTo(request.getId()));
        assertThat(foundDto.getFirst().getOwnerId(), equalTo(owner.getId()));
    }

    @Test
    void addNewComment() {
        // when
        AddCommentRqDto commentDto = new AddCommentRqDto("Comment 1");
        CommentDto savedCommentDto = service.addNewComment(booker.getId(),
                                                      item.getId(), commentDto);
        // then
        assertThat(savedCommentDto.getId(), notNullValue());
        assertThat(savedCommentDto.getAuthorName(), equalTo(booker.getName()));
        assertThat(savedCommentDto.getText(), equalTo(commentDto.getText()));
    }
}
