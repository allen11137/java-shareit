package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import static org.springframework.http.ResponseEntity.ok;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserClient userService;

    @PostMapping
    public ResponseEntity<Object> addUser(@RequestBody UserDto userDto) {
        return userService.create(userDto);
    }

    @PatchMapping("{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        return userService.update(userDto, userId);
    }

    @GetMapping("{userId}")
    public ResponseEntity<Object> getUser(@PathVariable Long userId) {
        return userService.getById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getListOfUsers() {
        return userService.getAll();
    }

    @DeleteMapping("{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.delete(userId);
    }
}
