package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.AddBookingRqDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
public class BookingControllerTest {
    private BookingService service;
    private BookingController controller;

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;

    private User owner;
    private User booker;
    private User requester;
    private ItemRequest request;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        service = new BookingServiceImpl(bookingRepository, itemRepository, userRepository);
        controller = new BookingController(service);
        mvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(ErrorHandler.class).build();

        mapper.registerModule(new JavaTimeModule());

        owner = new User(1L, "Owner", "owner@mail.com");
        booker = new User(2L, "Booker", "booker@mail.com");
        item = new Item(1L, "Item", "Item description", true, owner, request);
        requester = new User(2L, "Requester", "requester@mail.com");
        request = new ItemRequest(1L, "Request description", requester, LocalDateTime.now());
        booking = new Booking(1L,
                              LocalDateTime.now().minusDays(2),
                              LocalDateTime.now().minusDays(1),
                              BookingStatus.APPROVED,
                              booker,
                              item);
    }

    @Test
    void addNewBooking() throws Exception {
        when(itemRepository.findByIdAndOwnerIdNot(any(), any())).thenReturn(Optional.of(item));
        when(userRepository.findById(any())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByItemIdAndActiveInPeriod(any(), any(), any())).thenReturn(new ArrayList<>());
        when(bookingRepository.save(any())).thenReturn(booking);

        AddBookingRqDto bookingDto = new AddBookingRqDto(LocalDateTime.now().minusDays(2),
                                                         LocalDateTime.now().minusDays(1),
                                                         item.getId());

        mvc.perform(post("/bookings").content(mapper.writeValueAsString(bookingDto))
                            .header("X-Sharer-User-Id", booker.getId())
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(booking.getStatus().name())));
    }

    @Test
    void updateStatus() throws Exception {
        Booking updatedBooking = new Booking(1L,
                                             LocalDateTime.now().minusDays(2),
                                             LocalDateTime.now().minusDays(1),
                                             BookingStatus.REJECTED,
                                             booker,
                                             item);

        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(updatedBooking);

        mvc.perform(patch("/bookings/{id}", booking.getId()).param("approved", "false")
                            .header("X-Sharer-User-Id", owner.getId())
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedBooking.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(updatedBooking.getStatus().name())));
    }

    @Test
    void findById() throws Exception {
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));

        mvc.perform(get("/bookings/{id}", booking.getId()).characterEncoding(StandardCharsets.UTF_8)
                            .header("X-Sharer-User-Id", booker.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(booking.getStatus().name())));
    }

    @Test
    void getUserBookings() throws Exception {
        when(bookingRepository.findByBookerIdAndState(any(), any())).thenReturn(List.of(booking));
        mvc.perform(get("/bookings").characterEncoding(StandardCharsets.UTF_8)
                            .header("X-Sharer-User-Id", booker.getId())
                            .param("state", "ALL")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(booking.getStatus().name())));
    }

    @Test
    void getBookingsByItemsOwner() throws Exception {
        when(bookingRepository.findByOwnerIdAndState(any(), any())).thenReturn(List.of(booking));
        mvc.perform(get("/bookings/owner").characterEncoding(StandardCharsets.UTF_8)
                            .header("X-Sharer-User-Id", owner.getId())
                            .param("state", "ALL")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(booking.getStatus().name())));
    }
}
