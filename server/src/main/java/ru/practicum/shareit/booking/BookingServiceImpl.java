package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.AddBookingRqDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStateForSearching;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingDto addNewBooking(Long bookerId, AddBookingRqDto newBookingDto) {
        Item item = itemRepository.findByIdAndOwnerIdNot(newBookingDto.getItemId(), bookerId)
                .orElseThrow(() -> new NotFoundException("Не найдена вещь с id = " +
                                                                 newBookingDto.getItemId() +
                                                                 ", которую мог бы забронировать пользователь с id = " +
                                                                 bookerId));

        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь id = " + bookerId));

        Booking booking = BookingMapper.mapToBooking(booker, item, newBookingDto);
        List<Booking> existingBookings = bookingRepository.findByItemIdAndActiveInPeriod(item.getId(),
                                                                                         booking.getStart(),
                                                                                         booking.getEnd());
        if (!item.getAvailable() || !existingBookings.isEmpty()) {
            throw new BadRequestException("Вещь с id = " + item.getId() + " недоступна");
        }

        return BookingMapper.mapToBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto updateStatus(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Не найдено бронирование с id = " + bookingId));

        BookingStatus newStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        if (booking.getStatus() == newStatus) {
            throw new BadRequestException("Передан неправильный новый статус бронирования");
        }
        Item item = booking.getItem();
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new BadRequestException("Указан неправильный владелец вещи");
        }

        booking.setStatus(newStatus);

        return BookingMapper.mapToBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto findById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Не найдено бронирование с id = " + bookingId));
        if (!Objects.equals(booking.getBooker().getId(), userId) &&
                !Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new NotFoundException("Просматривать бронирование может только инициатор или владелец вещи");
        }
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, String stateString) {
        BookingStateForSearching state = getBookingStateForSearching(stateString);
        return BookingMapper.mapToBookingDto(bookingRepository.findByBookerIdAndState(userId, state.name()));
    }

    private BookingStateForSearching getBookingStateForSearching(String stateString) {
        BookingStateForSearching state;
        try {
            state = BookingStateForSearching.valueOf(stateString.toUpperCase());
        } catch (Exception e) {
            throw new BadRequestException("Unknown state: " + stateString);
        }
        return state;
    }

    @Override
    public List<BookingDto> getBookingsByItemsOwner(Long ownerId, String stateString) {
        BookingStateForSearching state = getBookingStateForSearching(stateString);
        List<BookingDto> result = BookingMapper.mapToBookingDto(bookingRepository.findByOwnerIdAndState(ownerId,
                                                                                                        state.name()));
        if (result.isEmpty()) {
            throw new NotFoundException("Не найдено бронирований вещей");
        }
        return result;
    }
}
