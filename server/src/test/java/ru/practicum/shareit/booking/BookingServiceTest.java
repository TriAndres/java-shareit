package ru.practicum.shareit.booking;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.AddBookingRqDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserIdDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("test")
@Transactional
public class BookingServiceTest {
    private final BookingService service;
    private final EntityManager em;

    private Item item;
    private User owner;
    private User requester;
    private User booker;
    private ItemRequest request;
    private Booking booking;

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
        booking = new Booking(null,
                              LocalDateTime.now().minusDays(3),
                              LocalDateTime.now().minusDays(2),
                              BookingStatus.APPROVED,
                              booker,
                              item);
        em.persist(booking);

        em.flush();
    }

    @Test
    void addNewBooking() {
        // given
        AddBookingRqDto bookingDto = new AddBookingRqDto(LocalDateTime.now().plusDays(1),
                                                         LocalDateTime.now().plusDays(2),
                                                         item.getId());

        // when
        BookingDto savedBookingDto = service.addNewBooking(booker.getId(), bookingDto);

        // then
        assertThat(savedBookingDto.getItem(), equalTo(ItemMapper.mapToItemBasicInfoDto(item)));
        assertThat(savedBookingDto.getBooker(), equalTo(new UserIdDto(booker.getId())));
        assertThat(savedBookingDto.getStart(), equalTo(bookingDto.getStart()));
        assertThat(savedBookingDto.getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(savedBookingDto.getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void updateStatus() {
        // given
        // when
        BookingDto bookingDto = service.updateStatus(owner.getId(), booking.getId(), false);

        // then
        assertThat(bookingDto.getStatus(), equalTo(BookingStatus.REJECTED));
        assertThat(bookingDto.getId(), equalTo(booking.getId()));
    }

    @Test
    void findById() {
        // given
        // when
        BookingDto foundDto = service.findById(owner.getId(), booking.getId());
        // then
        assertThat(foundDto.getId(), equalTo(booking.getId()));
    }

    @Test
    void getUserBookings() {
        // given
        // when
        List<BookingDto> foundDto = service.getUserBookings(booker.getId(), "all");
        // then
        assertThat(foundDto.size(), equalTo(1));
        assertThat(foundDto.getFirst().getId(), equalTo(booking.getId()));
    }

    @Test
    void getBookingsByItemsOwner() {
        // given
        // when
        List<BookingDto> foundDto = service.getBookingsByItemsOwner(owner.getId(), "all");
        // then
        assertThat(foundDto.size(), equalTo(1));
        assertThat(foundDto.getFirst().getId(), equalTo(booking.getId()));
    }
}
