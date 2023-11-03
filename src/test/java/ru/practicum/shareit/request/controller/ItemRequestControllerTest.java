package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.Samples;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


    @WebMvcTest(controllers = ItemRequestController.class)
    @RequiredArgsConstructor(onConstructor_ = @Autowired)
    public class ItemRequestControllerTest {
        public static final String HEADER_USER_ID = "X-Sharer-User-Id";
        private final MockMvc mvc;
        private final ObjectMapper mapper;
        @MockBean
        private final ItemRequestService itemRequestService;
        private final ItemDto itemDto = Samples.getItem1();
        private final ItemRequestDtoResponse itemRequestResponseDto =
                Samples.getItemRequestResponseDto(1L, List.of(itemDto), LocalDateTime.now());

        private final ItemRequestDto itemRequestDto = Samples.getItemRequestDto();

        @Test
        public void addNewRequest() throws Exception {
            when(itemRequestService.createItemRequest(anyLong(), any()))
                    .thenReturn(itemRequestResponseDto);
            mvc.perform(post("/requests")
                            .content(mapper.writeValueAsString(itemRequestDto))
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(HEADER_USER_ID, 1))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(itemRequestResponseDto.getId()), Long.class))
                    .andExpect(jsonPath("$.description", is(itemRequestResponseDto.getDescription())));
        }

        @Test
        public void findAllByUserId() throws Exception {
            when(itemRequestService.findByUserId(anyLong()))
                    .thenReturn(List.of(itemRequestResponseDto, itemRequestResponseDto));
            mvc.perform(get("/requests?from=0&size=2")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(HEADER_USER_ID, 1))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()", is(2)));
        }

        @Test
        public void findAll() throws Exception {
            when(itemRequestService.findAll(anyInt(), anyInt(), anyLong()))
                    .thenReturn(List.of(itemRequestResponseDto, itemRequestResponseDto));
            mvc.perform(get("/requests/all?from=0&size=2")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(HEADER_USER_ID, 1))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()", is(2)));
        }

        @Test
        public void findRequestByID() throws Exception {
            when(itemRequestService.findRequest(anyLong(), anyLong()))
                    .thenReturn(itemRequestResponseDto);
            mvc.perform(get("/requests/{requestId}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(HEADER_USER_ID, 1))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(itemRequestResponseDto.getId()), Long.class))
                    .andExpect(jsonPath("$.description", is(itemRequestResponseDto.getDescription())));

    }
}