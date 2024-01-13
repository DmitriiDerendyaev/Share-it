package ru.practicum.shareit.booking;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@EqualsAndHashCode
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional(rollbackFor = Exception.class)
    public Booking createBooking(Long userId, BookingDto bookingDto) {

        LocalDateTime saveStartTime = bookingDto.getStart();

        if (saveStartTime.isAfter(bookingDto.getEnd())) {
            throw new ValidException("Data start can't be later then end");
        }

        if (saveStartTime.isEqual(bookingDto.getEnd())) {
            throw new ValidException("Dates start and end can be different");
        }

        if (!userRepository.existsById(userId)) {
            log.warn("This user is not exist");
            throw new ObjectNotFoundException("This user is not exist");
        }

        long saveItemId = bookingDto.getItemId();

        if (itemRepository.findById(saveItemId).isEmpty()) {
            throw new ObjectNotFoundException("Item not found");
        }

        Item itemSave = itemRepository.getReferenceById(saveItemId);

        if (!itemSave.getAvailable()) {
            throw new ValidException("Item is not available");
        }

        if (itemSave.getOwner().getId() == userId) {

            throw new ObjectNotFoundException("You can't send request for your item");
        }

        Booking saveBooking = new Booking();
        saveBooking.setId(bookingDto.getId());
        saveBooking.setStart(bookingDto.getStart());
        saveBooking.setEnd(bookingDto.getEnd());
        saveBooking.setItem(itemRepository.findById(bookingDto.getItemId()).orElseThrow());
        saveBooking.setBooker(userRepository.findById(userId).orElseThrow());
        saveBooking.setStatus(BookingStatus.WAITING);

        return bookingRepository.save(saveBooking);
    }

    @Transactional
    public Booking checkRequest(Long userId, Long bookingId, String approved) {

        Booking saveBooking = bookingRepository.findById(bookingId).orElseThrow();
        saveBooking.setBooker(userRepository.getReferenceById(saveBooking.getBooker().getId()));
        saveBooking.setItem(itemRepository.getReferenceById(saveBooking.getItem().getId()));

        if (approved.isBlank()) {
            throw new ValidException("approved must be true/false");
        }

        if (bookingId == 0) {
            throw new ValidException("bookingId can't be null");
        }

        if (!bookingRepository.existsById(bookingId)) {
            throw new ObjectNotFoundException("This booking not found");
        }

        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("User not found");
        }

        if (saveBooking.getItem().getOwner().getId() != userId) {
            log.warn("Change status can only owner");
            throw new ObjectNotFoundException("Change status can only owner");
        }

        if (saveBooking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidException("This booking cheked");
        }

        switch (approved) {
            case "true":
                if (!saveBooking.getItem().getAvailable()) {
                    saveBooking.setStatus(BookingStatus.WAITING);
                }
                saveBooking.setStatus(BookingStatus.APPROVED);

                return saveBooking;

            case "false":
                saveBooking.setStatus(BookingStatus.REJECTED);
                return saveBooking;

            default:
                throw new ValidException("Approved must be true or false");
        }
    }

    public Booking getBooking(Long userId, Long bookingId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("User not found");
        }
        if (!bookingRepository.existsById(bookingId)) {
            throw new ObjectNotFoundException("This booking not found");
        }

        Booking saveBooking = bookingRepository.findById(bookingId).orElseThrow();

        if (saveBooking.getBooker().getId() != userId
                && (saveBooking.getItem().getOwner().getId() != userId)) {
            throw new ObjectNotFoundException("Get information about booking can owner item or booker only");
        }

        return saveBooking;
    }

    public List<Booking> getBookingsByStatus(Long userId, String state) {

        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("This user not exist");
        }

        List<Booking> bookingsByUserId = bookingRepository.findByBookerId(userId);
        return checkState(bookingsByUserId, state);
    }

    public List<Booking> getUserBookings(Long ownerId, String state) {

        List<Item> itemByOwnerId = itemRepository.findByOwnerId(ownerId);

        if (itemByOwnerId.isEmpty()) {
            throw new ObjectNotFoundException("This owner haven't any item");
        }

        List<Long> allItemsByUser = itemByOwnerId.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<Booking> saveBooking = bookingRepository.findByItemIdIn(allItemsByUser);

        return checkState(saveBooking, state);

    }

    public List<Booking> checkState(List<Booking> saveBooking, String state) {

        switch (state) {
            case "ALL":
                log.info("Get list by status ALL");
                return saveBooking.stream()
                        .sorted((Comparator.comparing(Booking::getStart)).reversed())
                        .collect(Collectors.toList());

            case "CURRENT":
                log.info("Get list by status CURRENT");
                return saveBooking.stream()
                        .filter(x -> x.getEnd().isAfter(LocalDateTime.now()) && x.getStart().isBefore(LocalDateTime.now()))
                        .sorted((Comparator.comparing(Booking::getStart)).reversed())
                        .collect(Collectors.toList());

            case "PAST":
                log.info("Get list by status PAST");
                return saveBooking.stream()
                        .filter(x -> x.getEnd().isBefore(LocalDateTime.now()))
                        .sorted((Comparator.comparing(Booking::getStart)).reversed())
                        .collect(Collectors.toList());

            case "FUTURE":
                log.info("Get list by status FUTURE");
                return saveBooking.stream()
                        .filter(x -> x.getStart().isAfter(LocalDateTime.now()))
                        .sorted((Comparator.comparing(Booking::getStart)).reversed())
                        .collect(Collectors.toList());

            case "WAITING":
                log.info("Get list by status WAITING");
                return saveBooking.stream()
                        .filter(x -> x.getStatus().equals(BookingStatus.WAITING))
                        .sorted((Comparator.comparing(Booking::getStart)).reversed())
                        .collect(Collectors.toList());

            case "REJECTED":
                log.info("Get list by status REJECTED");
                return saveBooking.stream()
                        .filter(x -> x.getStatus().equals(BookingStatus.REJECTED))
                        .sorted((Comparator.comparing(Booking::getStart)).reversed())
                        .collect(Collectors.toList());

            default:
                throw new ValidException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}