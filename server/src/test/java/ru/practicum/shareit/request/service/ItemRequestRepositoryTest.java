package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.Samples;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class ItemRequestRepositoryTest {
    @Autowired
    private ItemRequestRepository requestRepository;
    @Autowired
    private UserRepository userRepository;
    private User user;
    private User user2;

    @BeforeEach
    void init() {
        user = UserMapper.mapUserDtoToUser(Samples.getUser1());
        userRepository.save(user);
        user2 = UserMapper.mapUserDtoToUser(Samples.getUser2());
        userRepository.save(user2);
    }

    @Test
    void createItemRequest() {
        List<ItemRequest> all = requestRepository.findAll();
        assertThat(all).isEqualTo(List.of());
        ItemRequest itemRequest = ItemRequest.builder()
                .requesterId(user.getId())
                .description("some")
                .created(LocalDateTime.now())
                .build();
        requestRepository.save(itemRequest);
        assertThat(requestRepository.findAll()).isNotNull();
    }

    @Test
    void findAllByRequesterId() {
        ItemRequest itemRequest = ItemRequest.builder()
                .requesterId(user.getId())
                .description("some")
                .created(LocalDateTime.now())
                .build();
        requestRepository.save(itemRequest);
        List<ItemRequest> allByRequesterId = requestRepository.findAllByRequesterId(user.getId());
        assertThat(allByRequesterId.size()).isEqualTo(1);
    }

    @Test
    void findAllByRequesterIdIsNot() {
        ItemRequest itemRequest = ItemRequest.builder()
                .requesterId(user.getId())
                .description("some")
                .created(LocalDateTime.now())
                .build();
        requestRepository.save(itemRequest);
        Pageable pageable = getPageable(0, 10, Sort.unsorted());
        List<ItemRequest> allByRequesterId = requestRepository.findAllByRequesterIdIsNot(user2.getId(), pageable);
        assertThat(allByRequesterId.size()).isEqualTo(1);
    }

    private static Pageable getPageable(int from, int size, Sort sort) {
        return PageRequest.of(from / size, size, sort);
    }
}