# ⚙️ NoCodeAPI Backend

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-brightgreen)
![Java](https://img.shields.io/badge/Java-23-blue)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue)
![Flyway](https://img.shields.io/badge/Flyway-Migrations-red)
![Security](https://img.shields.io/badge/Spring%20Security-Auth-lightgrey)
![OpenAPI](https://img.shields.io/badge/OpenAPI-springdoc-blueviolet)
![Status](https://img.shields.io/badge/Status-Work%20In%20Progress-yellow)
![Type](https://img.shields.io/badge/Type-Private%20Project-purple)

Backend Service für **NoCodeAPI**, eine Plattform zur konfigurierbaren API Erstellung.

Das Backend übernimmt Persistenz, Sicherheit, Verarbeitung und Schnittstellenlogik.

---

## 🚀 Features

✔ REST API mit Spring Web
✔ Persistenz via Spring Data JPA
✔ Datenbank Migrationen mit Flyway
✔ PostgreSQL Support
✔ Input Validierung
✔ Security Layer mit Spring Security
✔ OAuth2 Unterstützung
✔ OpenAPI Dokumentation
✔ Testintegration via Testcontainers
✔ Reaktive Erweiterung über WebFlux

---

## 🧰 Tech Stack

### ⚙️ Backend

* Java 23
* Spring Boot 3.5.0
* Spring Web
* Spring WebFlux
* Spring Data JPA
* Spring Validation
* Lombok

### 🔐 Security

* Spring Security
* OAuth2 Resource Server
* OAuth2 Client

### 🗄️ Datenbank

* PostgreSQL
* Flyway
* H2 (Dev Option)

### 📄 API Dokumentation

* springdoc OpenAPI

### 🧪 Testing

* JUnit
* Testcontainers

### 🧩 Weitere Komponenten

* Javalin
* Jetty

---

## 🏗️ Architektur

Schichtenstruktur:

* Controller Layer
* Service Layer
* Repository Layer

Persistenz via:

* JPA

Migration via:

* Flyway

API Dokumentation via:

* OpenAPI

---

## 🛠️ Setup

Voraussetzungen:

* Java 23
* Maven
* PostgreSQL (optional H2 für lokale Entwicklung)

Starten:

mvn spring-boot:run

---

## 🔗 Zugehöriges Projekt

[Frontend Repository](https://github.com/vincentkalwoda/nocodeapi-frontend)

---

## 🚧 Status

Work in Progress
