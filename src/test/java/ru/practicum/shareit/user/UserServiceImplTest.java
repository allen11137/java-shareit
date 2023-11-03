package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.Samples;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class UserServiceImplTest {
    private final UserService userService;

    @Test
    public void saveResponseIsValid() {
        UserDto userDto = Samples.getUser1();
        UserDto savedUserDto = userService.createUser(userDto);
        userDto.setId(savedUserDto.getId());
        assertThat(userDto).isEqualTo(savedUserDto);
    }

    @Test
    public void saveAndGetByIdAreSame() {
        UserDto userDto = Samples.getUser1();
        UserDto savedUserDto = userService.createUser(userDto);
        User getByIdUserDto = userService.getUser(savedUserDto.getId());
        assertThat(savedUserDto.getEmail()).isEqualTo(getByIdUserDto.getEmail());
    }

    @Test
    public void updateIsUpdated() {
        UserDto userDtoBeforeUpdate = Samples.getUser1();
        UserDto savedUserDtoBeforeUpdate = userService.createUser(userDtoBeforeUpdate);
        UserDto updatedUserDto = Samples.getUser2();
        updatedUserDto.setId(savedUserDtoBeforeUpdate.getId());
        UserDto savedUserDtoAfterUpdate = userService.updateUser(savedUserDtoBeforeUpdate.getId(), updatedUserDto);
        User getByIdUserDto = userService.getUser(savedUserDtoAfterUpdate.getId());
        assertAll(
                () -> assertThat(getByIdUserDto).isNotEqualTo(savedUserDtoBeforeUpdate),
                () -> assertThat(getByIdUserDto.getName()).isEqualTo(savedUserDtoAfterUpdate.getName())
        );
    }

    @Test
    public void deleteGetByIdRaiseError() {
        UserDto userDto = Samples.getUser1();
        UserDto savedUserDto = userService.createUser(userDto);
        userService.deleteUser(savedUserDto.getId());
        assertThrows(NotFoundException.class, () -> userService.getUser(savedUserDto.getId()));
    }

    @Test
    public void getAllTwoUsersReturned() {
        UserDto savedUserDto1 = userService.createUser(Samples.getUser1());
        UserDto savedUserDto2 = userService.createUser(Samples.getUser2());
        assertThat(userService.getListOfUser().size()).isEqualTo(List.of(savedUserDto1, savedUserDto2).size());
    }
}
