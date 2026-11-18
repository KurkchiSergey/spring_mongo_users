package ru.netology.mongousers.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.netology.mongousers.model.User;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {

    // Поиск по имени без учёта регистра: "alexey" найдёт и "Alexey"
    List<User> findByNameIgnoreCase(String name);

    // Поиск по точному возрасту
    List<User> findByAge(Integer age);

    // Поиск по диапазону возраста (включительно)
    List<User> findByAgeBetween(Integer minAge, Integer maxAge);

    // Проверки уникальности email
    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, String id);
}
