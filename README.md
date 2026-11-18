
## Запуск

1. Запустите MongoDB в Docker:

   ```bash
   docker run -d --name my-mongo -p 27017:27017 -v mongo_data:/data/db mongo:7
   ```

2. Запустите приложение из IDE (класс `MongoUsersApplication`) или командой:

   ```bash
   mvn spring-boot:run
   ```

Строка подключения задана в `src/main/resources/application.properties`:

```
spring.data.mongodb.uri=mongodb://localhost:27017/users_db
```

## API

| Метод | URL | Описание |
|---|---|---|
| GET | `/api/users` | все пользователи |
| GET | `/api/users?name=alexey` | поиск по имени (без учёта регистра) |
| GET | `/api/users?age=30` | поиск по возрасту |
| GET | `/api/users?minAge=18&maxAge=30` | поиск по диапазону возраста |
| GET | `/api/users/{id}` | пользователь по id |
| POST | `/api/users` | создать пользователя |
| PUT | `/api/users/{id}` | обновить пользователя |
| DELETE | `/api/users/{id}` | удалить пользователя |

Пример тела запроса для POST и PUT:

```json
{
  "name": "Alexey",
  "email": "alexey@mail.ru",
  "age": 30
}
```

### Валидация

| Поле | Правило |
|---|---|
| `name` | не пустое, от 2 до 50 символов |
| `email` | не пустой, корректный формат, уникальный |
| `age` | обязателен, от 0 до 150 |

### Коды ответов

| Код | Когда |
|---|---|
| 200 OK | успешный GET или PUT |
| 201 Created | пользователь создан |
| 204 No Content | пользователь удалён |
| 400 Bad Request | данные не прошли валидацию или JSON некорректен |
| 404 Not Found | пользователя с таким id нет |
| 409 Conflict | email уже занят |

Пример ответа с ошибкой валидации:

```json
{
  "timestamp": "2026-11-18T21:30:00",
  "status": 400,
  "message": "Ошибка валидации",
  "errors": {
    "name": "Имя не может быть пустым",
    "email": "Некорректный email",
    "age": "Возраст не может быть отрицательным"
  }
}
```

## Тестирование

- Автотесты (MongoDB не нужна): `mvn test`
- Ручная проверка: импортируйте в Postman файл `postman_collection.json`
