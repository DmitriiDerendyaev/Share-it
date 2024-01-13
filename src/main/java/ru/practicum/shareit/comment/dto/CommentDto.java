package ru.practicum.shareit.comment.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {

    private long id;

    @NotBlank
    private String text;

    private long author;

    private String authorName;

    private LocalDateTime created;
}