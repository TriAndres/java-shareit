package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.AddBookingRqDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto addNewBooking(Long bookerId, AddBookingRqDto newBookingDto);

    BookingDto updateStatus(Long userId, Long bookingId, Boolean approved);

    BookingDto findById(Long userId, Long bookingId);

    List<BookingDto> getUserBookings(Long userId, String status);

    List<BookingDto> getBookingsByItemsOwner(Long ownerId, String state);
}
