package com.example.securityApp.repository;

import com.example.securityApp.entities.Items;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface ItemRepository extends JpaRepository<Items,Long> {

 List<Items>findAllByNameContainsIgnoreCase(String key);
 List<Items>findAllByNameContains(String key);
 List<Items>findAllByBrands_Name(String name);
}
