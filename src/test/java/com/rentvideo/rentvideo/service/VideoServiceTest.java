
package com.rentvideo.rentvideo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.rentvideo.rentvideo.dto.VideoRequest;
import com.rentvideo.rentvideo.dto.VideoResponse;
import com.rentvideo.rentvideo.entity.Video;
import com.rentvideo.rentvideo.repository.VideoRepository;

@ExtendWith(MockitoExtension.class)
class VideoServiceTest {

    @Mock
    private VideoRepository videoRepository;

    @InjectMocks
    private VideoService videoService;

    private Video video;
    private VideoRequest request;

    private static final Long VIDEO_ID = 10L;

    @BeforeEach
    void setUp() {

        video = new Video(
                "Inception",
                "Christopher Nolan",
                "Sci-Fi"
        );

        ReflectionTestUtils.setField(video, "id", VIDEO_ID);

        request = new VideoRequest();
        request.setTitle("Inception");
        request.setDirector("Christopher Nolan");
        request.setGenre("Sci-Fi");
    }

    @Test
    void createVideo_shouldSaveAndReturnVideo() {

        when(videoRepository.save(org.mockito.ArgumentMatchers.any(Video.class)))
                .thenAnswer(invocation -> {
                    Video savedVideo = invocation.getArgument(0);
                    ReflectionTestUtils.setField(savedVideo, "id", VIDEO_ID);
                    return savedVideo;
                });

        VideoResponse response = videoService.createVideo(request);

        assertNotNull(response);
        assertEquals(VIDEO_ID, response.getId());
        assertEquals("Inception", response.getTitle());
        assertEquals("Christopher Nolan", response.getDirector());
        assertEquals("Sci-Fi", response.getGenre());
        assertTrue(response.getAvailable());

        verify(videoRepository).save(org.mockito.ArgumentMatchers.any(Video.class));
    }

    @Test
    void getAllAvailableVideos_shouldReturnAvailableVideos() {

        when(videoRepository.findByAvailableTrue())
                .thenReturn(List.of(video));

        List<VideoResponse> response =
                videoService.getAllAvailableVideos();

        assertEquals(1, response.size());
        assertEquals("Inception", response.get(0).getTitle());
        assertEquals(VIDEO_ID, response.get(0).getId());
        assertTrue(response.get(0).getAvailable());

        verify(videoRepository).findByAvailableTrue();
    }

    @Test
    void getAllAvailableVideos_shouldReturnEmptyListWhenNoVideosAvailable() {

        when(videoRepository.findByAvailableTrue())
                .thenReturn(List.of());

        List<VideoResponse> response =
                videoService.getAllAvailableVideos();

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void getVideoById_shouldReturnVideoWhenFound() {

        when(videoRepository.findById(VIDEO_ID))
                .thenReturn(Optional.of(video));

        VideoResponse response =
                videoService.getVideoById(VIDEO_ID);

        assertNotNull(response);
        assertEquals(VIDEO_ID, response.getId());
        assertEquals("Inception", response.getTitle());
        assertEquals("Christopher Nolan", response.getDirector());
    }

    @Test
    void getVideoById_shouldThrowExceptionWhenVideoDoesNotExist() {

        when(videoRepository.findById(VIDEO_ID))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> videoService.getVideoById(VIDEO_ID)
        );

        assertEquals("Video not found", exception.getMessage());
    }

    @Test
    void updateVideo_shouldUpdateAndReturnVideo() {

        VideoRequest updateRequest = new VideoRequest();
        updateRequest.setTitle("Interstellar");
        updateRequest.setDirector("Christopher Nolan");
        updateRequest.setGenre("Adventure");

        when(videoRepository.findById(VIDEO_ID))
                .thenReturn(Optional.of(video));

        when(videoRepository.save(video))
                .thenReturn(video);

        VideoResponse response =
                videoService.updateVideo(VIDEO_ID, updateRequest);

        assertEquals("Interstellar", response.getTitle());
        assertEquals("Christopher Nolan", response.getDirector());
        assertEquals("Adventure", response.getGenre());
        assertEquals(VIDEO_ID, response.getId());

        verify(videoRepository).save(video);
    }

    @Test
    void deleteVideo_shouldDeleteVideoWhenFound() {

        when(videoRepository.findById(VIDEO_ID))
                .thenReturn(Optional.of(video));

        videoService.deleteVideo(VIDEO_ID);

        verify(videoRepository).delete(video);
    }

    @Test
    void deleteVideo_shouldThrowExceptionWhenVideoDoesNotExist() {

        when(videoRepository.findById(VIDEO_ID))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> videoService.deleteVideo(VIDEO_ID)
        );

        assertEquals("Video not found", exception.getMessage());

        verify(videoRepository, never()).delete(video);
    }

    @Test
    void updateVideo_shouldThrowExceptionWhenVideoDoesNotExist() {

        when(videoRepository.findById(VIDEO_ID))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> videoService.updateVideo(VIDEO_ID, request)
        );

        verify(videoRepository, never()).save(video);
    }
}
