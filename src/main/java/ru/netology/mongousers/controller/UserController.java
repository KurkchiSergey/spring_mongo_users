package ru.netology.mongousers.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.mongousers.exception.EmailAlreadyExistsException;
import ru.netology.mongousers.exception.UserNotFoundException;
import ru.netology.mongousers.model.User;
import ru.netology.mongousers.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }


    @GetMapping
    public List<User> getUsers(@RequestParam(required = false) String name,
                               @RequestParam(required = false) Integer age,
                               @RequestParam(required = false) Integer minAge,
                               @RequestParam(required = false) Integer maxAge) {
        if (name != null) {
            return repository.findByNameIgnoreCase(name);
        }
        if (age != null) {
            return repository.findByAge(age);
        }
        if (minAge != null || maxAge != null) {
            int from = minAge != null ? minAge : 0;
            int to = maxAge != null ? maxAge : Integer.MAX_VALUE;
            return repository.findByAgeBetween(from, to);
        }
        return repository.findAll();
    }

    // GET /api/users/{id} — один пользователь
    @GetMapping("/{id}")
    public User getUser(@PathVariable String id) {
        return repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    // POST /api/users — создать пользователя
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        if (repository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException(user.getEmail());
        }
        user.setId(null); // id всегда генерирует MongoDB
        User saved = repository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/users/{id} — обновить пользователя целиком
    @PutMapping("/{id}")
    public User updateUser(@PathVariable String id, @Valid @RequestBody User user) {
        if (!repository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        if (repository.existsByEmailAndIdNot(user.getEmail(), id)) {
            throw new EmailAlreadyExistsException(user.getEmail());
        }
        user.setId(id);
        return repository.save(user);
    }

    // DELETE /api/users/{id} — удалить пользователя
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        if (!repository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
