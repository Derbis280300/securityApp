package com.example.securityApp.repository;

import com.example.securityApp.entities.Brands;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.Table;
import javax.transaction.Transactional;

@Repository
@Transactional
public interface BrandsRepsoitory extends JpaRepository<Brands,Long> {
}
