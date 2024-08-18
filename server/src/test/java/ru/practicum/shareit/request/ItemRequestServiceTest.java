package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.AddItemRequestRqDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("test")
@Transactional
public class ItemRequestServiceTest {
    private final ItemRequestService service;
    private final EntityManager em;

    Item item;
    User owner;
    User requester;
//    User booker;
    ItemRequest request;

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

        em.flush();
    }

    @Test
    void addNewItemRequest() {
        // given
        AddItemRequestRqDto newRequestDto = new AddItemRequestRqDto("Request 1");

        // when
        ItemRequestDto savedRequestDto = service.addNewItemRequest(requester.getId(), newRequestDto);

        // then
        assertThat(savedRequestDto.getId(), notNullValue());
        assertThat(savedRequestDto.getItems().size(), equalTo(0));
        assertThat(savedRequestDto.getRequesterName(), equalTo(requester.getName()));
    }

    @Test
    void findAllUserItemRequests() {
        // given
        // when
        List<ItemRequestDto> foundDto = service.findAllUserItemRequests(requester.getId());

        // then
        assertThat(foundDto.size(), equalTo(1));
        assertThat(foundDto.getFirst().getRequesterName(), equalTo(requester.getName()));
    }

    @Test
    void findItemRequestById() {
        // given
        // when
        ItemRequestDto foundDto = service.findItemRequestById(requester.getId(), request.getId());

        // then
        assertThat(foundDto.getId(), equalTo(request.getId()));
        assertThat(foundDto.getRequesterName(), equalTo(requester.getName()));
    }

    @Test
    void findAllItemRequestsFromOtherUsers() {
        // given
        // when
        List<ItemRequestDto> foundDto = service.findAllItemRequestsFromOtherUsers(owner.getId(), 0, 10);

        // then
        assertThat(foundDto.size(), equalTo(1));
        assertThat(foundDto.getFirst().getRequesterName(), equalTo(requester.getName()));
    }
}
