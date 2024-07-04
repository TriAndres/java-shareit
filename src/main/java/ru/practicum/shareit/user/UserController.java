package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exseption.ConflictException;
import ru.practicum.shareit.user.dto.UserDto;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody UserDto userDto) throws ConflictException {
        return new ResponseEntity<>(userService.create(userDto), HttpStatus.CREATED);
    }

    @PatchMapping(value = "/{userId}")
    public ResponseEntity<?> update(@PathVariable("id") @Min(1) Long id, @RequestBody UserDto userDto) throws ConflictException {
        return new ResponseEntity<>(userService.update(userDto, id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> findAllUsers() {
        return new ResponseEntity<>(userService.findAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findUserById(@PathVariable("id") @Min(1) Long id) {
        return new ResponseEntity<>(userService.findUserById(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        return new ResponseEntity<>(userService.delete(id), HttpStatus.OK);
    }
}