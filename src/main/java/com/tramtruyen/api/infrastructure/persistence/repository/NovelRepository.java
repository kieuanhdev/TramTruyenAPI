package com.tramtruyen.api.infrastructure.persistence.repository;

import com.tramtruyen.api.infrastructure.persistence.entity.NovelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NovelRepository extends JpaRepository<NovelEntity, UUID> {
}