package de.meinprojekt;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AboverwaltungApplication {

    public static void main(String[] args) {
        SpringApplication.run(AboverwaltungApplication.class, args);
    }

    @PostConstruct
    public void printStartupLinks() {
        System.out.println("\n✅ Aboverwaltung erfolgreich gestartet!");
        System.out.println("🔗 Öffne im Browser:");
        System.out.println("   🏠 Web-Oberfläche:    http://localhost:8080");
        System.out.println("   📊 Swagger UI (API):  http://localhost:8080/swagger-ui/index.html");
        System.out.println("   📁 CSV-Export:        http://localhost:8080/api/subscriptions/export");
    }
}
