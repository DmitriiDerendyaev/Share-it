package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ExistException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public UserDto create(User user) {
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

        User newUser = userRepository.create(user);

        return userMapper.toDto(newUser);
    }

    @Override
    public UserDto update(User user, Long userId) {
        checkUserExists(userId);
        if (userRepository.existByEmailAndId(user, userId)) {
            log.warn("User with email is exist");
            throw new ExistException("User with email is exist");
        }

        User saveUser = userRepository.getById(userId);
        if (user.getEmail() != null) {
            saveUser.setEmail(user.getEmail());
        }

        if (user.getName() != null) {
            saveUser.setName(user.getName());
        }

        return userMapper.toDto(userRepository.update(saveUser, userId));
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.getAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public User getById(Long userId) {
        checkUserExists(userId);
        return userRepository.getById(userId);
    }

    @Override
    public User deleteById(Long userId) {
        checkUserExists(userId);

        return userRepository.deleteById(userId);
    }

    public void checkUserExists(Long userId) {
        if (!userRepository.containsUser(userId)) {
            log.warn("This user is not exist");
            throw new ObjectNotFoundException("This user is not exist");
        }
    }
}