package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

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

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestBody ItemDto itemDto) {
        return itemClient.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable Long itemId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody ItemDto itemDto) {
        return itemClient.update(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable Long itemId,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.getById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getListOfItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(defaultValue = PAGE_FROM_DEFAULT) @PositiveOrZero Integer from,
                                                @RequestParam(defaultValue = PAGE_SIZE_DEFAULT) @Positive Integer size) {
        return itemClient.findByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemByText(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam String text,
                                                @RequestParam(defaultValue = PAGE_FROM_DEFAULT) @PositiveOrZero Integer from,
                                                @RequestParam(defaultValue = PAGE_SIZE_DEFAULT) @Positive Integer size) {
        return itemClient.search(text, userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody CommentDto commentDto,
                                             @PathVariable Long itemId) {
        return itemClient.addComment(commentDto, itemId, userId);
    }
}
