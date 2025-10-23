package is.hi.basketmob.mapper;

import is.hi.basketmob.dto.UserResponse;
import is.hi.basketmob.entity.User;

public final class UserMapper {
    private UserMapper() {}
    public static UserResponse toResponse(User u) {
        return new UserResponse(u.getId(), u.getEmail(), u.getDisplayName(), u.getAvatarUrl(), u.getGender());
    }
}

