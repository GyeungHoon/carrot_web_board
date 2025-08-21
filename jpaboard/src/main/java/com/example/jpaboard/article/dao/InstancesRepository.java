
package com.example.jpaboard.article.dao;

import com.example.jpaboard.article.domain.Comment;
import com.example.jpaboard.article.domain.Instances;
import com.example.jpaboard.article.domain.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InstancesRepository extends JpaRepository<Instances, Long> {
        @Query("SELECT i FROM Instances i WHERE i.street_number = :streetNumber") 
    List<Instances> findByStreetNumber(@Param("streetNumber") String streetNumber);
    
    @Query("SELECT i FROM Instances i WHERE i.virtual_address = :virtualAddress") 
    List<Instances> findByVirtualAddress(@Param("virtualAddress") String virtualAddress);
    
    // 필터링된 instances 조회 (현재 사용자 또는 미할당된 것만)
    @Query("SELECT i FROM Instances i WHERE " +
           "(i.conferment = :userId OR i.conferment IS NULL OR i.conferment = '') AND " +
           "i.status = 'available' AND " +
           "(i.street_number LIKE %:streetNumber% OR i.virtual_address LIKE %:virtualAddress%)")
    List<Instances> findFilteredInstances(
        @Param("userId") String userId,
        @Param("streetNumber") String streetNumber,
        @Param("virtualAddress") String virtualAddress
    );
    
    // instance_name으로 Instances 조회
    @Query("SELECT i FROM Instances i WHERE i.instance_name = :instanceName")
    Optional<Instances> findByInstanceName(@Param("instanceName") String instanceName);
}
