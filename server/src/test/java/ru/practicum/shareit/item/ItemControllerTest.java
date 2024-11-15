package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.item.dto.AddCommentRqDto;
import ru.practicum.shareit.item.dto.AddItemRqDto;
import ru.practicum.shareit.item.dto.UpdateItemRqDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRequestRepository requestRepository;

    private ItemService service;
    private ItemController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private User owner;
    private User requester;
    private User booker;
    private Item item;
    private ItemRequest request;
    private Booking lastBooking;
    private Booking nextBooking;
    private Comment comment;


    @BeforeEach
    void setUp() {
        service = new ItemServiceImpl(itemRepository,
                                      commentRepository,
                                      userRepository,
                                      bookingRepository,
                                      requestRepository);
        controller = new ItemController(service);
        mvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(ErrorHandler.class).build();

        owner = new User(1L, "Owner", "owner@mail.com");
        requester = new User(2L, "Requester", "requester@mail.com");
        booker = new User(3L, "Booker", "booker@mail.com");
        request = new ItemRequest(1L, "Request description", requester, LocalDateTime.now());
        item = new Item(1L, "Item", "Item description", true, owner, request);
        lastBooking = new Booking(1L,
                                  LocalDateTime.now().minusDays(2),
                                  LocalDateTime.now().minusDays(1),
                                  BookingStatus.APPROVED,
                                  booker,
                                  item);
        nextBooking = new Booking(2L,
                                  LocalDateTime.now().plusDays(1),
                                  LocalDateTime.now().plusDays(2),
                                  BookingStatus.APPROVED,
                                  booker,
                                  item);
        comment = new Comment(1L, "Comment", item, booker, LocalDateTime.now());
    }

    @Test
    void addNewItem() throws Exception {
        when(userRepository.findById(any())).thenReturn(Optional.of(owner));
        when(requestRepository.findById(any())).thenReturn(Optional.of(request));
        when(itemRepository.save(any())).thenReturn(item);

        AddItemRqDto itemDto = new AddItemRqDto(item.getName(),
                                                item.getDescription(),
                                                item.getAvailable(),
                                                request.getId());

        mvc.perform(post("/items").content(mapper.writeValueAsString(itemDto))
                            .header("X-Sharer-User-Id", 1L)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())));
    }

    @Test
    void findById() throws Exception {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.getLastBookings(any(Item.class),
                                               any())).thenReturn(new PageImpl<>(List.of(lastBooking)));
        when(bookingRepository.getNextBookings(any(Item.class),
                                               any())).thenReturn(new PageImpl<>(List.of(nextBooking)));
        when(commentRepository.findByItemOrderByCreatedDesc(item)).thenReturn(List.of(comment));

        mvc.perform(get("/items/{id}", 1L).characterEncoding(StandardCharsets.UTF_8)
                            .header("X-Sharer-User-Id", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())));
    }

    @Test
    void findAll() throws Exception {
        when(itemRepository.findByOwnerId(any(), any())).thenReturn(List.of(item));
        when(bookingRepository.findNearestPrevBookingsByItemOwner(any(), any())).thenReturn(List.of(lastBooking));
        when(bookingRepository.findNearestNextBookingsByItemOwner(any(), any())).thenReturn(List.of(nextBooking));
        when(commentRepository.findCommentsForItemsOfOwner(any())).thenReturn(List.of(comment));

        mvc.perform(get("/items").characterEncoding(StandardCharsets.UTF_8)
                            .header("X-Sharer-User-Id", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(item.getName())))
                .andExpect(jsonPath("$[0].description", is(item.getDescription())));
    }

    @Test
    void search() throws Exception {
        when(itemRepository.findAvailableItemsByNameAndDescriptionIgnoreCase(any())).thenReturn(List.of(item));

        mvc.perform(get("/items/search").param("text", "Item")
                            .characterEncoding(StandardCharsets.UTF_8)
                            .header("X-Sharer-User-Id", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(item.getName())))
                .andExpect(jsonPath("$[0].description", is(item.getDescription())));
    }

    @Test
    void updateItemById() throws Exception {
        UpdateItemRqDto updateItemDto = new UpdateItemRqDto("Updated Item", "Updated description", true);
        Item updatedItem = new Item(1L,
                                    updateItemDto.getName(),
                                    updateItemDto.getDescription(),
                                    updateItemDto.getAvailable(),
                                    owner,
                                    request);

        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(updatedItem);

        mvc.perform(patch("/items/{id}", 1L).content(mapper.writeValueAsString(updateItemDto))
                            .header("X-Sharer-User-Id", 1L)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedItem.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updatedItem.getName())))
                .andExpect(jsonPath("$.description", is(updatedItem.getDescription())));
    }

    @Test
    void addNewComment() throws Exception {
        AddCommentRqDto addCommentRqDto = new AddCommentRqDto("Comment");
        when(bookingRepository.findByBookerIdAndItemIdAndStatusAndEndBefore(any(),
                                                                            any(),
                                                                            any(),
                                                                            any())).thenReturn(List.of(lastBooking));
        when(commentRepository.save(any())).thenReturn(comment);

        mvc.perform(post("/items/{id}/comment", 1L).content(mapper.writeValueAsString(addCommentRqDto))
                            .header("X-Sharer-User-Id", 1L)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(comment.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(comment.getText())));
    }
}
