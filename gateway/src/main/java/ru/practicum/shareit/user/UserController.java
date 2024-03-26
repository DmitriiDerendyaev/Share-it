package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;

    @PostMapping()
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto) {
        log.info("Create new user: {}", userDto);

        return userClient.create(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateById(@RequestBody UserDto userDto, @PathVariable Long userId) {
        log.info("Update user by ID={}", userId);

        return userClient.update(userDto, userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getById(@PathVariable Long userId) {
        log.info("Get user by ID={}", userId);

        return userClient.getById(userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAll() {
        log.info("Get all users");

        return userClient.getAll();
    }

    @DeleteMapping("/{userId}")
    public void deleteById(@PathVariable Long userId) {
        log.info("Delete user by ID={}", userId);

        userClient.deleteById(userId);
    }

}