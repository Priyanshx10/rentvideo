
package com.rentvideo.rentvideo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rentvideo.rentvideo.dto.RentalResponse;
import com.rentvideo.rentvideo.service.RentalService;

@RestController
@RequestMapping("/api")
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @PostMapping("/videos/{videoId}/rent")
    public ResponseEntity<RentalResponse> rentVideo(
            @PathVariable Long videoId,
            Authentication authentication
    ) {
        RentalResponse response = rentalService.rentVideo(
                videoId,
                authentication.getName()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/books/{videoId}/return")
    public ResponseEntity<RentalResponse> returnVideo(
            @PathVariable Long videoId,
            Authentication authentication
    ) {
        RentalResponse response = rentalService.returnVideo(
                videoId,
                authentication.getName()
        );

        return ResponseEntity.ok(response);
    }
}
