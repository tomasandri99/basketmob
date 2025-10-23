package is.hi.basketmob.dto;

import javax.validation.constraints.NotNull;

/** Request body for following a team (provides the team to follow). */
public class FavoriteRequest {
    @NotNull(message = "teamId is required")
    public Long teamId;

    public FavoriteRequest() {}
    public FavoriteRequest(Long teamId) {
        this.teamId = teamId;
    }
}
