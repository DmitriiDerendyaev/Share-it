package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingItemDto;

import javax.validation.constraints.Min;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private final String userIDHeader  = "X-Sharer-User-Id";

    private final BookingService bookingService;

    @PostMapping()
    public Booking createBooking(@RequestHeader(userIDHeader) Long userId, @RequestBody BookingItemDto bookingItemDto) {
        log.info("Create booking");
        return bookingService.createBooking(userId, bookingItemDto);
    }

    @GetMapping("/{bookingId}")
    public Booking getBooking(@RequestHeader(userIDHeader) Long userId, @PathVariable Long bookingId) {
        log.info("Get information by booking for owner item or booker only");
        return bookingService.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public Booking checkRequest(@RequestHeader(userIDHeader) Long userId, @PathVariable Long bookingId, @RequestParam String approved) {
        log.info("Check request booking");
        return bookingService.checkRequest(userId, bookingId, approved);
    }

    @GetMapping
    public List<Booking> getBookingsByStatus(@RequestHeader(userIDHeader) Long userId, @RequestParam(defaultValue = "ALL") String state,
                                             @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                             @RequestParam(defaultValue = "10") @Min(1) Integer size) {

        log.info("Get list of user's bookings");
        return bookingService.getBookingsByStatus(userId, state,
                PageRequest.of(from / size, size, Sort.by("start").descending()));
    }

    @GetMapping("/owner")
    public List<Booking> getUserBookings(@RequestHeader(userIDHeader) Long userId, @RequestParam(defaultValue = "ALL") String state,
                                         @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                         @RequestParam(defaultValue = "10") @Min(1) Integer size) {

        return bookingService.getUserBookings(userId, state, PageRequest.of(from / size, size, Sort.by("start").descending()));
    }
}