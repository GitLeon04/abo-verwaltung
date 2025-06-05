package de.meinprojekt.controller;

import de.meinprojekt.model.Interval;
import de.meinprojekt.model.Subscription;
import de.meinprojekt.repository.SubscriptionRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    @Autowired
    private SubscriptionRepository repository;

    /* ---------- CRUD ---------- */

    @PostMapping
    public ResponseEntity<Subscription> create(@Valid @RequestBody Subscription s) {
        return ResponseEntity.ok(repository.save(s));
    }

    @GetMapping
    public List<Subscription> all() {
        return repository.findAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Subscription> update(@PathVariable Long id,
                                               @Valid @RequestBody Subscription dto) {
        return repository.findById(id)
                .map(old -> {
                    dto.setId(id);
                    return ResponseEntity.ok(repository.save(dto));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Subscription> cancel(@PathVariable Long id) {
        return repository.findById(id)
                .map(sub -> {
                    sub.setCanceled(true);
                    return ResponseEntity.ok(repository.save(sub));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /* ---------- Summary ---------- */

    @GetMapping("/summary")
    public BigDecimal summary() {
        BigDecimal sum = repository.findAll().stream()
                .filter(s -> !s.isCanceled())
                .map(s -> s.getInterval() == Interval.MONTHLY
                        ? s.getPrice()
                        : s.getPrice().divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return sum.setScale(2, RoundingMode.HALF_UP);
    }

    /* ---------- CSV-Export ---------- */

    @GetMapping(value = "/export", produces = "text/csv")
    public void exportCsv(HttpServletResponse response) throws IOException {
        response.setHeader("Content-Disposition", "attachment; filename=\"subscriptions.csv\"");
        try (PrintWriter pw = response.getWriter()) {
            // Kopfzeile
            pw.println("Name,Provider,Price,Interval,StartDate,Canceled");

            // Jede Zeile als sauber gejointe Strings
            repository.findAll().forEach(s -> {
                String price = s.getPrice()
                                 .setScale(2, RoundingMode.HALF_UP)
                                 .toString();
                String interval = s.getInterval().name();
                String startDate = s.getStartDate().toString();
                String canceled = Boolean.toString(s.isCanceled());

                // Escape-Quotes für name und provider
                String nameEsc = escapeCsv(s.getName());
                String provEsc = escapeCsv(s.getProvider());

                String line = String.join(",",
                        nameEsc,
                        provEsc,
                        price,
                        interval,
                        startDate,
                        canceled
                );
                pw.println(line);
            });
        }
    }

    /** 
     * CSV-Escaper: setzt das Feld in doppelte Anführungszeichen
     * und verdoppelt darin vorhandene Anführungszeichen.
     */
    private String escapeCsv(String text) {
        if (text == null) {
            return "";
        }
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }
}
