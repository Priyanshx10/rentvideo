
package com.rentvideo.rentvideo.dto;

public class VideoResponse {

    private Long id;
    private String title;
    private String director;
    private String genre;
    private Boolean available;

    public VideoResponse(Long id, String title, String director,
                         String genre, Boolean available) {
        this.id = id;
        this.title = title;
        this.director = director;
        this.genre = genre;
        this.available = available;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDirector() {
        return director;
    }

    public String getGenre() {
        return genre;
    }

    public Boolean getAvailable() {
        return available;
    }
}
