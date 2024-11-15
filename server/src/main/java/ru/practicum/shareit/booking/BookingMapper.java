package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.AddBookingRqDto;
import ru.practicum.shareit.booking.dto.BookingBasicInfoDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserIdDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookingMapper {
    public static Booking mapToBooking(User booker, Item item, AddBookingRqDto newBookingDto) {
        return new Booking(null, newBookingDto.getStart(), newBookingDto.getEnd(), BookingStatus.WAITING, booker, item);
    }

    public static BookingDto mapToBookingDto(Booking booking) {
        Item item = booking.getItem();
        return new BookingDto(booking.getId(),
                              booking.getStart(),
                              booking.getEnd(),
                              booking.getStatus(),
                              new UserIdDto(booking.getBooker().getId()),
                              ItemMapper.mapToItemBasicInfoDto(item));
    }

    public static BookingBasicInfoDto mapToBookingBasicInfoDto(Booking booking) {
        return new BookingBasicInfoDto(Optional.ofNullable(booking).map(Booking::getId).orElse(null),
                                       Optional.ofNullable(booking).map((b) -> b.getBooker().getId()).orElse(null));
    }

    public static List<BookingDto> mapToBookingDto(List<Booking> bookings) {
        List<BookingDto> bookingDtos = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingDtos.add(mapToBookingDto(booking));
        }
        return bookingDtos;
    }
}
