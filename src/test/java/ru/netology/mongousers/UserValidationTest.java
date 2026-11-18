package ru.netology.mongousers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import ru.netology.mongousers.model.User;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Проверяет правила валидации модели User. MongoDB для этого теста не нужна.
 */
class UserValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validUserHasNoErrors() {
        User user = new User("Alexey", "alexey@mail.ru", 30);
        assertThat(validator.validate(user)).isEmpty();
    }

    @Test
    void invalidUserHasErrorsOnAllFields() {
        User user = new User("", "not-an-email", -5);
        Set<String> fields = validator.validate(user).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .collect(Collectors.toSet());
        assertThat(fields).containsExactlyInAnyOrder("name", "email", "age");
    }

    @Test
    void missingAgeIsError() {
        User user = new User("Anna", "anna@mail.ru", null);
        assertThat(validator.validate(user)).hasSize(1);
    }
}
