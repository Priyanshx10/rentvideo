
package com.rentvideo.rentvideo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rentvideo.rentvideo.entity.Rental;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    long countByUserIdAndReturnedAtIsNull(Long userId);

    Optional<Rental> findByUserIdAndVideoIdAndReturnedAtIsNull(
            Long userId,
            Long videoId
    );
}
