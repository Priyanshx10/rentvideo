
package com.rentvideo.rentvideo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;

import com.rentvideo.rentvideo.entity.Video;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    List<Video> findByAvailableTrue();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM Video v WHERE v.id = :id")
    Optional<Video> findByIdForUpdate(@Param("id") Long id);
}
