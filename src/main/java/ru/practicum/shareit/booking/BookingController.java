package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final BookingService bookingService;

    @PostMapping
    public Booking createBooking(@RequestHeader(USER_ID_HEADER) Long userId, @Valid @RequestBody BookingDto bookingDto) {
        log.info("Create booking");
        return bookingService.createBooking(userId, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public Booking getBooking(@RequestHeader(USER_ID_HEADER) Long userId, @PathVariable Long bookingId) {
        log.info("Get information by booking for owner item or booker only");
        return bookingService.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public Booking checkRequest(@RequestHeader(USER_ID_HEADER) Long userId, @PathVariable Long bookingId, @RequestParam String approved) {
        log.info("Check request booking");
        return bookingService.checkRequest(userId, bookingId, approved);
    }

    @GetMapping
    public List<Booking> getBookingsByStatus(@RequestHeader(USER_ID_HEADER) Long userId, @RequestParam(defaultValue = "ALL") String state) {
        log.info("Get list of user's bookings");
        return bookingService.getBookingsByStatus(userId, state);
    }

    @GetMapping("/owner")
    public List<Booking> getUserBookings(@RequestHeader(USER_ID_HEADER) Long userId, @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getUserBookings(userId, state);
    }
}