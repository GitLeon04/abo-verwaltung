package de.meinprojekt.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Pattern(
        regexp = "[A-Za-zÄÖÜäöüß\\s+&\\-.]+",
        message = "Erlaubt: Buchstaben, Leerzeichen, +, &, -, ."
    )
    private String name;

    @NotBlank
    @Pattern(
        regexp = "[A-Za-zÄÖÜäöüß\\s+&\\-.]+",
        message = "Erlaubt: Buchstaben, Leerzeichen, +, &, -, ."
    )
    private String provider;

    @DecimalMin(value = "0.00", inclusive = true,
                message = "Preis darf nicht negativ sein")
    @Digits(integer = 11, fraction = 2,     // max 99 999 999 999,99
            message = "Maximal 11 Ziffern vor und 2 nach dem Komma erlaubt")
    private BigDecimal price;

    @Column(name = "subscription_interval")
    @Enumerated(EnumType.STRING)
    private Interval interval;

    @NotNull(message = "Startdatum ist erforderlich")
    private LocalDate startDate;

    private boolean canceled;
}
