
package com.rentvideo.rentvideo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.rentvideo.rentvideo.dto.VideoRequest;
import com.rentvideo.rentvideo.dto.VideoResponse;
import com.rentvideo.rentvideo.entity.Video;
import com.rentvideo.rentvideo.repository.VideoRepository;

@Service
public class VideoService {

    private final VideoRepository videoRepository;

    public VideoService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    // Create a new video
    public VideoResponse createVideo(VideoRequest request) {

        Video video = new Video(
                request.getTitle(),
                request.getDirector(),
                request.getGenre()
        );

        Video savedVideo = videoRepository.save(video);

        return convertToResponse(savedVideo);
    }

    // Get all available videos
    public List<VideoResponse> getAllAvailableVideos() {

        return videoRepository.findByAvailableTrue()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // Get a video by ID
    public VideoResponse getVideoById(Long id) {

        Video video = videoRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Video not found"));

        return convertToResponse(video);
    }

    // Update an existing video
    public VideoResponse updateVideo(Long id, VideoRequest request) {

        Video video = videoRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Video not found"));

        video.setTitle(request.getTitle());
        video.setDirector(request.getDirector());
        video.setGenre(request.getGenre());

        Video updatedVideo = videoRepository.save(video);

        return convertToResponse(updatedVideo);
    }

    // Delete a video
    public void deleteVideo(Long id) {

        Video video = videoRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Video not found"));

        videoRepository.delete(video);
    }

    // Convert Entity into DTO
    private VideoResponse convertToResponse(Video video) {

        return new VideoResponse(
                video.getId(),
                video.getTitle(),
                video.getDirector(),
                video.getGenre(),
                video.getAvailable()
        );
    }
}
