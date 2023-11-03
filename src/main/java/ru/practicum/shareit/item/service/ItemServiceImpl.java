package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingItemDtoResponse;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ErrorException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotUserBookerException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserService userService;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        if (itemDto.getAvailable() == null ||
                itemDto.getName() == null || itemDto.getName().isBlank() ||
                itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ErrorException();
        }
        userService.getUser(userId);
        itemDto.setOwner(userId);
        ItemRequest itemRequest = null;
        if (itemDto.getRequestId() != null) {
            itemRequest = itemRequestRepository.findById(itemDto.getRequestId()).orElse(null);
        }
        Item item = ItemMapper.mapItemDtoToItem(itemDto, itemRequest);
        itemRepository.save(item);
        return ItemMapper.mapItemToItemDto(item);
    }

    @Override
    public ItemDto updateItem(Long itemId, Long userId, ItemDto itemDto) {
        userService.getUser(userId);
        Optional<Item> itemForUpdate = itemRepository.findByIdAndOwnerId(itemId, userId);
        if (itemForUpdate.isPresent()) {
            itemForUpdate.get().setAvailable((itemDto.getAvailable() != null ? itemDto.getAvailable() : itemForUpdate.get().getAvailable()));
            itemForUpdate.get().setDescription(itemDto.getDescription() != null ? itemDto.getDescription() : itemForUpdate.get().getDescription());
            itemForUpdate.get().setName(itemDto.getName() != null ? itemDto.getName() : itemForUpdate.get().getName());
            itemRepository.save(itemForUpdate.get());

            ItemRequest itemRequest = null;
            if (itemDto.getRequestId() != null) {
                itemRequest = itemRequestRepository.findById(itemDto.getRequestId()).orElse(null);
            }
            itemForUpdate.get().setItemRequest(itemDto.getRequestId() != null ? itemRequest : itemForUpdate.get().getItemRequest());
            return ItemMapper.mapItemToItemDto(itemForUpdate.get());
        }

        throw new NotFoundException("Вещь не найдена");
    }

    @Override
    public List<Item> itemByText(Long userId, String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.findAll().stream()
                .filter(item -> item.getAvailable() &&
                        (item.getName().toLowerCase().contains(text.toLowerCase(Locale.ROOT))
                                || item.getDescription().toLowerCase().contains(text.toLowerCase())))
                .collect(Collectors.toList());
    }

    @Override
    public Item getItemById(long itemId, long userId) {
        userService.getUser(userId);
        return itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена"));
    }

    @Override
    public CommentResponseDto addComment(CommentDto dto, Long itemId, Long userId) {
        Item item = getItemById(itemId, userId);
        User user = userService.getUser(userId);
        List<Booking> bookingList = bookingRepository.findBookingsByBookerIdAndItemAndEndBeforeAndStatus(userId, item, LocalDateTime.now(),
                Status.APPROVED);
        if (!bookingList.isEmpty()) {
            Comment comment = CommentMapper.toComment(dto, item, user);
            commentRepository.save(comment);
            return CommentMapper.toCommentResponseDto(comment);
        } else {
            throw new NotUserBookerException("Ошибка");
        }
    }

    @Override
    public ItemWithBookingDto getItem(long itemId, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена" + itemId));
        BookingItemDtoResponse lastBooking = null;
        BookingItemDtoResponse nextBooking = null;
        if (item.getOwnerId() == userId) {
            lastBooking = bookingRepository.findLastBooking(itemId)
                    .map(b -> BookingMapper.mapBookingToBookingItemDtoResponse(b, userService.getUser(b.getBookerId())))
                    .orElse(null);
            nextBooking = bookingRepository.findNextBooking(itemId)
                    .map(b -> BookingMapper.mapBookingToBookingItemDtoResponse(b, userService.getUser(b.getBookerId())))
                    .orElse(null);
        }
        List<CommentResponseDto> commentResponseDto = CommentMapper
                .toListCommentResponseDto(commentRepository.findCommentsByItem(item));
        return ItemMapper.itemWithBookingDto(item, lastBooking, nextBooking, commentResponseDto);
    }

    @Override
    public List<ItemWithBookingDto> findItemByUserId(long userId, int from, int size) {
        List<ItemWithBookingDto> itemWithBookingDto = new ArrayList<>();
        Pageable pageable = getPageable(from, size, Sort.unsorted());
        for (Item item : itemRepository.findAllByOwnerId(userId, pageable)) {
            BookingItemDtoResponse lastBooking = bookingRepository.findLastBooking(item.getId())
                    .map(b -> BookingMapper.mapBookingToBookingItemDtoResponse(b, userService.getUser(b.getBookerId())))
                    .orElse(null);
            BookingItemDtoResponse nextBooking = bookingRepository.findNextBooking(item.getId())
                    .map(b -> BookingMapper.mapBookingToBookingItemDtoResponse(b, userService.getUser(b.getBookerId())))
                    .orElse(null);
            List<CommentResponseDto> commentsResponseDto = CommentMapper
                    .toListCommentResponseDto(commentRepository.findCommentsByItem(item));
            itemWithBookingDto.add(ItemMapper.itemWithBookingDto(item, lastBooking, nextBooking, commentsResponseDto));
        }
        return itemWithBookingDto;
    }

    private static Pageable getPageable(int from, int size, Sort sort) {
        return PageRequest.of(from / size, size, sort);
    }
}
