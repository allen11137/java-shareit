package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> addUser(@RequestBody UserDto userDto) {
        return ok(userService.createUser(userDto));
    }

    @PatchMapping("{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        return ok(userService.updateUser(userId, userDto));
    }

    @GetMapping("{userId}")
    public ResponseEntity<User> getUser(@PathVariable Long userId) {
        return ok(userService.getUser(userId));
    }

    @GetMapping
    public ResponseEntity<List<User>> getListOfUsers() {
        return ok(userService.getListOfUser());
    }

    @DeleteMapping("{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}
