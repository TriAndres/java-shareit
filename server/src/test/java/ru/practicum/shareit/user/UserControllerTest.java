package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.user.dto.UpdateUserRqDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserRepository repository;

    private UserService userService;
    private UserController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private UserDto userDto;
    private User user;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(repository);
        controller = new UserController(userService);
        mvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(ErrorHandler.class).build();
        userDto = new UserDto(1L, "John", "john.doe@mail.com");
        user = new User(1L, "John", "john.doe@mail.com");
    }

    @Test
    void addNewUser() throws Exception {
        when(repository.save(any())).thenReturn(user);

        mvc.perform(post("/users").content(mapper.writeValueAsString(userDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    @Test
    void updateUserById() throws Exception {
        UpdateUserRqDto updateUserRqDto = new UpdateUserRqDto("New Name", "newmail@mail.com");
        User updatedUser = new User(1L, updateUserRqDto.getName(), updateUserRqDto.getEmail());

        when(repository.save(any())).thenReturn(updatedUser);
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        mvc.perform(patch("/users/{id}", 1L).content(mapper.writeValueAsString(updateUserRqDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedUser.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updatedUser.getName())))
                .andExpect(jsonPath("$.email", is(updatedUser.getEmail())));
    }

    @Test
    void updateUserByWrongId() throws Exception {
        UpdateUserRqDto updateUserRqDto = new UpdateUserRqDto("New Name", "newmail@mail.com");

        when(repository.findById(-1L)).thenReturn(Optional.empty());

        mvc.perform(patch("/users/{id}", -1L).content(mapper.writeValueAsString(updateUserRqDto))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)).andExpect(status().is4xxClientError());
    }

    @Test
    void findAll() throws Exception {
        when(repository.findAll()).thenReturn(List.of(user));
        mvc.perform(get("/users").characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(user.getName())))
                .andExpect(jsonPath("$[0].email", is(user.getEmail())));
    }

    @Test
    void findById() throws Exception {
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        mvc.perform(get("/users/{id}", 1L).characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    @Test
    void findByWrongId() throws Exception {
        when(repository.findById(-1L)).thenReturn(Optional.empty());

        mvc.perform(get("/users/{id}", -1L).characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)).andExpect(status().is4xxClientError());
    }

    @Test
    void deleteById() throws Exception {
        mvc.perform(delete("/users/{id}", 1L).characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }
}
