package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        User user = userMapper.toUser(userDto);

        if (user.getId() != null) {
            log.warn("id must be null");
            throw new ValidException("id must be null");
        }

        try {
            return userMapper.toDto(userRepository.save(user));
        } catch (Exception e) {
            throw new ExistException("Email can't be the same");
        }
    }

    @Override
    @Transactional
    public UserDto update(UserDto userDto, Long userId) {
        checkUserExists(userId);

        User saveUser = userRepository.findById(userId).orElseThrow();
        if (userDto.getEmail() != null) {
            saveUser.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) {
            saveUser.setName(userDto.getName());
        }

        return userMapper.toDto(userRepository.save(saveUser));
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
    @Transactional
    public void deleteById(Long userId) {
        checkUserExists(userId);

        Optional<User> user = userRepository.findById(userId);
        log.info("Перед удалением пользователя с ID={} найден пользователь: {}", userId, user);
        userRepository.deleteById(userId);
    }

    public void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.warn("This user is not exist");
            throw new ObjectNotFoundException("This user is not exist");
        }
    }
}