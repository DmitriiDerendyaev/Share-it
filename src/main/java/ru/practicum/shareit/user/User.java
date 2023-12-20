package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */

@Data
@Builder
public class User {

    @NotBlank
    private Long id;

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;
}
