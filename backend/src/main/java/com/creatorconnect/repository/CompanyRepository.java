package com.creatorconnect.repository;

import com.creatorconnect.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByUserId(Long userId);
    Page<Company> findByVerified(boolean verified, Pageable pageable);
    Page<Company> findByCompanyNameContainingIgnoreCase(String name, Pageable pageable);
}
