package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    // create
    // getById
    // getAll
    // update
    // delete

    private final UserService userService;

    private final UserMapper userMapper;

    @PostMapping()
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.info("Create new user: {}", userDto);

        return userService.create(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateById(@Valid @RequestBody UserDto userDto, @PathVariable Long userId) {
        log.info("Update user by ID={}", userId);

        return userService.update(userDto, userId);
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable Long userId) {
        log.info("Get user by ID={}", userId);

        return userService.getById(userId);
    }

    @GetMapping()
    public List<UserDto> getAll() {
        log.info("Get all users");

        return userService.getAll();
    }

    @DeleteMapping("/{userId}")
    public User deleteById(@PathVariable Long userId) {
        log.info("Delete user by ID={}", userId);

        return userService.deleteById(userId);
    }

}
