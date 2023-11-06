package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity<ItemRequestDtoResponse> addNewRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                @RequestBody ItemRequestDto itemRequestDto) {
        return ResponseEntity.ok(itemRequestService.createItemRequest(userId, itemRequestDto));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDtoResponse>> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemRequestService.findByUserId(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDtoResponse>> findAll(@RequestParam(required = false, defaultValue = "0") Integer from,
                                                                @RequestParam(required = false, defaultValue = "1") Integer size,
                                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemRequestService.findAll(from, size, userId));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDtoResponse> findRequestByID(@PathVariable Long requestId,
                                                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemRequestService.findRequest(requestId, userId));
    }

}
