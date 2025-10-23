package is.hi.basketmob.controller;

import is.hi.basketmob.dto.UserResponse;
import is.hi.basketmob.dto.UserUpdateRequest;
import is.hi.basketmob.entity.User;
import is.hi.basketmob.mapper.UserMapper;
import is.hi.basketmob.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> putUpdate(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest body
    ) {
        User updated = userService.updateUser(id, body, /*actorUserId*/ id, /*actorIsAdmin*/ false, /*isPut*/ true);
        return ResponseEntity.ok(UserMapper.toResponse(updated));
    }


    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> patchUpdate(
            @PathVariable Long id,
            @RequestBody UserUpdateRequest body
    ) {
        User updated = userService.updateUser(id, body, /*actorUserId*/ id, /*actorIsAdmin*/ false, /*isPut*/ false);
        return ResponseEntity.ok(UserMapper.toResponse(updated));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id, /*actorUserId*/ id, /*actorIsAdmin*/ false);
        return ResponseEntity.noContent().build();
    }
        @GetMapping("/me")
        public ResponseEntity<UserResponse> me() {

            var u = userService.findByEmail("tomas@example.com");
            return ResponseEntity.ok(UserMapper.toResponse(u));
        }

    }

