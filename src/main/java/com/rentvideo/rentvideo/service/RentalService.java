
package com.rentvideo.rentvideo.service;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.rentvideo.rentvideo.dto.RentalResponse;
import com.rentvideo.rentvideo.entity.Rental;
import com.rentvideo.rentvideo.entity.User;
import com.rentvideo.rentvideo.entity.Video;
import com.rentvideo.rentvideo.repository.RentalRepository;
import com.rentvideo.rentvideo.repository.UserRepository;
import com.rentvideo.rentvideo.repository.VideoRepository;

@Service
public class RentalService {

    private static final int MAX_ACTIVE_RENTALS = 2;

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;

    public RentalService(
            RentalRepository rentalRepository,
            UserRepository userRepository,
            VideoRepository videoRepository
    ) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.videoRepository = videoRepository;
    }

    @Transactional
    public RentalResponse rentVideo(Long videoId, String email) {

        // Find the customer using the authenticated email.
        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        // Lock the customer row so concurrent rental requests
        // cannot exceed the maximum rental limit.
        User user = userRepository.findByIdForUpdate(existingUser.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        long activeRentals =
                rentalRepository.countByUserIdAndReturnedAtIsNull(user.getId());

        if (activeRentals >= MAX_ACTIVE_RENTALS) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A customer can have a maximum of 2 active rentals");
        }

        // Lock the video row to prevent simultaneous rentals.
        Video video = videoRepository.findByIdForUpdate(videoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Video not found"));

        if (!Boolean.TRUE.equals(video.getAvailable())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Video is currently unavailable");
        }

        video.setAvailable(false);
        videoRepository.save(video);

        Rental rental = new Rental(user, video);
        Rental savedRental = rentalRepository.save(rental);

        return toResponse(savedRental);
    }

    @Transactional
    public RentalResponse returnVideo(Long videoId, String email) {

        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        // Lock the customer row first.
        User user = userRepository.findByIdForUpdate(existingUser.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        // Lock the video row.
        Video video = videoRepository.findByIdForUpdate(videoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Video not found"));

        // Find an active rental belonging to this customer.
        Rental rental = rentalRepository
                .findByUserIdAndVideoIdAndReturnedAtIsNull(
                        user.getId(), videoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "No active rental found for this customer and video"));

        rental.setReturnedAt(LocalDateTime.now());
        video.setAvailable(true);

        rentalRepository.save(rental);
        videoRepository.save(video);

        return toResponse(rental);
    }

    private RentalResponse toResponse(Rental rental) {

        return new RentalResponse(
                rental.getId(),
                rental.getVideo().getId(),
                rental.getVideo().getTitle(),
                rental.getRentedAt(),
                rental.getReturnedAt(),
                rental.getReturnedAt() == null
        );
    }
}
