
package com.rentvideo.rentvideo.dto;

import java.time.LocalDateTime;

public class RentalResponse {

    private Long rentalId;
    private Long videoId;
    private String videoTitle;
    private LocalDateTime rentedAt;
    private LocalDateTime returnedAt;
    private boolean active;

    public RentalResponse(
            Long rentalId,
            Long videoId,
            String videoTitle,
            LocalDateTime rentedAt,
            LocalDateTime returnedAt,
            boolean active
    ) {
        this.rentalId = rentalId;
        this.videoId = videoId;
        this.videoTitle = videoTitle;
        this.rentedAt = rentedAt;
        this.returnedAt = returnedAt;
        this.active = active;
    }

    public Long getRentalId() {
        return rentalId;
    }

    public Long getVideoId() {
        return videoId;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public LocalDateTime getRentedAt() {
        return rentedAt;
    }

    public LocalDateTime getReturnedAt() {
        return returnedAt;
    }

    public boolean isActive() {
        return active;
    }
}
