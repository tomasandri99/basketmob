package is.hi.basketmob.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "basketmob.rate-limit.enabled=true",
        "basketmob.rate-limit.limit=2",
        "basketmob.rate-limit.window=PT10M"
})
class RateLimitFilterTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void blocksRequestsAfterLimit() throws Exception {
        mockMvc.perform(get("/api/v1/games")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/games")).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/games")).andExpect(status().isTooManyRequests());
    }
}
