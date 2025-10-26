package de.upteams.tasktracker.user.controller.impl;

import de.upteams.tasktracker.user.controller.interfaces.UserApi;
import de.upteams.tasktracker.user.dto.request.UserCreateDto;
import de.upteams.tasktracker.user.dto.response.UserResponseDto;
import de.upteams.tasktracker.user.entity.AppUser;
import de.upteams.tasktracker.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller that receives http-requests for various operations with Employees
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserControllerImpl implements UserApi {

    private final UserService service;

    @Override
    public List<UserResponseDto> getAll() {
        return service.getAll();
    }

    // Новый endpoint для редактирования профиля
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable String id,
                                           @RequestBody UserCreateDto dto,
                                           Authentication authentication) {
        // Получаем email текущего пользователя
        String currentEmail = authentication.getName();
        AppUser currentUser = service.getByEmailOrThrow(currentEmail);

        // Сравниваем UUID
        if (!currentUser.getId().toString().equals(id)) {
            // Попытка редактировать чужой профиль
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You can edit only your own profile.");
        }

        // Реализуем обновление пользователя — например, меняем email и password
        currentUser.setEmail(dto.email());
        currentUser.setPassword(dto.password());
        service.saveOrUpdate(currentUser);

        // Возвращаем UserResponseDto
        UserResponseDto responseDto = new UserResponseDto(
                currentUser.getEmail(),
                currentUser.getRole().name(),
                currentUser.getConfirmationStatus()
        );
        return ResponseEntity.ok(responseDto);
    }
}