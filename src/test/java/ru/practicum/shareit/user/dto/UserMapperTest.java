package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.Samples;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    void testMapUserToUserDto() {
        User user = User.builder()
                .name("Name")
                .id(1L)
                .email("email@email.com")
                .build();

        UserDto userDto = UserMapper.mapUserToUserDto(user);

        assertEquals(userDto.getName(), user.getName());
    }

    @Test
    void testMapUserDtoToUser() {
        UserDto userDto = Samples.getUser1();
        User user = UserMapper.mapUserDtoToUser(userDto);
        assertEquals(user.getEmail(), userDto.getEmail());
    }
}