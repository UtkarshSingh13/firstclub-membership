package com.firstclub.membership.repository;

import com.firstclub.membership.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    long countByUserIdAndOrderDateAfter(Long userId, LocalDateTime afterDate);

    @Query("SELECT COALESCE(SUM(o.amount), 0) FROM Order o WHERE o.user.id = :userId AND o.orderDate > :afterDate")
    BigDecimal sumAmountByUserIdAfter(@Param("userId") Long userId, @Param("afterDate") LocalDateTime afterDate);

    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);
}
