package is.hi.basketmob.controller;

import is.hi.basketmob.dto.GameListItemDto;
import is.hi.basketmob.dto.NotificationDto;
import is.hi.basketmob.dto.StandingDto;
import is.hi.basketmob.dto.TeamDto;
import is.hi.basketmob.dto.UserUpdateRequest;
import is.hi.basketmob.entity.League;
import is.hi.basketmob.entity.Team;
import is.hi.basketmob.repository.LeagueRepository;
import is.hi.basketmob.repository.TeamRepository;
import is.hi.basketmob.security.AuthenticatedUser;
import is.hi.basketmob.service.FavoriteService;
import is.hi.basketmob.service.GameService;
import is.hi.basketmob.service.NotificationService;
import is.hi.basketmob.service.StandingsProvider;
import is.hi.basketmob.service.UserService;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class ViewController {

    private final GameService gameService;
    private final StandingsProvider standingsProvider;
    private final LeagueRepository leagues;
    private final FavoriteService favoriteService;
    private final NotificationService notificationService;
    private final UserService userService;
    private final TeamRepository teamRepository;

    public ViewController(GameService gameService,
                          StandingsProvider standingsProvider,
                          LeagueRepository leagues,
                          FavoriteService favoriteService,
                          NotificationService notificationService,
                          UserService userService,
                          TeamRepository teamRepository) {
        this.gameService = gameService;
        this.standingsProvider = standingsProvider;
        this.leagues = leagues;
        this.favoriteService = favoriteService;
        this.notificationService = notificationService;
        this.userService = userService;
        this.teamRepository = teamRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        LocalDate today = LocalDate.now();
        List<League> allLeagues = leagues.findAll();
        League featured = selectLeague(null, allLeagues);
        List<GameListItemDto> gamesToday = gameService.listByDate(today, 0, 5, "tipoff,asc").getContent();
        model.addAttribute("today", today);
        model.addAttribute("games", gamesToday);
        model.addAttribute("leagues", allLeagues);
        model.addAttribute("featuredLeague", featured);
        model.addAttribute("featuredStandings", featured == null
                ? Collections.emptyList()
                : standingsProvider.getStandings(featured.getId(), featured.getSeason()));
        return "index";
    }

    @GetMapping("/games")
    public String games(@RequestParam(required = false)
                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                        LocalDate date,
                        Model model) {
        LocalDate target = date != null ? date : LocalDate.now();
        model.addAttribute("selectedDate", target);
        model.addAttribute("games",
                gameService.listByDate(target, 0, 50, "tipoff,asc").getContent());
        return "games";
    }

    @GetMapping("/standings")
    public String standings(@RequestParam(required = false) Long leagueId,
                            Model model) {
        List<League> allLeagues = leagues.findAll();
        model.addAttribute("leagues", allLeagues);

        League selected = selectLeague(leagueId, allLeagues);
        List<StandingDto> rows = selected == null
                ? Collections.emptyList()
                : standingsProvider.getStandings(selected.getId(), selected.getSeason());

        model.addAttribute("selectedLeague", selected);
        model.addAttribute("standings", rows);
        return "standings";
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal AuthenticatedUser actor, Model model) {
        if (actor == null) {
            return "redirect:/login";
        }

        var user = userService.findById(actor.getId());
        List<TeamDto> favorites = favoriteService.list(actor.getId());
        List<NotificationDto> notifications = notificationService.list(actor.getId());
        List<Team> allTeams = teamRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        Set<Long> favoriteIds = favorites.stream().map(TeamDto::id).collect(Collectors.toSet());
        List<Team> availableTeams = allTeams.stream()
                .filter(team -> !favoriteIds.contains(team.getId()))
                .collect(Collectors.toList());

        UserUpdateRequest profileForm = new UserUpdateRequest();
        profileForm.setDisplayName(user.getDisplayName());
        profileForm.setAvatarUrl(user.getAvatarUrl());
        profileForm.setGender(user.getGender());

        model.addAttribute("profile", user);
        model.addAttribute("favorites", favorites);
        model.addAttribute("notifications", notifications);
        model.addAttribute("availableTeams", availableTeams);
        if (!model.containsAttribute("profileForm")) {
            model.addAttribute("profileForm", profileForm);
        }
        return "dashboard";
    }

    private League selectLeague(Long leagueId, List<League> all) {
        if (all.isEmpty()) {
            return null;
        }
        if (leagueId == null) {
            return all.get(0);
        }
        return all.stream()
                .filter(l -> l.getId().equals(leagueId))
                .findFirst()
                .orElse(all.get(0));
    }
}
