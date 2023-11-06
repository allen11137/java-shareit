package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ErrorUser;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotValidEmailException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isBlank() || !validate(userDto.getEmail().toUpperCase())) {
            throw new NotValidEmailException("Неверный формат email");
        } else {
            User user = UserMapper.mapUserDtoToUser(userDto);
            if (userDto.getId() != null && userRepository.findById(userDto.getId()).isPresent()) {
                throw new ErrorUser("Пользователь уже существует");
            }
            userRepository.save(user);
            return UserMapper.mapUserToUserDto(user);
        }
    }

    @Override
    @Transactional
    public UserDto updateUser(Long userId, UserDto userDto) {
        User userToUpdate = userRepository.findById(userId).orElse(null);
        if (userToUpdate == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        userToUpdate.setName(userDto.getName() != null ? userDto.getName() : userToUpdate.getName());
        if (userRepository.findAll().stream()
                .filter(u -> u.getEmail().equals(userDto.getEmail()))
                .anyMatch(u -> !u.getId().equals(userId))) {
            throw new ConflictException();
        }
        userToUpdate.setEmail(userDto.getEmail() != null ? userDto.getEmail() : userToUpdate.getEmail());
        userRepository.save(userToUpdate);
        return UserMapper.mapUserToUserDto(userToUpdate);
    }

    @Override
    public User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    @Override
    public List<User> getListOfUser() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.findById(userId).ifPresent(userRepository::delete);
    }


    private static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.matches();
    }
}
