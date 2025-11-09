package is.hi.basketmob.controller;

import is.hi.basketmob.dto.LoginForm;
import is.hi.basketmob.dto.RegisterForm;
import is.hi.basketmob.entity.User;
import is.hi.basketmob.repository.UserRepository;
import is.hi.basketmob.security.AuthenticatedUser;
import is.hi.basketmob.service.AuthTokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Controller
public class UiAuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenService authTokenService;

    public UiAuthController(UserRepository userRepository,
                            PasswordEncoder passwordEncoder,
                            AuthTokenService authTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authTokenService = authTokenService;
    }

    @GetMapping("/login")
    public String loginForm(@AuthenticationPrincipal AuthenticatedUser user, Model model) {
        if (user != null) {
            return "redirect:/dashboard";
        }
        if (!model.containsAttribute("loginForm")) {
            model.addAttribute("loginForm", new LoginForm());
        }
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@Valid @ModelAttribute("loginForm") LoginForm form,
                          BindingResult result,
                          HttpServletResponse response,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "login";
        }

        var userOpt = userRepository.findByEmail(form.getEmail());
        if (userOpt.isEmpty() || !passwordEncoder.matches(form.getPassword(), userOpt.get().getPassword())) {
            result.reject("invalid", "Vitlaus innskráningarupplýsing");
            return "login";
        }

        User user = userOpt.get();
        issueCookie(user, response);
        redirectAttributes.addFlashAttribute("toast", "Velkomin(n) " + user.getDisplayName());
        return "redirect:/dashboard";
    }

    @GetMapping("/register")
    public String registerForm(@AuthenticationPrincipal AuthenticatedUser user, Model model) {
        if (user != null) {
            return "redirect:/dashboard";
        }
        if (!model.containsAttribute("registerForm")) {
            model.addAttribute("registerForm", new RegisterForm());
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerForm") RegisterForm form,
                           BindingResult result,
                           HttpServletResponse response,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "register";
        }

        if (userRepository.findByEmail(form.getEmail()).isPresent()) {
            result.rejectValue("email", "duplicate", "Netfang er þegar í notkun");
            return "register";
        }

        OffsetDateTime nowUtc = OffsetDateTime.now(ZoneOffset.UTC);
        User user = new User();
        user.setEmail(form.getEmail());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setDisplayName(form.getDisplayName());
        user.setCreatedAt(nowUtc);
        user.setUpdatedAt(nowUtc);
        user = userRepository.save(user);

        issueCookie(user, response);
        redirectAttributes.addFlashAttribute("toast", "Nýskráning tókst – velkomin(n)!");
        return "redirect:/dashboard";
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response, RedirectAttributes redirectAttributes) {
        ResponseCookie cookie = ResponseCookie.from("BM_TOKEN", "")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        redirectAttributes.addFlashAttribute("toast", "Útskráning tókst");
        return "redirect:/";
    }

    private void issueCookie(User user, HttpServletResponse response) {
        String token = authTokenService.issueToken(user.getId(), user.isAdmin());
        ResponseCookie cookie = ResponseCookie.from("BM_TOKEN", token)
                .path("/")
                .maxAge(Duration.ofDays(1))
                .httpOnly(true)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
