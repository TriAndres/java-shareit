package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.model.Booking;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ItemWithRelatedDataRow {
    private Item item;
    private Booking lastBooking;
    private Booking nextBooking;
    private Comment comment;
}
