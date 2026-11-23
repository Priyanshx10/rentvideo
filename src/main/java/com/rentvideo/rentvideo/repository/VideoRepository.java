package com.rentvideo.rentvideo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rentvideo.rentvideo.entity.Video;

public interface VideoRepository extends JpaRepository<Video, Long> {

    List<Video> findByAvailableTrue();
}
