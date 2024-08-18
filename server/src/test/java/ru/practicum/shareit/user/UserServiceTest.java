package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UpdateUserRqDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("test")
@Transactional
public class UserServiceTest {

    private final UserService service;
    private final EntityManager em;

    @Test
    void addNewUser() {
        // given
        UserDto userDto = new UserDto(null, "Пётр Иванов", "some@email.com");

        // when
        UserDto savedUserDto = service.addNewUser(userDto);
        assertThat(userDto.getName(), equalTo(savedUserDto.getName()));
        assertThat(userDto.getEmail(), equalTo(savedUserDto.getEmail()));

        // then
        User user = queryUserFromDb(savedUserDto.getId());

        assertThat(user.getId(), equalTo(savedUserDto.getId()));
        assertThat(user.getName(), equalTo(savedUserDto.getName()));
        assertThat(user.getEmail(), equalTo(savedUserDto.getEmail()));
    }

    @Test
    void updateUser() {
        // given
        UserDto userDto = new UserDto(null, "Пётр Иванов", "some@email.com");

        User entity = UserMapper.mapToUser(userDto);
        em.persist(entity);
        em.flush();

        // when
        UpdateUserRqDto updateUserRqDto = new UpdateUserRqDto("Иван Петров", "petrov@mail.com");
        service.updateUserById(entity.getId(), updateUserRqDto);

        // then
        User updatedUser = queryUserFromDb(entity.getId());

        assertThat(updatedUser.getName(), equalTo(updateUserRqDto.getName()));
        assertThat(updatedUser.getEmail(), equalTo(updateUserRqDto.getEmail()));
    }

    @Test
    void findById() {
        // given
        UserDto userDto = new UserDto(null, "Пётр Иванов", "some@email.com");

        User entity = UserMapper.mapToUser(userDto);
        em.persist(entity);
        em.flush();

        // when
        UserDto foundDto = service.findById(entity.getId());

        // then
        assertThat(foundDto.getName(), equalTo(userDto.getName()));
        assertThat(foundDto.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void deleteById() {
        // given
        UserDto userDto = new UserDto(null, "Пётр Иванов", "some@email.com");

        User entity = UserMapper.mapToUser(userDto);
        em.persist(entity);
        em.flush();

        // when
        service.deleteById(entity.getId());

        // then
        assertThatThrownBy(() -> queryUserFromDb(entity.getId())).isInstanceOf(NoResultException.class);
    }

    @Test
    void findAll() {
        // given
        List<UserDto> userDtos = List.of(new UserDto(null, "User1", "user1@mail.com"),
                                         new UserDto(null, "User2", "user2@mail.com"),
                                         new UserDto(null, "User3", "user3@mail.com"),
                                         new UserDto(null, "User4", "user4@mail.com"));
        for (UserDto dto : userDtos) {
            User entity = UserMapper.mapToUser(dto);
            em.persist(entity);
        }
        em.flush();

        // when
        List<UserDto> foundDtos = service.findAll();

        // then
        assertThat(foundDtos, hasSize(userDtos.size()));
        for (UserDto sourceUser : userDtos) {
            assertThat(foundDtos,
                       hasItem(allOf(hasProperty("id", notNullValue()),
                                     hasProperty("name", equalTo(sourceUser.getName())),
                                     hasProperty("email", equalTo(sourceUser.getEmail())))));
        }

    }

    private User queryUserFromDb(Long id) {
        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        return query.setParameter("id", id).getSingleResult();
    }
}
