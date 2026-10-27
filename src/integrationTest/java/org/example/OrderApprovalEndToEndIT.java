package org.example;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
class OrderApprovalEndToEndIT {

    private static final int POSTGRES_PORT = 5432;

    @Container
    static ComposeContainer environment = new ComposeContainer(
            new File("docker-compose-test.yml")
    )
            .withExposedService("order_db", POSTGRES_PORT,
                    Wait.forListeningPort().withStartupTimeout(Duration.ofMinutes(2)))
            .withExposedService("storage_db", POSTGRES_PORT,
                    Wait.forListeningPort().withStartupTimeout(Duration.ofMinutes(2)));

    @BeforeEach
    void waitForDatabaseSchema() {
        Awaitility.await()
                .atMost(Duration.ofSeconds(120))
                .pollInterval(Duration.ofSeconds(2))
                .untilAsserted(() -> {
                    try (Connection orderDb = orderDbConnection();
                         Connection storageDb = storageDbConnection()) {

                        assertTrue(tableExists(orderDb, "orders"));
                        assertTrue(tableExists(orderDb, "outbox_events"));
                        assertTrue(tableExists(orderDb, "processed_events"));

                        assertTrue(tableExists(storageDb, "assembly_orders"));
                        assertTrue(tableExists(storageDb, "outbox_events"));
                        assertTrue(tableExists(storageDb, "processed_events"));
                    }
                });
    }

    @Test
    void shouldProcessOrderApprovalFlowThroughKafkaAndOutbox() throws Exception {
        UUID outboxEventId = UUID.randomUUID();
        UUID sourceEventId = UUID.randomUUID();

        UUID orderId = UUID.fromString("60000000-0000-0000-0000-000000000001");
        UUID carId = UUID.fromString("30000000-0000-0000-0000-000000000001");
        UUID traceId = UUID.randomUUID();

        try (Connection orderDb = orderDbConnection()) {
            insertOrderSentForApprovalOutboxEvent(
                    orderDb,
                    outboxEventId,
                    sourceEventId,
                    orderId,
                    carId,
                    traceId
            );
        }

        Awaitility.await()
                .atMost(Duration.ofSeconds(120))
                .pollInterval(Duration.ofSeconds(2))
                .untilAsserted(() -> {
                    try (Connection orderDb = orderDbConnection()) {
                        assertEquals(
                                "PUBLISHED",
                                selectOutboxStatus(orderDb, outboxEventId),
                                "OrderService outbox event should be published"
                        );
                    }
                });

        Awaitility.await()
                .atMost(Duration.ofSeconds(120))
                .pollInterval(Duration.ofSeconds(2))
                .untilAsserted(() -> {
                    try (Connection storageDb = storageDbConnection()) {
                        String assemblyStatus = selectAssemblyOrderStatus(storageDb, orderId);

                        assertTrue(
                                assemblyStatus.equals("ASSEMBLED") || assemblyStatus.equals("FAIL"),
                                "StorageService should create assembly order with ASSEMBLED or FAIL status"
                        );
                    }
                });

        Awaitility.await()
                .atMost(Duration.ofSeconds(120))
                .pollInterval(Duration.ofSeconds(2))
                .untilAsserted(() -> {
                    try (Connection storageDb = storageDbConnection()) {
                        String resultEventType = selectLatestStorageOutboxEventType(storageDb, orderId);
                        String resultEventStatus = selectLatestStorageOutboxStatus(storageDb, orderId);

                        assertTrue(
                                resultEventType.equals("OrderApproved") || resultEventType.equals("OrderRejected"),
                                "StorageService should create OrderApproved or OrderRejected event"
                        );

                        assertEquals(
                                "PUBLISHED",
                                resultEventStatus,
                                "StorageService result outbox event should be published"
                        );
                    }
                });

        Awaitility.await()
                .atMost(Duration.ofSeconds(120))
                .pollInterval(Duration.ofSeconds(2))
                .untilAsserted(() -> {
                    try (Connection orderDb = orderDbConnection()) {
                        String finalOrderStatus = selectOrderStatus(orderDb, orderId);

                        assertTrue(
                                finalOrderStatus.equals("CAR_READY_FOR_PICKUP") || finalOrderStatus.equals("CANCELLED"),
                                "OrderService should update final order status after storage result"
                        );
                    }
                });
    }

    @Test
    void shouldNotCreateDuplicateAssemblyOrderForSameSourceOrder() throws Exception {
        UUID orderId = UUID.fromString("60000000-0000-0000-0000-000000000002");
        UUID carId = UUID.fromString("30000000-0000-0000-0000-000000000002");
        UUID traceId = UUID.randomUUID();

        UUID firstOutboxEventId = UUID.randomUUID();
        UUID firstSourceEventId = UUID.randomUUID();

        UUID secondOutboxEventId = UUID.randomUUID();
        UUID secondSourceEventId = UUID.randomUUID();

        try (Connection orderDb = orderDbConnection()) {
            insertOrderSentForApprovalOutboxEvent(
                    orderDb,
                    firstOutboxEventId,
                    firstSourceEventId,
                    orderId,
                    carId,
                    traceId
            );

            insertOrderSentForApprovalOutboxEvent(
                    orderDb,
                    secondOutboxEventId,
                    secondSourceEventId,
                    orderId,
                    carId,
                    traceId
            );
        }

        Awaitility.await()
                .atMost(Duration.ofSeconds(120))
                .pollInterval(Duration.ofSeconds(2))
                .untilAsserted(() -> {
                    try (Connection storageDb = storageDbConnection()) {
                        assertEquals(
                                1,
                                countAssemblyOrdersBySourceOrderId(storageDb, orderId),
                                "StorageService should not create duplicate assembly orders for one source order"
                        );
                    }
                });
    }

    private static Connection orderDbConnection() throws Exception {
        String host = environment.getServiceHost("order_db", POSTGRES_PORT);
        Integer port = environment.getServicePort("order_db", POSTGRES_PORT);

        return DriverManager.getConnection(
                "jdbc:postgresql://" + host + ":" + port + "/order_db",
                "postgres",
                "postgres"
        );
    }

    private static Connection storageDbConnection() throws Exception {
        String host = environment.getServiceHost("storage_db", POSTGRES_PORT);
        Integer port = environment.getServicePort("storage_db", POSTGRES_PORT);

        return DriverManager.getConnection(
                "jdbc:postgresql://" + host + ":" + port + "/storage_db",
                "postgres",
                "postgres"
        );
    }

    private static void insertOrderSentForApprovalOutboxEvent(Connection connection,
                                                              UUID outboxEventId,
                                                              UUID sourceEventId,
                                                              UUID orderId,
                                                              UUID carId,
                                                              UUID traceId) throws Exception {
        String payload = """
                {
                  "eventId": "%s",
                  "orderId": "%s",
                  "orderType": "IN_STOCK",
                  "carId": "%s",
                  "carModelId": null,
                  "componentIds": [],
                  "traceId": "%s",
                  "occurredAt": "2026-05-01T22:00:00Z"
                }
                """.formatted(sourceEventId, orderId, carId, traceId);

        try (var statement = connection.prepareStatement("""
                INSERT INTO outbox_events (
                    id,
                    aggregate_id,
                    event_type,
                    payload,
                    status,
                    created_at,
                    published_at
                )
                VALUES (?, ?, 'OrderSentForApproval', ?, 'NEW', NOW(), NULL)
                """)) {
            statement.setObject(1, outboxEventId);
            statement.setObject(2, orderId);
            statement.setString(3, payload);
            statement.executeUpdate();
        }
    }

    private static String selectOutboxStatus(Connection connection, UUID eventId) throws Exception {
        try (var statement = connection.prepareStatement("""
                SELECT status
                FROM outbox_events
                WHERE id = ?
                """)) {
            statement.setObject(1, eventId);

            try (var resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                return resultSet.getString("status");
            }
        }
    }

    private static String selectAssemblyOrderStatus(Connection connection, UUID sourceOrderId) throws Exception {
        try (var statement = connection.prepareStatement("""
                SELECT status
                FROM assembly_orders
                WHERE source_order_id = ?
                """)) {
            statement.setObject(1, sourceOrderId);

            try (var resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                return resultSet.getString("status");
            }
        }
    }

    private static int countAssemblyOrdersBySourceOrderId(Connection connection, UUID sourceOrderId) throws Exception {
        try (var statement = connection.prepareStatement("""
                SELECT COUNT(*)
                FROM assembly_orders
                WHERE source_order_id = ?
                """)) {
            statement.setObject(1, sourceOrderId);

            try (var resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1);
            }
        }
    }

    private static String selectLatestStorageOutboxEventType(Connection connection, UUID aggregateId) throws Exception {
        try (var statement = connection.prepareStatement("""
                SELECT event_type
                FROM outbox_events
                WHERE aggregate_id = ?
                ORDER BY created_at DESC
                LIMIT 1
                """)) {
            statement.setObject(1, aggregateId);

            try (var resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                return resultSet.getString("event_type");
            }
        }
    }

    private static String selectLatestStorageOutboxStatus(Connection connection, UUID aggregateId) throws Exception {
        try (var statement = connection.prepareStatement("""
                SELECT status
                FROM outbox_events
                WHERE aggregate_id = ?
                ORDER BY created_at DESC
                LIMIT 1
                """)) {
            statement.setObject(1, aggregateId);

            try (var resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                return resultSet.getString("status");
            }
        }
    }

    private static String selectOrderStatus(Connection connection, UUID orderId) throws Exception {
        try (var statement = connection.prepareStatement("""
                SELECT status
                FROM orders
                WHERE id = ?
                """)) {
            statement.setObject(1, orderId);

            try (var resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                return resultSet.getString("status");
            }
        }
    }

    private static boolean tableExists(Connection connection, String tableName) throws Exception {
        try (var statement = connection.prepareStatement("""
            SELECT EXISTS (
                SELECT 1
                FROM information_schema.tables
                WHERE table_schema = 'public'
                  AND table_name = ?
            )
            """)) {
            statement.setString(1, tableName);

            try (var resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getBoolean(1);
            }
        }
    }
}