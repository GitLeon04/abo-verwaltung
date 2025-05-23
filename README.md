# Abo-Verwaltung (TDD-Projekt)

<!-- Test für CI/CD -->


Ein voll funktionsfähiges Aboverwaltungssystem mit moderner Weboberfläche, REST-API, Validierung, Testabdeckung und Exportfunktionen – vollständig entwickelt nach dem Prinzip **testgetriebener Entwicklung (TDD)**.

---

## 🔧 Installation & Start

### Voraussetzungen
- Java 17+
- Maven 3.8+

### Build & Run

```bash
mvn clean package
java -jar target/abo-verwaltung-1.0.0.jar
```

**→ Browser öffnen:** [http://localhost:8080](http://localhost:8080)  
**→ API-Test mit Swagger:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## 🧩 Features

- Abonnements erstellen, bearbeiten, kündigen & löschen
- Monatskosten automatisch berechnen
- Export aller Abos als `.csv`
- Swagger/OpenAPI: interaktive API-Dokumentation
- Responsive UI mit Kalender + Autoformatierung
- Dark-Mode-Toggle per Button
- Vollständig testgetrieben entwickelt

---

## 🔗 API-Endpunkte (Auszug)

| Methode | Pfad                     | Beschreibung                     |
|---------|--------------------------|----------------------------------|
| GET     | `/api/subscriptions`     | Alle Abos                        |
| POST    | `/api/subscriptions`     | Neues Abo erstellen              |
| PUT     | `/api/subscriptions/{id}`| Abo aktualisieren                |
| PUT     | `/api/subscriptions/{id}/cancel` | Abo kündigen              |
| DELETE  | `/api/subscriptions/{id}`| Abo löschen                      |
| GET     | `/api/subscriptions/summary` | Monatskosten berechnen       |
| GET     | `/api/subscriptions/export`  | Export als CSV                 |

Weitere Details siehe Swagger:  
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## ✅ Testabdeckung

Tests wurden mit JUnit5 und MockMvc geschrieben.

```bash
mvn test
```

**Testtypen:**
- Unit-Tests für Validierung & Logik
- Controller-Tests mit MockMvc
- Negative Tests für Fehlerfälle
- Spezialfälle wie Rundung, leere Liste, ungültiger Input

### JaCoCo Coverage Report
Nach dem Testlauf generiert unter:
```
target/site/jacoco/index.html
```

> 🔍 **Screenshot** im Ordner `docs/` hinzufügen.

---

## 🐳 Docker (optional)

```dockerfile
# Dockerfile
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/abo-verwaltung-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

```yaml
# docker-compose.yml
services:
  abo:
    build: .
    ports: ["8080:8080"]
```

---

## 📸 Screenshots

| Seite | Bild |
|-------|------|
| Hauptansicht (Light) | `docs/ui-light.png` |
| Dark Mode aktiv      | `docs/ui-dark.png` |
| Swagger              | `docs/swagger.png` |
| CSV-Export           | `docs/csv-export.png` |

---

## 👨‍💻 Autor / Version

Max Mustermann  
HTWK Leipzig, Modul: Testgetriebene Anwendungsentwicklung  
Version: 1.0.0  
Mai 2025