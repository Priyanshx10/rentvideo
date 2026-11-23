
package com.rentvideo.rentvideo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rentvideo.rentvideo.dto.VideoRequest;
import com.rentvideo.rentvideo.dto.VideoResponse;
import com.rentvideo.rentvideo.service.VideoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @PostMapping
    public ResponseEntity<VideoResponse> createVideo(
            @Valid @RequestBody VideoRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(videoService.createVideo(request));
    }

    @GetMapping
    public ResponseEntity<List<VideoResponse>> getAllVideos() {

        return ResponseEntity.ok(
                videoService.getAllAvailableVideos()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoResponse> getVideoById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                videoService.getVideoById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<VideoResponse> updateVideo(
            @PathVariable Long id,
            @Valid @RequestBody VideoRequest request) {

        return ResponseEntity.ok(
                videoService.updateVideo(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVideo(
            @PathVariable Long id) {

        videoService.deleteVideo(id);

        return ResponseEntity.noContent().build();
    }
}
