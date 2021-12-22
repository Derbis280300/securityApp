package com.example.securityApp.repository;

import com.example.securityApp.entities.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface CategoriesRepsoitory extends JpaRepository<Categories,Long> {

}
