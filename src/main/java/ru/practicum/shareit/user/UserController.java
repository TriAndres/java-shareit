package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;


/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping()
    public ResponseEntity<?> create(@Valid @RequestBody UserDto userDto) {
        log.info("поступил запрос на создание пользователя");
        return new ResponseEntity<>(userService.createUser(userDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") @Min(1) Long id, @RequestBody UserDto userDto) {
        log.info("поступил запрос на изменение данных пользователя");
        return new ResponseEntity<>(userService.updateUser(userDto, id), HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<?> findAllUsers() {
        log.info("поступил запрос на получение данных всех пользователей");
        return new ResponseEntity<>(userService.findAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findUserById(@PathVariable("id") @Min(1) Long id) {
        log.info("поступил запрос на получение данных пользователя");
        return new ResponseEntity<>(userService.findUserById(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") @Min(1) Long id) {
        log.info("поступил запрос на получение данных пользователя");
        return new ResponseEntity<>(userService.deleteUser(id), HttpStatus.OK);
    }
}
