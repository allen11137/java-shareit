package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.constraints.Min;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity<ItemRequestDtoResponse> addNewRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                @Validated @RequestBody ItemRequestDto itemRequestDto) {
        return ResponseEntity.ok(itemRequestService.createItemRequest(userId, itemRequestDto));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDtoResponse>> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemRequestService.findByUserId(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDtoResponse>> findAll(@RequestParam(required = false) @Min(0) Integer from,
                                                                @RequestParam(required = false) @Min(1) Integer size,
                                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        if (from != null && size != null) {
            return ResponseEntity.ok(itemRequestService.findAll(from, size, userId));
        }
        return ResponseEntity.ok(itemRequestService.findAll(0, 0, userId));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDtoResponse> findRequestByID(@PathVariable Long requestId,
                                                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemRequestService.findRequest(requestId, userId));
    }

}
