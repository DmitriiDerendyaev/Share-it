package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(Long userId);

    List<Booking> findByItemIdIn(List<Long> allItemsByUser);

    List<Booking> findByItemIdAndStatus(Long itemId, BookingStatus status);

    List<Booking> findByBookerIdAndItemId(Long userId, Long itemId);
}