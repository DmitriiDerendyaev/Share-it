package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {

    // create
    // getById
    // getAll
    // update
    // delete

    @PostMapping()
    public UserDto create(@RequestBody UserDto userDto) {
        log.info("Create new user: {}", userDto);

        return null;
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable String userId) {
        log.info("Get user by ID={}", userId);

        return null;
    }

    @GetMapping()
    public List<UserDto> getAll() {
        log.info("Get all users");

        return null;
    }

    @PatchMapping("/{userId}")
    public UserDto updateById(@PathVariable String userId) {
        log.info("Update user by ID={}", userId);

        return null;
    }

    @DeleteMapping("/{userId}")
    public UserDto deleteById(@PathVariable String userId) {
        log.info("Delete user by ID-{}", userId);

        return  null;
    }

}
