package is.hi.basketmob.controller;

import is.hi.basketmob.dto.GameDto;
import is.hi.basketmob.dto.GameUpdateRequest;
import is.hi.basketmob.security.AuthenticatedUser;
import is.hi.basketmob.service.GameCommandService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@Profile("!stub")
@RequestMapping("/api/v1/admin/games")
public class AdminGameController {

    private final GameCommandService commandService;

    public AdminGameController(GameCommandService commandService) {
        this.commandService = commandService;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<GameDto> update(@PathVariable Long id,
                                          @Valid @RequestBody GameUpdateRequest request,
                                          @AuthenticationPrincipal AuthenticatedUser actor) {
        if (!actor.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ADMIN_ONLY");
        }
        return ResponseEntity.ok(commandService.updateGame(id, request));
    }
}
