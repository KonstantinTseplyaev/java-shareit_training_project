/*package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

@Transactional
@TestPropertySource("classpath:application-test.properties")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegrationTest {
    private final EntityManager em;
    private final UserService service;

    @Test
    void createUser() {
        UserDto userDto = UserDto.builder()
                .name("John")
                .email("john@email.com")
                .build();
        service.createUser(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query
                .setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void UpdateUser() {
        UserDto userDto = UserDto.builder()
                .name("John")
                .email("john@email.com")
                .build();

        UserDto user = service.createUser(userDto);

        UserDto update = UserDto.builder()
                .name("Update name")
                .email("update@email.com")
                .build();

        service.updateUser(user.getId(), update);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User updatedUser = query
                .setParameter("id", user.getId())
                .getSingleResult();

        assertThat(updatedUser.getId(), notNullValue());
        assertThat(updatedUser.getName(), equalTo(update.getName()));
        assertThat(updatedUser.getEmail(), equalTo(update.getEmail()));
    }
}*/
