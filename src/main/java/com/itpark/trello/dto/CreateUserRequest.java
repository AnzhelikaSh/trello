package com.itpark.trello.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {

    @NotBlank(message = "Введите имя пользователя")
    @Size(min = 3, max = 50, message = "Длина имени пользователя должна составлять от 3 до 50 символов")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Имя пользователя может содержать только буквы, цифры, точки, дефисы и нижнее подчеркивание")
    private String username;

    @NotBlank(message = "Введите почту")
    @Email(message = "Неправильный формат почты")
    private String email;

    @NotBlank(message = "Введите пароль")
    @Size(min = 6, message = "Пароль должен содержать не менее 6 символов")
    private String password;

    private String fullName;
}
