package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Validated
public class ItemController {
    public static final String PAGE_FROM_DEFAULT = "0";
    public static final String PAGE_SIZE_DEFAULT = "10";

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestBody ItemDto itemDto) {
        return ok(itemService.createItem(userId, itemDto));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@PathVariable Long itemId,
                                              @RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestBody ItemDto itemDto) {
        return ok(itemService.updateItem(itemId, userId, itemDto));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemWithBookingDto> getItem(@PathVariable Long itemId,
                                                      @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemService.getItem(itemId, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemWithBookingDto>> getListOfItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                  @RequestParam(defaultValue = PAGE_FROM_DEFAULT) @PositiveOrZero Integer from,
                                                                  @RequestParam(defaultValue = PAGE_SIZE_DEFAULT) @Positive Integer size) {
        return ResponseEntity.ok(itemService.findItemByUserId(userId, from, size));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Item>> getItemByText(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam String text,
                                                    @RequestParam(defaultValue = PAGE_FROM_DEFAULT) @PositiveOrZero Integer from,
                                                    @RequestParam(defaultValue = PAGE_SIZE_DEFAULT) @Positive Integer size) {
        return ResponseEntity.ok(itemService.itemByText(userId, text, from, size));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentResponseDto> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @Valid @RequestBody CommentDto commentDto,
                                                         @PathVariable Long itemId) {
        return ResponseEntity.ok(itemService.addComment(commentDto, itemId, userId));
    }
}
