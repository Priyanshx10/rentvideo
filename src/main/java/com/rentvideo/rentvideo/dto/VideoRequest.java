
package com.rentvideo.rentvideo.dto;

import jakarta.validation.constraints.NotBlank;

public class VideoRequest {

    @NotBlank(message = "Video title is required")
    private String title;

    @NotBlank(message = "Director name is required")
    private String director;

    @NotBlank(message = "Genre is required")
    private String genre;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}
