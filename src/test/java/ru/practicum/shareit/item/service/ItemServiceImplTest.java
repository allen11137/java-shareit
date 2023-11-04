package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.Samples;
import ru.practicum.shareit.exception.ErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotUserBookerException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ItemServiceImplTest {
    private final ItemService itemService;
    private final UserService userService;
    private final ItemDto itemDto = Samples.getItem1();
    private final ItemDto itemDto2 = Samples.getItem2();
    private final CommentDto commentDto = Samples.getComment_1();
    private UserDto userDto;
    private UserDto userDto2;

    @BeforeEach
    public void beforeEach() {
        userDto = userService.createUser(Samples.getUser1());
        userDto2 = userService.createUser(Samples.getUser2());
    }

    @Test
    public void updateIsUpdated() {
        ItemDto savedItemDto = itemService.createItem(userDto.getId(), itemDto);
        savedItemDto.setName(savedItemDto.getName() + "test");
        savedItemDto.setDescription(savedItemDto.getDescription() + "test");
        ItemDto itemDtoAfterUpdate = itemService.updateItem(savedItemDto.getId(), userDto.getId(), savedItemDto);
        assertThat(itemDtoAfterUpdate).isEqualTo(savedItemDto);
    }

    @Test
    public void findByUserIdCorrectResult() {
        ItemDto savedItemDto1 = itemService.createItem(userDto.getId(), itemDto);
        assertThat(itemService.findItemByUserId(userDto.getId(), 0, Integer.MAX_VALUE))
                .isEqualTo(List.of(Samples.getItemResponse1(savedItemDto1.getId())));
    }

    @Test
    public void searchCorrectResult() {
        ItemDto savedItemDto1 = itemService.createItem(userDto.getId(), itemDto);
        ItemWithBookingDto itemWithBookingDto = ItemMapper.itemWithBookingDto(ItemMapper.mapItemDtoToItem(savedItemDto1, null), null, null, List.of());
        assertThat(itemService.findItemByUserId(userDto.getId(), 0, Integer.MAX_VALUE))
                .isEqualTo(List.of(itemWithBookingDto));
    }

    @Test
    public void addCommentWithNoBookingThrowBadRequest() {
        ItemDto savedItemDto = itemService.createItem(userDto.getId(), itemDto);
        assertThrows(NotUserBookerException.class,
                () -> itemService.addComment(commentDto, savedItemDto.getId(), userDto.getId()));
    }

    @Test
    void createItem() {
        ItemDto savedItemDto = itemService.createItem(userDto.getId(), itemDto);
        assertNotNull(itemService.getItemById(savedItemDto.getId(), userDto.getId()));
    }

    @Test
    void updateItem() {
        ItemDto savedItemDto = itemService.createItem(userDto.getId(), itemDto);
        itemDto.setName("New");
        ItemDto itemDto = itemService.updateItem(savedItemDto.getId(), userDto.getId(), this.itemDto);
        assertNotEquals(savedItemDto.getName(), itemDto.getName());
    }

    @Test
    void itemByText() {
        itemService.createItem(userDto.getId(), itemDto);
        List<Item> itemList = itemService.itemByText(userDto.getId(), itemDto.getName(), 0, 1);
        List<Item> itemList1 = itemService.itemByText(userDto.getId(), "some text", 0, 1);

        assertEquals(itemList.size(), 1);
        assertEquals(itemList1.size(), 0);
    }

    @Test
    void getItemById() {
        itemService.createItem(userDto.getId(), itemDto);
        assertNotNull(itemService.findItemByUserId(userDto.getId(), 0, 1));
    }

    @Test
    void getItem() {
        ItemDto item = itemService.createItem(userDto.getId(), itemDto);
        ItemDto item2 = itemService.createItem(userDto2.getId(), itemDto2);

        assertNotNull(itemService.getItem(item2.getId(), userDto2.getId()));
    }

    @Test
    void findItemByUserId() {
        ItemDto item = itemService.createItem(userDto.getId(), itemDto);
        assertNotNull(itemService.findItemByUserId(userDto.getId(), 0, 1));
    }

    @Test
    void expectedErrorsCreateItem() {
        assertAll(() -> {
            assertThrows(ErrorException.class, () -> {
                itemService.createItem(userDto.getId(), ItemDto.builder()
                        .name("123")
                        .description("123")
                        .build());
            });
            assertThrows(ErrorException.class, () -> {
                itemService.createItem(userDto.getId(), ItemDto.builder()
                        .available(true)
                        .description("123")
                        .build());
            });
            assertThrows(ErrorException.class, () -> {
                itemService.createItem(userDto.getId(), ItemDto.builder()
                        .name("123")
                        .available(true)
                        .build());
            });
        });
    }

    @Test
    void expectedErrorsUpdateItem() {
        assertThrows(NotFoundException.class, () -> {
            itemService.updateItem(22L, userDto.getId(), ItemDto.builder()
                    .name("123")
                    .description("123")
                    .build());
        });
    }

    @Test
    void expectedErrorsByItemByText() {
        assertThat(itemService.itemByText(userDto.getId(), "", 0, 1)).isEqualTo(List.of());
    }

    @Test
    void expectedExceptionWithGetItemById() {
        assertThrows(NotFoundException.class, () -> {
            itemService.getItemById(userDto.getId(), 22L);
        });
    }
}
