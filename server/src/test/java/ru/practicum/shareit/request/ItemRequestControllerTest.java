package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.AddItemRequestRqDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestRepository requestRepository;

    private ItemRequestService service;
    private ItemRequestController controller;

    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;

    private User owner;
    private User booker;
    private User requester;
    private ItemRequest request;
    private Item item;

    @BeforeEach
    void setUp() {
        service = new ItemRequestServiceImpl(requestRepository, userRepository, itemRepository);
        controller = new ItemRequestController(service);
        mvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(ErrorHandler.class).build();

        owner = new User(1L, "Owner", "owner@mail.com");
        booker = new User(3L, "Booker", "booker@mail.com");
        requester = new User(2L, "Requester", "requester@mail.com");
        request = new ItemRequest(1L, "Request description", requester, LocalDateTime.now());
        item = new Item(1L, "Item", "Item description", true, owner, request);
    }

    @Test
    void addNewItemRequest() throws Exception {
        when(userRepository.findById(any())).thenReturn(Optional.of(booker));
        when(requestRepository.save(any())).thenReturn(request);

        AddItemRequestRqDto itemRequestDto = new AddItemRequestRqDto("Request description");

        mvc.perform(post("/requests").content(mapper.writeValueAsString(itemRequestDto))
                            .header("X-Sharer-User-Id", requester.getId())
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(request.getDescription())));
    }

    @Test
    void findAll() throws Exception {
        when(itemRepository.findByRequestRequesterId(any(), any())).thenReturn(List.of(item));
        when(requestRepository.findByRequesterId(any(), any())).thenReturn(List.of(request));

        mvc.perform(get("/requests").characterEncoding(StandardCharsets.UTF_8)
                            .header("X-Sharer-User-Id", requester.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(request.getDescription())));
    }

    @Test
    void findById() throws Exception {
        when(requestRepository.findById(any())).thenReturn(Optional.of(request));
        when(itemRepository.findByRequestId(any(), any())).thenReturn(List.of(item));

        mvc.perform(get("/requests/{id}", 1L).characterEncoding(StandardCharsets.UTF_8)
                            .header("X-Sharer-User-Id", requester.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(request.getDescription())));
    }

    @Test
    void findAllItemRequestsFromOtherUsers() throws Exception {
        when(requestRepository.findByRequesterIdNot(any(), any())).thenReturn(List.of(request));
        when(itemRepository.findByRequestIn(any(), any())).thenReturn(List.of(item));

        mvc.perform(get("/requests/all").characterEncoding(StandardCharsets.UTF_8)
                            .header("X-Sharer-User-Id", owner.getId())
                            .param("from", "0")
                            .param("size", "10")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(request.getDescription())));
    }

}
