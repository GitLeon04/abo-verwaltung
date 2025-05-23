package de.meinprojekt.controller;

import de.meinprojekt.model.Interval;
import de.meinprojekt.model.Subscription;
import de.meinprojekt.repository.SubscriptionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SubscriptionController.class)
class SubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubscriptionRepository repository;

    /* ---------- POSITIVE CASES ---------- */

    @Test
    void shouldCalculateMonthlySummaryCorrectly() throws Exception {
        Subscription monthly = Subscription.builder()
                .id(1L).name("Netflix").provider("Netflix Inc.")
                .price(BigDecimal.valueOf(12.99)).interval(Interval.MONTHLY)
                .startDate(LocalDate.of(2024, 1, 1)).canceled(false).build();

        Subscription yearly = Subscription.builder()
                .id(2L).name("Prime").provider("Amazon")
                .price(BigDecimal.valueOf(60)).interval(Interval.YEARLY)
                .startDate(LocalDate.of(2024, 1, 1)).canceled(false).build();

        given(repository.findAll()).willReturn(List.of(monthly, yearly));

        mockMvc.perform(get("/api/subscriptions/summary"))
               .andExpect(status().isOk())
               .andExpect(content().string("17.99"));
    }

    @Test
    void shouldCreateNewSubscription() throws Exception {
        String json = """
            {
              "name":"Spotify",
              "provider":"Spotify AB",
              "price":9.99,
              "interval":"MONTHLY",
              "startDate":"2025-06-01",
              "canceled":false
            }
            """;

        given(repository.save(any(Subscription.class))).willAnswer(inv -> {
            Subscription s = inv.getArgument(0);
            s.setId(1L);
            return s;
        });

        mockMvc.perform(post("/api/subscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.name").value("Spotify"));
    }

    @Test
    void shouldCancelSubscription() throws Exception {
        Subscription abo = Subscription.builder()
                .id(10L).name("Test").provider("X")
                .price(BigDecimal.TEN).interval(Interval.MONTHLY)
                .startDate(LocalDate.now()).canceled(false).build();
        Subscription canceled = Subscription.builder()
                .id(10L).name("Test").provider("X")
                .price(BigDecimal.TEN).interval(Interval.MONTHLY)
                .startDate(LocalDate.now()).canceled(true).build();

        when(repository.findById(10L)).thenReturn(Optional.of(abo));
        when(repository.save(abo)).thenReturn(canceled);

        mockMvc.perform(put("/api/subscriptions/10/cancel"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.canceled").value(true));
    }

    /* ---------- NEGATIVE / EDGE CASES ---------- */

    @Test
    void emptyBodyShouldReturn400() throws Exception {
        mockMvc.perform(post("/api/subscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
               .andExpect(status().isBadRequest());
    }

    @Test
    void badNameShouldReturn400() throws Exception {
        String badJson = """
            {
              "name":"Abo123",
              "provider":"Tester",
              "price":5,
              "interval":"MONTHLY",
              "startDate":"2025-01-01",
              "canceled":false
            }
            """;
        mockMvc.perform(post("/api/subscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(badJson))
               .andExpect(status().isBadRequest());
    }

    @Test
    void cancelNotFoundReturns404() throws Exception {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        mockMvc.perform(put("/api/subscriptions/99/cancel"))
               .andExpect(status().isNotFound());
    }

    @Test
    void summaryZeroIfAllCanceled() throws Exception {
        Subscription canceled = Subscription.builder()
                .id(1L).name("Old").provider("Z")
                .price(BigDecimal.valueOf(8)).interval(Interval.MONTHLY)
                .startDate(LocalDate.now()).canceled(true).build();

        given(repository.findAll()).willReturn(List.of(canceled));

        mockMvc.perform(get("/api/subscriptions/summary"))
               .andExpect(status().isOk())
               .andExpect(content().string("0.00"));
    }

    /* ---------- ADDITIONAL USEFUL TESTS ---------- */

    // 8) Ungültiges Interval löst 400 aus
    @Test
    void illegalIntervalReturns400() throws Exception {
        String badJson = """
            {
              "name":"Foo",
              "provider":"Bar",
              "price":5,
              "interval":"WEEKLY",
              "startDate":"2025-01-01",
              "canceled":false
            }
            """;
        mockMvc.perform(post("/api/subscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(badJson))
               .andExpect(status().isBadRequest());
    }

    // 9) Runden auf 2 Nachkommastellen wird korrekt angewendet
    @Test
    void summaryRoundingHalfUp() throws Exception {
        Subscription y1 = Subscription.builder()
                .id(1L).name("A").provider("P")
                .price(BigDecimal.valueOf(100.05)).interval(Interval.YEARLY)
                .startDate(LocalDate.now()).canceled(false).build();
        Subscription y2 = Subscription.builder()
                .id(2L).name("B").provider("P")
                .price(BigDecimal.valueOf(50.05)).interval(Interval.YEARLY)
                .startDate(LocalDate.now()).canceled(false).build();

        given(repository.findAll()).willReturn(List.of(y1, y2));

        mockMvc.perform(get("/api/subscriptions/summary"))
               .andExpect(status().isOk())
               .andExpect(content().string("12.51"));
    }
}
