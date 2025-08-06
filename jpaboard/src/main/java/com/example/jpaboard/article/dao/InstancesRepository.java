
package com.example.jpaboard.article.dao;

import com.example.jpaboard.article.domain.Comment;
import com.example.jpaboard.article.domain.Instances;
import com.example.jpaboard.article.domain.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InstancesRepository extends JpaRepository<Instances, Long> {
        @Query("SELECT i FROM Instances i WHERE i.street_number = :streetNumber") 
    List<Instances> findByStreetNumber(@Param("streetNumber") String streetNumber);
    
    @Query("SELECT i FROM Instances i WHERE i.virtual_address = :virtualAddress") 
    List<Instances> findByVirtualAddress(@Param("virtualAddress") String virtualAddress);
}
