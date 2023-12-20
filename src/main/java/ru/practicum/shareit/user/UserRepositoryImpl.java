package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final HashMap<Long, User> users = new HashMap<>();
    private Long id = 0L;

    @Override
    public User create(User user) {
        user.setId(incrementId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user, Long userId) {
        return users.put(userId, user);
    }

    @Override
    public User getById(Long userId) {
        return users.get(userId);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User deleteById(Long userId) {
        return users.remove(userId);
    }

    @Override
    public boolean containsUser(Long userId) {
        return users.containsKey(userId);
    }

    @Override
    public boolean existsByEmail(String email) {
        return users.values().stream()
                .anyMatch(x -> x.getEmail().equals(email));
    }

    @Override
    public boolean existByEmailAndId(User user, Long id) {
        return users.values().stream()
                .anyMatch(x -> x.getEmail().equals(user.getEmail()) && Objects.equals(x.getId(), id));
    }

    private Long incrementId() {
        return ++id;
    }
}
