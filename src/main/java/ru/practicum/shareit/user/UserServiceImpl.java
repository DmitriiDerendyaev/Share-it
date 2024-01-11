package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ExistException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public UserDto create(UserDto userDto) {
        User user = userMapper.toUser(userDto);

        if (user.getId() != null) {
            log.warn("id must be null");
            throw new ValidException("id must be null");
        }

        if (user.getEmail() == null) {
            throw new ValidException("Email can't by empty");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            log.warn("User with email is exist");
            throw new ExistException("User with email is exist");
        }

        User newUser = userRepository.save(user);

        return userMapper.toDto(newUser);
    }

    @Override
    public UserDto update(UserDto userDto, Long userId) {
        User user = userMapper.toUser(userDto);
        checkUserExists(userId);

        Optional<User> existingUser = userRepository.findById(userId);
        if (existingUser.isPresent() && !existingUser.get().getEmail().equals(user.getEmail()) &&
                userRepository.existsByEmail(user.getEmail())) {
            log.warn("User with email already exists");
            throw new ExistException("User with email already exists");
        }

        User savedUser = userRepository.save(user);

        return userMapper.toDto(savedUser);
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(Long userId) {
        checkUserExists(userId);
        return userMapper.toDto(userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User not found")));
    }

    @Override
    public User deleteById(Long userId) {
        checkUserExists(userId);

        userRepository.deleteById(userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));
    }

    public void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.warn("This user is not exist");
            throw new ObjectNotFoundException("This user is not exist");
        }
    }
}