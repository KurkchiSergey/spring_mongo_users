package ru.netology.mongousers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.netology.mongousers.controller.UserController;
import ru.netology.mongousers.model.User;
import ru.netology.mongousers.repository.UserRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тесты контроллера. Репозиторий подменён заглушкой, поэтому MongoDB не нужна.
 */
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private UserRepository repository;

    @Test
    void createValidUserReturns201() throws Exception {
        User saved = new User("Alexey", "alexey@mail.ru", 30);
        saved.setId("abc123");
        when(repository.existsByEmail("alexey@mail.ru")).thenReturn(false);
        when(repository.save(any(User.class))).thenReturn(saved);

        mvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Alexey\",\"email\":\"alexey@mail.ru\",\"age\":30}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("abc123"));
    }

    @Test
    void createInvalidUserReturns400() throws Exception {
        mvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"email\":\"bad\",\"age\":-1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.age").exists());
    }

    @Test
    void getUnknownUserReturns404() throws Exception {
        when(repository.findById("nope")).thenReturn(Optional.empty());
        mvc.perform(get("/api/users/nope"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteExistingUserReturns204() throws Exception {
        when(repository.existsById("abc123")).thenReturn(true);
        mvc.perform(delete("/api/users/abc123"))
                .andExpect(status().isNoContent());
    }
}
