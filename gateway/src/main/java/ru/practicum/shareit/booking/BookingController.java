package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

	private final String userIDHeader  = "X-Sharer-User-Id";

    private final BookingClient bookingClient;

	@PostMapping()
	public ResponseEntity<Object> createBooking(@RequestHeader(userIDHeader) Long userId, @Valid @RequestBody BookingDto bookingItemDto) {
		log.info("Create booking");
//		if(bookingItemDto.getEnd().isBefore(bookingItemDto.getStart()) || bookingItemDto.getEnd().isEqual(bookingItemDto.getStart())) {
//			ResponseEntity.badRequest().build();
//		}
		return bookingClient.createBooking(userId, bookingItemDto);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader(userIDHeader) Long userId, @PathVariable Long bookingId) {
		log.info("Get information by booking for owner item or booker only");
		return bookingClient.getBooking(userId, bookingId);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> checkRequest(@RequestHeader(userIDHeader) Long userId, @PathVariable Long bookingId, @RequestParam String approved) {
		log.info("Check request booking");
		return bookingClient.checkRequest(userId, bookingId, approved);
	}

	@GetMapping
	public ResponseEntity<Object> getBookingsByStatus(@RequestHeader(userIDHeader) Long userId, @RequestParam(defaultValue = "ALL") String state,
											 @RequestParam(defaultValue = "0") @Min(0) Integer from,
											 @RequestParam(defaultValue = "10") @Min(1) Integer size) {

		log.info("Get list of user's bookings");
		return bookingClient.getBookingsByStatus(userId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getUserBookings(@RequestHeader(userIDHeader) Long userId, @RequestParam(defaultValue = "ALL") String state,
										 @RequestParam(defaultValue = "0") @Min(0) Integer from,
										 @RequestParam(defaultValue = "10") @Min(1) Integer size) {

		return bookingClient.getUserBookings(userId, state, from, size);
	}
}
