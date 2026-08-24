package com.creatorconnect.repository;

import com.creatorconnect.entity.Payment;
import com.creatorconnect.entity.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Page<Payment> findByCreatorId(Long creatorId, Pageable pageable);
    Page<Payment> findByCompanyId(Long companyId, Pageable pageable);
    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.creator.id = :creatorId AND p.status = 'PAID'")
    BigDecimal sumPaidByCreator(@Param("creatorId") Long creatorId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.company.id = :companyId AND p.status = 'PAID'")
    BigDecimal sumPaidByCompany(@Param("companyId") Long companyId);
}
