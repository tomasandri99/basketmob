package is.hi.basketmob.service;

import is.hi.basketmob.dto.UserUpdateRequest;
import is.hi.basketmob.entities.User;
import is.hi.basketmob.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Update a user (owner or admin).
     *
     * @param id            target user id (path variable)
     * @param req           request body
     * @param actorUserId   authenticated user's id
     * @param actorIsAdmin  whether the actor has admin privileges
     * @param isPut         true for PUT (replace semantics), false for PATCH (partial)
     * @return updated User entity
     */
    @Transactional
    public User updateUser(Long id,
                           UserUpdateRequest req,
                           Long actorUserId,
                           boolean actorIsAdmin,
                           boolean isPut) {


        if (!(actorIsAdmin || Objects.equals(actorUserId, id))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot modify this account");
        }


        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));


        validateRequest(req);


        if (isPut) {

            user.setDisplayName(req.getDisplayName());
            user.setAvatarUrl(req.getAvatarUrl());
            user.setGender(req.getGender());
        } else {

            if (req.getDisplayName() != null) user.setDisplayName(req.getDisplayName());
            if (req.getAvatarUrl() != null)   user.setAvatarUrl(req.getAvatarUrl());
            if (req.getGender() != null)      user.setGender(req.getGender());
        }


        if (req.getNewPassword() != null) {
            if (req.getCurrentPassword() == null ||
                    !passwordEncoder.matches(req.getCurrentPassword(), user.getPassword())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
            }
            user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        }


        user.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));


        return userRepository.save(user);
    }
    @Transactional
    public void deleteUser(Long id, Long actorUserId, boolean actorIsAdmin) {
        if (!(actorIsAdmin || Objects.equals(actorUserId, id))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot delete this account");
        }
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        userRepository.delete(user);
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND"));
    }


    private void validateRequest(UserUpdateRequest req) {

        Optional.ofNullable(req.getDisplayName()).ifPresent(name -> {
            int len = name.trim().length();
            if (len < 2 || len > 50) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "displayName must be 2–50 characters");
            }
        });


        Optional.ofNullable(req.getAvatarUrl()).ifPresent(url -> {
            if (!url.matches("^(https?://).+")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "avatarUrl must start with http:// or https://");
            }
        });


        Optional.ofNullable(req.getGender()).ifPresent(g -> {
            if (!g.matches("^(male|female|other)$")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "gender must be one of: male, female, other");
            }
        });


        Optional.ofNullable(req.getNewPassword()).ifPresent(pw -> {
            int len = pw.length();
            if (len < 8 || len > 200) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "newPassword must be 8–200 characters");
            }

            if (req.getCurrentPassword() == null || req.getCurrentPassword().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "currentPassword is required when changing password");
            }

        });
    }
}

