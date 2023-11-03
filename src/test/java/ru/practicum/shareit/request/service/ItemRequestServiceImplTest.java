package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.Samples;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ItemRequestServiceImplTest {
    private final ItemRequestService itemRequestService;
    private final UserService userService;
    private final ItemRequestDto itemRequestDto = Samples.getItemRequestDto();
    private UserDto userDto;
    private UserDto userDto2;

    @BeforeEach
    private void beforeEach() {
        userDto = userService.createUser(Samples.getUser1());
        userDto2 = userService.createUser(Samples.getUser2());
    }

    @Test
    public void create() {
        ItemRequestDtoResponse itemRequestResponseDtoSaved =
                itemRequestService.createItemRequest(userDto.getId(), itemRequestDto);
        assertThat(itemRequestResponseDtoSaved)
                .hasFieldOrPropertyWithValue("description", itemRequestDto.getDescription());
    }

    @Test
    public void findByUserId() {
        ItemRequestDtoResponse itemRequestResponseDtoSaved =
                itemRequestService.createItemRequest(userDto.getId(), itemRequestDto);
        List<ItemRequestDtoResponse> itemRequestResponseDtos =
                itemRequestService.findByUserId(userDto.getId());
        assertThat(itemRequestResponseDtos).isEqualTo(List.of(itemRequestResponseDtoSaved));
    }

    @Test
    public void findAllOk() {
        ItemRequestDtoResponse itemRequestResponseDtoSaved =
                itemRequestService.createItemRequest(userDto.getId(), itemRequestDto);
        List<ItemRequestDtoResponse> itemRequestResponseDtos =
                itemRequestService.findAll(0, Integer.MAX_VALUE, userDto2.getId());
        assertThat(itemRequestResponseDtos).isEqualTo(List.of(itemRequestResponseDtoSaved));
    }

    @Test
    public void findAllEmptyResult() {
        ItemRequestDtoResponse itemRequestResponseDtoSaved =
                itemRequestService.createItemRequest(userDto.getId(), itemRequestDto);
        List<ItemRequestDtoResponse> itemRequestResponseDtos =
                itemRequestService.findAll(0, Integer.MAX_VALUE, userDto.getId());
        assertThat(itemRequestResponseDtos).isEqualTo(Collections.emptyList());
    }


    @Test
    public void findByIdWrongIdThrowsError() {
        assertThrows(NotFoundException.class, () -> itemRequestService.findByUserId(-1L));
    }
}