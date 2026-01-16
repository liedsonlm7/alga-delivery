![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![Build](https://img.shields.io/badge/build-passing-success)

# 🚚 Arquitetura de Microsserviços com Spring Cloud Gateway

Projeto de estudo e referência que demonstra uma **arquitetura de microsserviços em Java/Spring Boot**, utilizando **API Gateway**, **Service Discovery**, **padrões de resiliência**, e conceitos de **DDD (Domain Driven Design)** e **Event-Driven Architecture**.

O sistema é composto por múltiplos microserviços independentes (ex.: `courier-management`, `delivery-tracking`), orquestrados por um **API Gateway reativo**, responsável por roteamento, segurança, resiliência e exposição controlada das APIs.

<br>

## 🧭 Visão Geral da Arquitetura

* **API Gateway** como ponto único de entrada
* **Service Discovery** com Eureka
* **Microsserviços independentes**, cada um com seu próprio domínio e banco de dados
* **Resiliência** com Retry e Circuit Breaker
* **Comunicação síncrona (HTTP)** e **assíncrona (eventos/Kafka)**

```text
flowchart LR
  Client -->|HTTP| Gateway[Gateway\n(Spring Cloud Gateway)]
  Gateway -->|lb://courier-management| Eureka[Eureka\n(Service Discovery)]
  Gateway -->|lb://delivery-tracking| Eureka
  Eureka -->|resolve| ServiceA[Courier Management\nController → Service → Repository]
  Eureka -->|resolve| ServiceB[Delivery Tracking\nController → Service → Repository]
  ServiceA --> DB1[(Database)]
  ServiceB --> DB2[(Database)]
  Gateway -. Resilience4j .-> ServiceA
  Gateway -. Resilience4j .-> ServiceB
```

<br>

## 🏗️ Padrão Arquitetural

* **API Gateway Pattern**: centraliza acesso, roteamento e políticas transversais
* **Microservices Architecture**: serviços independentes e desacoplados
* **Layered Architecture (por serviço)**:

  * Controller → Service → Repository
* **Reactive Gateway** com Spring WebFlux

Cada microserviço possui:

* Ciclo de vida independente
* Compartilhamento de um **banco de dados único** (monolítico), executado em container Docker
* Modelo de domínio isolado (Bounded Context)

<br>

## 📐 Domain Driven Design (DDD)

### Design Estratégico

* Cada microserviço representa um **Bounded Context**

  * Ex.: `courier-management` (Couriers)
  * Ex.: `delivery-tracking` (Entregas)

### Design Tático

* **Entidades** e **Agregados** no domínio
* **Value Objects** para regras e validações
* **Repositórios** como abstração de persistência
* **Domain Services** para regras complexas

Os Controllers apenas expõem APIs REST e delegam a lógica para o domínio.

<br>

## 🔁 Comunicação entre Serviços

### Comunicação Síncrona

* HTTP REST
* Resolução dinâmica via `lb://<service-name>` usando Eureka
* Cliente HTTP (WebClient / RestClient)

### Comunicação Assíncrona

* **Kafka** como broker de mensageria
* Uso de **eventos de domínio** (ex.: `DeliveryFulFilled`)
* Comunicação publish/subscribe entre serviços

<br>

## 🛡️ Resiliência e Tolerância a Falhas

Configurada principalmente no **API Gateway**, usando **Resilience4j**:

* **Retry** com backoff exponencial
* **Circuit Breaker** para isolar falhas
* **Timeouts** de conexão e resposta

Benefícios:

* Proteção contra serviços instáveis
* Evita efeito cascata
* Melhora disponibilidade geral do sistema

<br>

## 🛠️ Tecnologias Utilizadas

* **Java 21+**
* **Spring Boot**
* **Spring Cloud Gateway (WebFlux)**
* **Spring Cloud Netflix Eureka**
* **Resilience4j**
* **Spring Data JPA**
* **Kafka** (mensageria e eventos assíncronos)
* **Maven**
* **Docker / Docker Compose** (infraestrutura: banco de dados e Kafka)
* **REST Assured** (testes de integração)

<br>

## 🗂️ Estrutura do Projeto

Exemplo de **monorepo** com múltiplos módulos:

```text
├── gateway/
│   ├── src/main/java
│   │   ├── GatewayApplication
│   │   └── Resilience4jCircuitBreakerEventConsumer
│   ├── src/main/resources
│   │   └── application.yml
│   └── pom.xml
│
├── courier-management/
│   ├── src/main/java
│   │   ├── api
│   │   ├── domain
│   │   ├── infraestructure
│   │   ├── Application
│   ├── src/main/resources
│   │   └── application.yml
│   └── pom.xml
│
├── delivery-tracking/
│   └── (estrutura similar ao courier-management)
│
├── service-registry/
│   ├── src/main/java
│   ├── src/main/resources
│   │   └── application.yml
│   └── pom.xml
│
└── docker-compose.yml 
```

<br>

## 🐳 Infraestrutura com Docker

A infraestrutura do projeto é executada via **Docker Compose**, incluindo:

* Banco de dados PostgreSQL executado por um container via PgAdmin
* Kafka (broker de mensageria)

Os serviços Java se conectam a esses containers em tempo de execução.

<br>

## ▶️ Como Executar o Projeto

### Pré-requisitos

* Java 21+
* Maven
* Docker 

### Passos

```bash
# Clonar o repositório
git clone https://github.com/liedsonlm7/alga-delivery.git

# Entrar no projeto
cd alga-delivery

# Subir infraestrutura 
docker-compose up -d # sobe banco de dados e Kafka

# Subir o Eureka Server
cd service-registry
mvn spring-boot:run

# Subir o Gateway
cd gateway
mvn spring-boot:run

# Subir os microserviços
cd courier-management
mvn spring-boot:run

cd delivery-tracking
mvn spring-boot:run
```

* Gateway: [http://localhost:9999](http://localhost:9999)
* Eureka Dashboard: [http://localhost:8761](http://localhost:8761)

<br>

## 🌐 Endpoints (exemplo)

### Gateway

```http
GET /api/v1/couriers/**
GET /api/v1/deliveries/**
```

### Rotas públicas

```http
GET /public/couriers
GET /public/couriers/{id}
```

> O Gateway aplica filtros como `RewritePath` e remove atributos sensíveis da resposta.

<br>

## 🧪 Testes

* **Testes unitários** nas camadas de domínio e service
* **Testes de integração** com REST Assured
* **Testes de contrato** para APIs REST

<br>

## 🎯 Objetivo do Projeto

* Demonstrar uma **arquitetura moderna de microsserviços**
* Aplicar conceitos reais de **DDD**, **resiliência** e **event-driven**
* Servir como **base de estudo** ou **template inicial** para projetos distribuídos

<br>

## 📄 Licença

Projeto desenvolvido para fins educacionais e de estudo em arquitetura de software com Java e Spring.
