
# 📦 Abo‑Verwaltung

Eine schlanke Spring‑Boot‑Anwendung (Java 17) mit einem puren HTML / JavaScript‑Frontend zum Verwalten deiner laufenden Abonnements.  
Dank **Docker** kannst du die App mit genau **einem** Befehl starten – ganz ohne lokale JDK‑ oder Maven‑Installation.

---

## Inhaltsübersicht

| Bereich  | Tech / Tool |
|----------|-------------|
| **Backend**  | Spring Boot 3 · REST · JPA (H2 / MySQL) |
| **Frontend** | Plain HTML / JS · Flatpickr · Dark‑Mode · CSV‑Export‑Button |
| **CI / CD**  | GitHub Actions – Maven‑Build, Tests & automatisches Docker‑Image in GHCR |
| **Docker‑Image** | `ghcr.io/gitleon04/abo-verwaltung:latest` |

---

## 1 · Voraussetzungen

* **Docker 24 oder neuer** (Docker Desktop unter macOS / Windows oder die Engine unter Linux)

Damit ist alles abgedeckt – kein zusätzliches JDK oder Maven nötig, wenn du nur das fertige Release nutzen möchtest.

---

## 2 · Schnellstart (Production‑like)

```bash
docker pull ghcr.io/gitleon04/abo-verwaltung:latest
docker run --rm -p 8080:8080 ghcr.io/gitleon04/abo-verwaltung:latest
```

*Aufruf im Browser:* **http://localhost:8080**  
Hier kannst du Abos anlegen, den Dark‑Mode umschalten und über den CSV‑Button deine Daten exportieren.

---

## 3 · Eigenes Image bauen (optional)

Falls du Codeänderungen testen oder dein eigenes Image veröffentlichen möchtest:

```bash
# Repository klonen
git clone https://github.com/GitLeon04/abo-verwaltung.git
cd abo-verwaltung

# JAR bauen
mvn -B clean package

# Docker‑Image erstellen
docker build -t abo-verwaltung:local .

# Anwendung starten
docker run --rm -p 8080:8080 abo-verwaltung:local
```

---

## 4 · Tests lokal ausführen

Alle Unit‑ und Integration‑Tests laufen komplett ohne Docker:

```bash
# Nur Tests (schnell)
mvn test

# Tests + Coverage (JaCoCo) + Checkstyle
mvn verify
```

Die gleiche Test‑Suite läuft auf GitHub automatisch bei jedem Push oder Pull‑Request.  
Ergebnisse findest du in **GitHub → Actions → Java CI mit Maven**.

---

## 5 · Entwicklungsmodus mit Hot‑Reload (optional)

```bash
mvn spring-boot:run
```

*Vorteil:* Änderungen an Java‑Klassen oder Dateien unter `src/main/resources/static/` werden nach wenigen Sekunden automatisch neu geladen.

---

## 6 · Deployment mit Docker Compose + MySQL (optional)

```yaml
version: "3.9"
services:
  db:
    image: mysql:8
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: abodb
    volumes: [db-data:/var/lib/mysql]

  abo-verwaltung:
    image: ghcr.io/gitleon04/abo-verwaltung:latest
    ports: ["80:8080"]
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://db:3306/abodb
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root
    depends_on: [db]

volumes:
  db-data:
```

```bash
docker compose up -d
```

Damit läuft MySQL im Container **db**; die Anwendung verbindet sich per Umgebungsvariablen automatisch.

---

## 7 · CI / CD auf einen Blick

1. **CI‑Workflow (`ci.yml`)** – jeder Push oder Pull‑Request baut das Projekt und führt alle Tests aus.  
2. **CD‑Workflow (`cd.yml`)** – nach grünem Build wird ein Docker‑Image erzeugt und als  
   `ghcr.io/gitleon04/abo-verwaltung` mit den Tags **`latest`** und der Commit‑SHA veröffentlicht.

So liegt nach jedem Merge in `main` unmittelbar ein neues Release‑Artefakt bereit, das auf jedem Docker‑Host ausgeführt werden kann.