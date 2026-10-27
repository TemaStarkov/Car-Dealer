package org.example.infrastructure.repository;

import org.example.domain.model.order.TestDriveOrder;
import org.example.domain.model.user.Customer;
import org.example.domain.model.user.Manager;
import org.example.domain.model.enums.TestDriveStatus;
import org.example.infrastructure.repository.InMemoryBaseRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class InMemoryTestDriveRepository extends InMemoryBaseRepository<TestDriveOrder, String> {

    @Override
    protected String getId(TestDriveOrder order) {
        return order.getId();
    }

    @Override
    protected void validate(TestDriveOrder order) {
        if (order == null) {
            throw new IllegalArgumentException("Test drive order cannot be null");
        }
    }

    public List<TestDriveOrder> findByCustomer(Customer customer) {

        return find(o -> o.getCustomer().equals(customer));
    }

    public List<TestDriveOrder> findByManager(Manager manager) {

        return find(o -> o.getManager().equals(manager));
    }

    public List<TestDriveOrder> findByStatus(TestDriveStatus status) {

        return find(o -> o.getStatus() == status);
    }

    public List<TestDriveOrder> findByDate(LocalDate date) {

        return find(o -> o.getRequestedTime().toLocalDate().equals(date));
    }

    public List<TestDriveOrder> findByDateRange(LocalDateTime from, LocalDateTime to) {

        return find(o ->
                o.getRequestedTime().isAfter(from) &&
                        o.getRequestedTime().isBefore(to)
        );
    }

    public List<TestDriveOrder> findUpcoming() {

        return storage.values().stream()
                .filter(o -> o.getStatus() == TestDriveStatus.CONFIRMED)
                .filter(o -> o.getRequestedTime().isAfter(LocalDateTime.now()))
                .sorted(Comparator.comparing(TestDriveOrder::getRequestedTime))
                .toList();
    }
}