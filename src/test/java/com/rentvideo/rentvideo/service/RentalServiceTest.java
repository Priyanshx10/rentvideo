
package com.rentvideo.rentvideo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import com.rentvideo.rentvideo.entity.Rental;
import com.rentvideo.rentvideo.entity.Role;
import com.rentvideo.rentvideo.entity.User;
import com.rentvideo.rentvideo.entity.Video;
import com.rentvideo.rentvideo.repository.RentalRepository;
import com.rentvideo.rentvideo.repository.UserRepository;
import com.rentvideo.rentvideo.repository.VideoRepository;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VideoRepository videoRepository;

    @InjectMocks
    private RentalService rentalService;

    private User customer;
    private Video video;

    private static final String CUSTOMER_EMAIL = "customer@test.com";
    private static final Long CUSTOMER_ID = 1L;
    private static final Long VIDEO_ID = 10L;

    @BeforeEach
    void setUp() {
        customer = new User(
                CUSTOMER_EMAIL,
                "encodedPassword",
                "Test",
                "Customer",
                Role.CUSTOMER
        );

        video = new Video(
                "Inception",
                "Christopher Nolan",
                "Sci-Fi"
        );

        // Assign IDs to entities for unit testing.
        ReflectionTestUtils.setField(customer, "id", CUSTOMER_ID);
        ReflectionTestUtils.setField(video, "id", VIDEO_ID);
    }

    @Test
    void rentVideo_shouldSuccessfullyRentAvailableVideo() {

        when(userRepository.findByEmail(CUSTOMER_EMAIL))
                .thenReturn(Optional.of(customer));

        when(userRepository.findByIdForUpdate(CUSTOMER_ID))
                .thenReturn(Optional.of(customer));

        when(rentalRepository.countByUserIdAndReturnedAtIsNull(CUSTOMER_ID))
                .thenReturn(0L);

        when(videoRepository.findByIdForUpdate(VIDEO_ID))
                .thenReturn(Optional.of(video));

        when(rentalRepository.save(any(Rental.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = rentalService.rentVideo(VIDEO_ID, CUSTOMER_EMAIL);

        assertNotNull(response);
        assertEquals(false, video.getAvailable());

        verify(videoRepository).save(video);
        verify(rentalRepository).save(any(Rental.class));
    }

    @Test
    void rentVideo_shouldRejectWhenCustomerAlreadyHasTwoActiveRentals() {

        when(userRepository.findByEmail(CUSTOMER_EMAIL))
                .thenReturn(Optional.of(customer));

        when(userRepository.findByIdForUpdate(CUSTOMER_ID))
                .thenReturn(Optional.of(customer));

        when(rentalRepository.countByUserIdAndReturnedAtIsNull(CUSTOMER_ID))
                .thenReturn(2L);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> rentalService.rentVideo(VIDEO_ID, CUSTOMER_EMAIL)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());

        verify(videoRepository, never()).findByIdForUpdate(VIDEO_ID);
        verify(videoRepository, never()).save(any(Video.class));
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    void rentVideo_shouldRejectWhenVideoIsUnavailable() {

        video.setAvailable(false);

        when(userRepository.findByEmail(CUSTOMER_EMAIL))
                .thenReturn(Optional.of(customer));

        when(userRepository.findByIdForUpdate(CUSTOMER_ID))
                .thenReturn(Optional.of(customer));

        when(rentalRepository.countByUserIdAndReturnedAtIsNull(CUSTOMER_ID))
                .thenReturn(0L);

        when(videoRepository.findByIdForUpdate(VIDEO_ID))
                .thenReturn(Optional.of(video));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> rentalService.rentVideo(VIDEO_ID, CUSTOMER_EMAIL)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());

        verify(videoRepository, never()).save(any(Video.class));
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    void returnVideo_shouldSuccessfullyReturnVideo() {

        Rental rental = new Rental(customer, video);

        when(userRepository.findByEmail(CUSTOMER_EMAIL))
                .thenReturn(Optional.of(customer));

        when(userRepository.findByIdForUpdate(CUSTOMER_ID))
                .thenReturn(Optional.of(customer));

        when(videoRepository.findByIdForUpdate(VIDEO_ID))
                .thenReturn(Optional.of(video));

        when(rentalRepository
                .findByUserIdAndVideoIdAndReturnedAtIsNull(
                        CUSTOMER_ID, VIDEO_ID))
                .thenReturn(Optional.of(rental));

        when(rentalRepository.save(any(Rental.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = rentalService.returnVideo(VIDEO_ID, CUSTOMER_EMAIL);

        assertNotNull(response);
        assertNotNull(rental.getReturnedAt());
        assertEquals(true, video.getAvailable());

        verify(rentalRepository).save(rental);
        verify(videoRepository).save(video);
    }

    @Test
    void returnVideo_shouldRejectWhenRentalBelongsToAnotherCustomer() {

        when(userRepository.findByEmail(CUSTOMER_EMAIL))
                .thenReturn(Optional.of(customer));

        when(userRepository.findByIdForUpdate(CUSTOMER_ID))
                .thenReturn(Optional.of(customer));

        when(videoRepository.findByIdForUpdate(VIDEO_ID))
                .thenReturn(Optional.of(video));

        // No active rental exists for this customer and video.
        when(rentalRepository
                .findByUserIdAndVideoIdAndReturnedAtIsNull(
                        CUSTOMER_ID, VIDEO_ID))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> rentalService.returnVideo(VIDEO_ID, CUSTOMER_EMAIL)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());

        // Video must remain unavailable because the return was rejected.
        assertNull(video.getAvailable() ? null : video.getAvailable());

        verify(rentalRepository, never()).save(any(Rental.class));
        verify(videoRepository, never()).save(any(Video.class));
    }

    @Test
    void rentVideo_shouldReturnNotFoundWhenVideoDoesNotExist() {

        when(userRepository.findByEmail(CUSTOMER_EMAIL))
                .thenReturn(Optional.of(customer));

        when(userRepository.findByIdForUpdate(CUSTOMER_ID))
                .thenReturn(Optional.of(customer));

        when(rentalRepository.countByUserIdAndReturnedAtIsNull(CUSTOMER_ID))
                .thenReturn(0L);

        when(videoRepository.findByIdForUpdate(VIDEO_ID))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> rentalService.rentVideo(VIDEO_ID, CUSTOMER_EMAIL)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(rentalRepository, never()).save(any(Rental.class));
    }
}
