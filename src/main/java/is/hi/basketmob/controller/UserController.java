package is.hi.basketmob.controller;

import is.hi.basketmob.dto.UserResponse;
import is.hi.basketmob.dto.UserUpdateRequest;
import is.hi.basketmob.mapper.UserMapper;
import is.hi.basketmob.security.AuthenticatedUser;
import is.hi.basketmob.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            @Valid @RequestBody UserUpdateRequest body,
            @AuthenticationPrincipal AuthenticatedUser actor
    ) {
        return ResponseEntity.ok(
                UserMapper.toResponse(userService.updateUser(id, body, actor.getId(), actor.isAdmin(), true))
        );
    }


    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> patchUpdate(
            @PathVariable Long id,
            @RequestBody UserUpdateRequest body,
            @AuthenticationPrincipal AuthenticatedUser actor
    ) {
        return ResponseEntity.ok(
                UserMapper.toResponse(userService.updateUser(id, body, actor.getId(), actor.isAdmin(), false))
        );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal AuthenticatedUser actor) {
        userService.deleteUser(id, actor.getId(), actor.isAdmin());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal AuthenticatedUser actor) {
        return ResponseEntity.ok(UserMapper.toResponse(userService.findById(actor.getId())));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> putMe(@Valid @RequestBody UserUpdateRequest body,
                                              @AuthenticationPrincipal AuthenticatedUser actor) {
        return ResponseEntity.ok(
                UserMapper.toResponse(userService.updateUser(actor.getId(), body, actor.getId(), actor.isAdmin(), true))
        );
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> patchMe(@RequestBody UserUpdateRequest body,
                                                @AuthenticationPrincipal AuthenticatedUser actor) {
        return ResponseEntity.ok(
                UserMapper.toResponse(userService.updateUser(actor.getId(), body, actor.getId(), actor.isAdmin(), false))
        );
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMe(@AuthenticationPrincipal AuthenticatedUser actor) {
        userService.deleteUser(actor.getId(), actor.getId(), actor.isAdmin());
        return ResponseEntity.noContent().build();
    }
}
