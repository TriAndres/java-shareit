package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdAndItemIdAndStatusAndEndBefore(Long bookerId,
                                                               Long itemId,
                                                               BookingStatus status,
                                                               LocalDateTime endDate);

    @Query("""
            select b
            from Booking as b
            join b.item as i
            where i.id = ?1
                and not(b.start > ?3 or b.end < ?2)
            """)
    List<Booking> findByItemIdAndActiveInPeriod(Long itemId, LocalDateTime start, LocalDateTime end);

    @Query("""
            select b
            from Booking as b
            join b.booker as br
            where br.id = ?1 and (
               ?2 = 'ALL'
               or
               ?2 in ('WAITING', 'REJECTED', 'APPROVED') and ?2 = b.status
               or
               ?2 = 'CURRENT' and ?#{T(java.time.LocalDateTime).now()} between b.start and b.end
               or
               ?2 = 'FUTURE' and ?#{T(java.time.LocalDateTime).now()} < b.start
               or
               ?2 = 'PAST' and b.status = 'APPROVED' and ?#{T(java.time.LocalDateTime).now()} > b.end
            )
            order by b.end desc""")
    List<Booking> findByBookerIdAndState(Long bookerId, String state);

    @Query("""
            select b
            from Booking as b
            join b.item.owner as o
            where o.id = ?1 and (
               ?2 = 'ALL'
               or
               ?2 in ('WAITING', 'REJECTED', 'APPROVED') and ?2 = b.status
               or
               ?2 = 'CURRENT' and ?#{T(java.time.LocalDateTime).now()} between b.start and b.end
               or
               ?2 = 'FUTURE' and ?#{T(java.time.LocalDateTime).now()} < b.start
               or
               ?2 = 'PAST' and b.status = 'APPROVED' and ?#{T(java.time.LocalDateTime).now()} > b.end
            )
            order by b.end desc""")
    List<Booking> findByOwnerIdAndState(Long ownerId, String state);

    @Query("""
            select b
            from Booking as b
            join b.item as i on i = ?1
            where ?#{T(java.time.LocalDateTime).now()} < b.start
                and b.status <> 'REJECTED'
            """)
    Page<Booking> getNextBookings(Item item, Pageable pageable);

    @Query("""
            select b
            from Booking as b
            join b.item as i on i = ?1
            where ?#{T(java.time.LocalDateTime).now()} >= b.start
                and b.status = 'APPROVED'
            """)
    Page<Booking> getLastBookings(Item item, Pageable pageable);

    @Query("""
            select b
            from Booking as b
            join b.item.owner as o on o.id = ?1
                and b.start = (
                    select max(bb.start)
                    from Booking as bb
                    where bb.item = b.item
                        and bb.start <= ?2
                )
            """)
    List<Booking> findNearestPrevBookingsByItemOwner(Long ownerId, LocalDateTime date);

    @Query("""
            select b
            from Booking as b
            join b.item.owner as o on o.id = ?1
                and b.start = (
                    select min(bb.start)
                    from Booking as bb
                    where bb.item = b.item
                        and bb.start > ?2
                )
            """)
    List<Booking> findNearestNextBookingsByItemOwner(Long ownerId, LocalDateTime now);
}
