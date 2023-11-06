package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;

    @Override
    @Transactional
    public ItemRequestDtoResponse createItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        User user = userService.getUser(userId);
        ItemRequest itemRequest = itemRequestRepository.save(ItemRequestMapper.mapToItemRequest(itemRequestDto, user));
        return ItemRequestMapper.mapToItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDtoResponse> findByUserId(Long userId) {
        userService.getUser(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterId(userId);
        return ItemRequestMapper.mapToItemRequestsDto(itemRequests);
    }

    @Override
    public List<ItemRequestDtoResponse> findAll(Integer from, Integer size, Long userId) {
        userService.getUser(userId);
        if (size == 0) {
            return ItemRequestMapper.mapToItemRequestsDto(itemRequestRepository
                    .findAllByRequesterId(userId)
                    .stream().sorted(Comparator.comparing(ItemRequest::getCreated).reversed())
                    .collect(Collectors.toList()));
        }
        Pageable pageRequest = PageRequest.of(from / size, size);
        return ItemRequestMapper.mapToItemRequestsDto(itemRequestRepository
                .findAllByRequesterIdIsNot(userId, pageRequest)
                .stream()
                .sorted(Comparator.comparing(ItemRequest::getCreated).reversed())
                .collect(Collectors.toList()));
    }

    @Override
    public ItemRequestDtoResponse findRequest(Long requestId, Long userId) {
        userService.getUser(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Не найдено", requestId));
        return ItemRequestMapper.mapToItemRequestDto(itemRequest);
    }
}
