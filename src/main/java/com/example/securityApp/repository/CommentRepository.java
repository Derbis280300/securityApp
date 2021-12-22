package com.example.securityApp.repository;

import com.example.securityApp.entities.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface CommentRepository extends JpaRepository<Comments,Long> {
    List<Comments>findAllByItems_Id(Long id);

}
