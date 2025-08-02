package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
