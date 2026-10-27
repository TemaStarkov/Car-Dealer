package org.example;

import org.example.infrastructure.persistence.repository.CarEntityRepository;
import org.example.infrastructure.persistence.repository.CarModelEntityRepository;
import org.example.infrastructure.persistence.repository.ComponentEntityRepository;
import org.example.infrastructure.persistence.repository.PartEntityRepository;
import org.example.infrastructure.persistence.repository.UserEntityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class LiquibaseAndSeedIT extends AbstractPostgresIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserEntityRepository userRepository;

    @Autowired
    private CarModelEntityRepository carModelRepository;

    @Autowired
    private CarEntityRepository carRepository;

    @Autowired
    private ComponentEntityRepository componentRepository;

    @Autowired
    private PartEntityRepository partRepository;

    @Test
    void liquibaseShouldCreateCoreTables() {

        Integer usersColumns = jdbcTemplate.queryForObject(
                "select count(*) from information_schema.columns where table_name = 'users'",
                Integer.class
        );

        Integer carsColumns = jdbcTemplate.queryForObject(
                "select count(*) from information_schema.columns where table_name = 'cars'",
                Integer.class
        );

        Integer ordersColumns = jdbcTemplate.queryForObject(
                "select count(*) from information_schema.columns where table_name = 'orders'",
                Integer.class
        );

        assertNotNull(usersColumns);
        assertNotNull(carsColumns);
        assertNotNull(ordersColumns);
        assertTrue(usersColumns > 0);
        assertTrue(carsColumns > 0);
        assertTrue(ordersColumns > 0);
    }

    @Test
    void dataInitializerShouldSeedReferenceData() {

        assertEquals(6, userRepository.findAllByRemovedFalse().size());
        assertEquals(2, carModelRepository.findAllByRemovedFalse().size());
        assertEquals(3, carRepository.findAllByRemovedFalse().size());
        assertEquals(7, componentRepository.findAll().size());
        assertEquals(3, partRepository.findAllByRemovedFalse().size());
        assertFalse(carRepository.findAllByRemovedFalse().isEmpty());
    }
}
