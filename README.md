<div align="center">

# 🚘 Car Dealer

### Backend автосалона: от выбора комплектации до согласования заказа со складом

![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.3-6DB33F?logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?logo=postgresql&logoColor=white)
![Kafka](https://img.shields.io/badge/Apache_Kafka-231F20?logo=apachekafka&logoColor=white)
![gRPC](https://img.shields.io/badge/gRPC-00897B)
![Docker](https://img.shields.io/badge/Docker_Compose-2496ED?logo=docker&logoColor=white)

**2 сервиса · отдельные БД · REST + gRPC · Kafka + Outbox · JWT / Keycloak**

[Возможности](#возможности) · [Технологии](#технологии) · [Архитектура](#архитектура) · [Локальный запуск](#запуск) · [Тесты](#сборка-и-тесты)

</div>

---

## О проекте

Учебный pet-проект на Java 21 и Spring Boot, моделирующий работу автосалона: клиент выбирает автомобиль или собирает комплектацию, оформляет заказ и записывается на тест-драйв; сотрудники управляют складом и согласуют заказы.

Проект демонстрирует разработку REST API, разделение бизнес-логики и инфраструктуры, работу с реляционными данными, синхронные и событийные интеграции, ролевой доступ и проверку взаимодействия сервисов в контейнерах. Взаимодействие с приложением — через **REST API и Swagger UI**; веб-интерфейса пока нет.

> **Другой проект в этом репозитории:** [FinanceTracker](https://github.com/TemaStarkov/FinanceTracker) — C# / ASP.NET Core API для операций со счетами, сессий и истории транзакций.

## Что посмотреть в коде

| Задача | Решение | Где смотреть |
| --- | --- | --- |
| Получать актуальные автомобили со склада | Синхронный gRPC-вызов с Protobuf-контрактом | [Клиент](order-service/src/main/java/org/example/integration/grpc/CarGrpcClient.java) · [Сервер](storage-service/src/main/java/org/example/integration/grpc/CarGrpcService.java) · [Контракт](common-proto/src/main/proto/) |
| Согласовывать заказы между сервисами | Kafka, сохранение исходящих событий в outbox и учёт обработанных событий | [Publisher заказов](order-service/src/main/java/org/example/integration/kafka/publisher/OutboxPublisher.java) · [Интеграции склада](storage-service/src/main/java/org/example/integration/kafka/) |
| Ограничивать доступ по ролям и владельцу | JWT от Keycloak, Spring Security и проверки прав на заказ | [SecurityConfig](order-service/src/main/java/org/example/infrastructure/config/SecurityConfig.java) · [OrderSecurity](order-service/src/main/java/org/example/infrastructure/security/OrderSecurity.java) |
| Поддерживать воспроизводимую схему БД | Миграции Liquibase и начальные данные | [Миграции заказов](order-service/src/main/resources/db/) · [Миграции склада](storage-service/src/main/resources/db/) |
| Проверять полный цикл согласования | Сквозной тест с Docker Compose и ожиданием асинхронного результата | [Kafka / outbox E2E](src/integrationTest/java/org/example/Lab4KafkaEndToEndIT.java) · [CI](.github/workflows/ci.yml) |

## Возможности

- Поиск автомобилей с фильтрами, сортировкой и просмотром моделей.
- Подбор компонентов конфигурации с проверкой совместимости.
- Заказ автомобиля из наличия или в индивидуальной комплектации, изменение статуса и отмена.
- Запись на тест-драйв, подтверждение, перенос, завершение и отметка неявки.
- Управление автомобилями, запчастями и заявками на сборку.
- Разграничение доступа по ролям и проверка владельца заказа.

## Архитектура

| Модуль | Назначение |
| --- | --- |
| `order-service` | Клиентское API, заказы, конфигуратор и тест-драйвы; gRPC-клиент склада; отправка запросов на согласование в Kafka |
| `storage-service` | Склад, автомобили и детали, заявки на сборку; gRPC-сервер; обработка согласования и публикация результата |
| `common-proto` | Protocol Buffers контракт `CarService`, генерация Java-классов и gRPC stubs |

У каждого сервиса своя PostgreSQL. В обоих модулях присутствуют общие предметные модели и часть REST-контроллеров; границы сервисов отражают учебное развитие проекта.

Внутри сервисов код разделён на слои:

- `domain` — модели автомобилей, комплектаций, заказов, пользователей, перечисления и доменные исключения.
- `application` — интерфейсы и реализации бизнес-сервисов.
- `infrastructure` — JPA-сущности и репозитории, конфигурация, безопасность и хранение событий.
- `integration` — REST-контроллеры, DTO, мапперы, Kafka и gRPC.

### Согласование заказа

`order-service` сохраняет событие в outbox, publisher отправляет его в `order.sent-for-approval`. `storage-service` обрабатывает запрос, формирует результат сборки и через свой outbox отправляет ответ в `order.approval-result`. Сервис заказов получает результат и обновляет заказ. Таблицы `processed_events` используются для учёта обработанных событий и защиты от повторной обработки.

gRPC контракт предоставляет `ListAvailableCars` и `GetCarById` для получения автомобилей со склада.

## Технологии

Версии указаны по файлам сборки и конфигурации этого проекта.

| Технология | Применение |
| --- | --- |
| Java 21, Gradle Wrapper | Язык, многомодульная сборка и Java toolchains |
| Spring Boot 3.2.3, Spring Web | Запуск сервисов и REST API |
| Spring Data JPA / Hibernate, PostgreSQL | Хранение предметных данных; PostgreSQL 15 в основном Compose и 16 в тестовом |
| Liquibase | Версионируемые миграции схемы и тестовые данные |
| Spring Security, OAuth2 Resource Server | Проверка JWT и прав доступа |
| Keycloak 24.0.5 | Пользователи, realm-роли и выдача токенов |
| Apache Kafka, Spring Kafka | События согласования; outbox и учёт обработанных сообщений |
| gRPC 1.62.2, Protobuf 3.25.3 | Межсервисные запросы; Spring Boot gRPC starters 3.1.0.RELEASE |
| MapStruct 1.5.5.Final, Lombok 1.18.30 | Преобразование DTO/моделей и сокращение шаблонного кода |
| springdoc-openapi 2.5.0 | OpenAPI и Swagger UI |
| JUnit 5, Spring Boot Test, Testcontainers, Awaitility | Автоматические проверки REST, БД, gRPC и асинхронного обмена |
| Docker Compose, GitHub Actions | Локальный стенд и CI |

## Структура репозитория

```text
.
├── order-service/
│   ├── src/main/java/org/example/  # domain, application, infrastructure, integration
│   ├── src/main/resources/        # application.yml и миграции Liquibase
│   └── src/integrationTest/       # интеграционные тесты сервиса
├── storage-service/              # аналогичная структура + сборка и gRPC-сервер
├── common-proto/src/main/proto/   # контракт gRPC
├── src/integrationTest/           # сквозной Kafka/outbox сценарий
├── docker/Dockerfile.service      # образ сервиса из собранного JAR
├── docker-compose.yml            # основной локальный стенд
├── docker-compose-test.yml       # стенд сквозного теста
├── .github/workflows/            # сборка и тесты в CI
└── gradlew                       # Gradle Wrapper
```

## Запуск

### 1. Поднять локальный стенд

Нужны JDK 21, Docker с Compose v2 и свободные порты из таблицы ниже. Первая сборка скачивает Gradle и зависимости.

```bash
git clone https://github.com/TemaStarkov/Car-Dealer.git
cd Car-Dealer
chmod +x gradlew
./gradlew :order-service:bootJar :storage-service:bootJar
docker compose up -d --build
```

Dockerfile копирует готовые JAR: перед пересборкой контейнеров необходимо заново выполнить `bootJar`.

| Компонент | Адрес с компьютера |
| --- | --- |
| API заказов | http://localhost:8080 |
| API склада | http://localhost:8082 |
| Swagger заказов | http://localhost:8080/swagger-ui/index.html |
| Swagger склада | http://localhost:8082/swagger-ui/index.html |
| OpenAPI | `/v3/api-docs` на порту соответствующего сервиса |
| Keycloak | http://localhost:8081 |
| БД заказов | localhost:5433 / `order_db` |
| БД склада | localhost:5434 / `storage_db` |
| Kafka для клиента на компьютере | localhost:9094 |

В Docker сервисы используют Kafka `kafka:9092`; gRPC склада доступен внутри сети как `storage-service:9091`.

### 2. Настроить авторизацию в Keycloak

Realm автоматически не импортируется. Для защищённых API его нужно создать вручную:

1. Откройте Keycloak по адресу http://localhost:8081 и войдите как `admin` / `admin`.
2. Создайте realm `car-dealership`.
3. Создайте realm-роли `USER`, `MANAGER`, `WAREHOUSE_ADMIN`, `ADMIN` (регистр важен).
4. Создайте OpenID Connect client для локальной разработки. Для приведённого ниже примера включите Direct access grants и отключите Client authentication; client ID — `car-dealer-local`.
5. Создайте пользователя, задайте ему постоянный пароль и назначьте нужную realm-роль. Роли должны попадать в claim `realm_access.roles` access token.

JWT `sub` используется приложением как UUID пользователя. Для сценариев с существующими пользователями/заказами в БД необходимо согласовать этот UUID с предметными данными; сами пользователи Keycloak автоматически из seed-данных не создаются.

Пример получения токена (подставьте свои значения):

```bash
curl -X POST 'http://localhost:8081/realms/car-dealership/protocol/openid-connect/token' \
  -d 'grant_type=password' \
  -d 'client_id=car-dealer-local' \
  -d 'username=YOUR_USERNAME' \
  -d 'password=YOUR_PASSWORD'
```

Передавайте `access_token` в заголовке `Authorization: Bearer <access_token>`.

В Compose настроен внутренний адрес JWK Keycloak, а в `application.yml` issuer — `http://localhost:8081/realms/car-dealership`: получайте токены через этот внешний адрес. При смене адресов нужно согласовать issuer в токене и настройках сервисов.

### Роли

| Роль | Основные операции |
| --- | --- |
| `USER` | Каталог, конфигуратор, создание заказов, собственные заказы и тест-драйвы |
| `MANAGER` | Каталог и конфигуратор, просмотр заказов, изменение статусов, сопровождение тест-драйвов |
| `WAREHOUSE_ADMIN` | Каталог, складские автомобили и детали, статусы заказов, заявки на сборку |
| `ADMIN` | Административные операции, разрешённые контроллерами |

Точные ограничения заданы в `@PreAuthorize` контроллеров и `OrderSecurity`.

### Запуск сервисов без контейнеров

```bash
docker compose up -d order-db storage-db kafka keycloak
./gradlew :storage-service:bootRun
# В другом терминале:
./gradlew :order-service:bootRun --args='--grpc.client.storage-service.address=static://localhost:9091'
```

### Проверка и остановка стенда

```bash
docker compose ps
docker compose logs -f order-service storage-service
docker compose down
```

Данные сохраняются в Docker volumes. Значения `postgres` / `postgres` и `admin` / `admin` — демонстрационные настройки локального стенда. Для внешнего развёртывания нужны собственные учётные данные и конфигурация. Kafka в основном Compose использует тег `latest`, поэтому поведение стенда может меняться при обновлении образа.

## REST API

Основные группы маршрутов (подробные DTO и параметры смотрите в Swagger):

| Путь | Назначение |
| --- | --- |
| `/api/catalog/cars`, `/api/catalog/models` | Каталог автомобилей и моделей |
| `/api/v1/cars` на order-service | Получение автомобилей через gRPC склада |
| `/api/configurator/configurations` | Создание конфигурации |
| `/api/configurator/models/{modelId}/components` | Компоненты модели |
| `/api/orders/in-stock`, `/api/orders/custom` | Создание заказа |
| `/api/orders/{id}/status`, `/api/orders/{id}/cancel` | Изменение статуса и отмена |
| `/api/test-drives` | Запись и сопровождение тест-драйвов |
| `/api/warehouse/cars`, `/api/warehouse/parts` | Управление складом |
| `/api/storage/assembly-orders` на storage-service | Заявки на сборку |

После настройки Keycloak получите токен и выполните первый запрос к каталогу:

```bash
curl 'http://localhost:8080/api/catalog/cars' \
  -H 'Authorization: Bearer YOUR_ACCESS_TOKEN'
```

## Сборка и тесты

```bash
./gradlew assemble
./gradlew test
./gradlew integrationTest
```

Интеграционные тесты требуют работающий Docker и загружают контейнеры. В сервисах проверяются миграции и seed-данные, каталог, заказы и gRPC. Корневой `Lab4KafkaEndToEndIT` поднимает `docker-compose-test.yml` и проверяет прохождение согласования через Kafka и outbox. Задача `integrationTest` зависит от сборки JAR обоих сервисов. `check` также запускает интеграционные тесты.

GitHub Actions выполняет сборку, unit- и интеграционные тесты. Наличие задачи `test` само по себе не означает наличие отдельных unit-тестов: текущие тестовые сценарии находятся в `src/integrationTest`.

## Если запуск не удался

| Симптом | Что проверить |
| --- | --- |
| Контейнер сервиса не собирается: JAR не найден | Сначала выполните `./gradlew :order-service:bootJar :storage-service:bootJar`, затем повторите `docker compose up -d --build` |
| Порт уже занят | Освободите порт из таблицы адресов или согласованно измените Compose и настройки приложения |
| API возвращает `401` | Realm создан, токен не истёк, issuer совпадает с `http://localhost:8081/realms/car-dealership` |
| API возвращает `403` | Назначена роль нужного маршрута, она присутствует в `realm_access.roles`; для заказа также проверьте владельца |
| gRPC недоступен при `bootRun` | Склад запущен, клиенту передан адрес `static://localhost:9091` |

## Текущий статус

Реализован backend учебного автосалона с интеграциями и контейнерным стендом. В сервисах частично дублируются предметные модели и REST-контроллеры; дальнейшее развитие — уточнение границ сервисов, автоматический импорт realm Keycloak и фиксация версии Kafka для воспроизводимого запуска.
