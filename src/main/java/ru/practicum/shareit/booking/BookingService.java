package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingItemDto;

import java.util.List;

public interface BookingService {
    Booking createBooking(Long userId, BookingItemDto bookingItemDto);

    Booking checkRequest(Long userId, Long bookingId, String approved);

    Booking getBooking(Long userId, Long bookingId);

    List<Booking> getBookingsByStatus(Long userId, String state, Pageable pageable);

    List<Booking> getUserBookings(Long ownerId, String state, Pageable pageable);
}