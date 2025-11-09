package is.hi.basketmob.dto;

import java.time.OffsetDateTime;

public record NotificationDto(
        Long id,
        Long gameId,
        String message,
        boolean read,
        OffsetDateTime createdAt
) {}
