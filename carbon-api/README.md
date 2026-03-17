# 🌍 Carbon API — Empreinte Carbone des Sites Physiques

API REST Spring Boot pour calculer et historiser l'empreinte carbone de sites physiques (bâtiments, matériaux, énergie, parking).  
Développée dans le cadre du **Hackathon #26 — Capgemini x SUP de Vinci**.

---

## Stack technique

| Composant       | Technologie                        |
|-----------------|------------------------------------|
| Langage         | Java 21                            |
| Framework       | Spring Boot 3.5.11                 |
| Build           | Maven                              |
| Base de données | PostgreSQL 17                      |
| ORM             | Spring Data JPA / Hibernate        |
| Export PDF      | iText PDF 5.5.13                   |
| Conteneurisation| Docker + Docker Compose            |

---

## Prérequis

- Java 21+
- Maven 3.9+
- Docker & Docker Compose

---

## Lancement rapide (Docker)

```bash
git clone https://github.com/ton-username/carbon-api.git
cd carbon-api
docker compose up --build
```

L'API est accessible sur : **http://localhost:8080**  
PostgreSQL tourne sur : **localhost:5432**

---

## Lancement local (sans Docker)

**1. Lancer PostgreSQL**
```bash
docker run --name hackathon-db \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=hackathon \
  -p 5432:5432 \
  -d postgres
```

**2. Configurer `src/main/resources/application.properties`**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/hackathon
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.open-in-view=false
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
```

**3. Lancer l'API**
```bash
./mvnw spring-boot:run
```

---

## Endpoints

### Sites
| Méthode | Endpoint            | Description                          |
|---------|---------------------|--------------------------------------|
| GET     | `/api/sites`        | Lister tous les sites avec KPIs CO₂  |
| GET     | `/api/sites/{id}`   | Obtenir un site par ID               |
| POST    | `/api/sites`        | Créer un site et calculer son CO₂    |
| DELETE  | `/api/sites/{id}`   | Supprimer un site                    |

### Comparaison
| Méthode | Endpoint             | Description                          |
|---------|----------------------|--------------------------------------|
| POST    | `/api/comparaison`   | Comparer plusieurs sites (body : `[1, 2]`) |

### ADEME
| Méthode | Endpoint                      | Description                       |
|---------|-------------------------------|-----------------------------------|
| GET     | `/api/ademe/facteurs`         | Facteurs ADEME live (avec fallback)|
| GET     | `/api/ademe/facteurs/defaut`  | Facteurs ADEME V23.6 officiels    |

### Export PDF
| Méthode | Endpoint                    | Description                       |
|---------|-----------------------------|-----------------------------------|
| GET     | `/api/pdf/site/{id}`        | Rapport PDF d'un site             |
| POST    | `/api/pdf/comparaison`      | Rapport PDF de comparaison        |

---

## Exemple de requête

```bash
curl -X POST http://localhost:8080/api/sites \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Site Rennes Capgemini",
    "location": "Rennes",
    "superficie": 11771,
    "nbParking": 308,
    "consoEnergetique": 1840,
    "nbEmployes": 1800,
    "quantiteBeton": 5000,
    "quantiteAcier": 800,
    "quantiteVerre": 200,
    "quantiteBois": 100
  }'
```

**Réponse :**
```json
{
  "id": 1,
  "name": "Site Rennes Capgemini",
  "location": "Rennes",
  "co2Construction": 2875000.0,
  "co2Exploitation": 104880000.0,
  "co2Total": 107755000.0,
  "co2ParM2": 9154.28,
  "co2ParEmploye": 59863.89
}
```

---

## Facteurs d'émission (Base Carbone® ADEME V23.6)

| Matériau              | Facteur      | Unité          |
|-----------------------|--------------|----------------|
| Béton                 | 250          | kgCO₂e/tonne   |
| Acier                 | 1 800        | kgCO₂e/tonne   |
| Verre                 | 900          | kgCO₂e/tonne   |
| Bois                  | 50           | kgCO₂e/tonne   |
| Électricité (réseau FR)| 57          | kgCO₂e/MWh     |

**Formules :**
```
CO₂_construction = (béton × 250) + (acier × 1800) + (verre × 900) + (bois × 50)
CO₂_exploitation = consoEnergetique (MWh) × 1000 × 57
CO₂_total        = CO₂_construction + CO₂_exploitation
```

---

## Structure du projet

```
src/main/java/com/hackathon/carbon/
├── controller/
│   ├── SiteController.java
│   ├── AdemeController.java
│   ├── ComparaisonController.java
│   └── PdfController.java
├── service/
│   ├── SiteService.java
│   ├── AdemeService.java
│   ├── ComparaisonService.java
│   └── PdfService.java
├── repository/
│   └── SiteRepository.java
├── model/
│   └── Site.java
├── dto/
│   ├── SiteDTO.java
│   ├── SiteResultDTO.java
│   ├── FacteurEmissionDTO.java
│   └── ComparaisonDTO.java
└── CorsConfig.java
```

---

## Variables d'environnement Docker

| Variable                         | Valeur par défaut                          |
|----------------------------------|--------------------------------------------|
| `SPRING_DATASOURCE_URL`          | `jdbc:postgresql://postgres:5432/hackathon`|
| `SPRING_DATASOURCE_USERNAME`     | `postgres`                                 |
| `SPRING_DATASOURCE_PASSWORD`     | `postgres`                                 |
| `SPRING_JPA_HIBERNATE_DDL_AUTO`  | `update`                                   |

---

## Données de référence — Site Capgemini Rennes

Données fournies dans le cahier des charges Hackathon #26 :

| Champ                  | Valeur        |
|------------------------|---------------|
| Surface totale         | 11 771 m²     |
| Parking sous-dalle     | 41 places     |
| Parking sous-sol       | 184 places    |
| Parking aériens        | 83 places     |
| **Total parking**      | **308 places**|
| Consommation énergie   | 1 840 MWh/an  |
| Collaborateurs         | ~1 800        |
| Postes de travail      | 1 037         |

---

## Licence

Projet réalisé dans le cadre du Hackathon #26 — SUP de Vinci × Capgemini — Mars 2026.