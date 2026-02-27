package com.tramtruyen.api.controller;

import com.tramtruyen.api.dto.response.CategoryResponse;
import com.tramtruyen.api.model.CategoryEntity;
import com.tramtruyen.api.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryEntity> categories = categoryRepository.findAll();

        List<CategoryResponse> response = categories.stream()
                .map(category -> new CategoryResponse(
                        category.getId(),
                        category.getName(),
                        category.getSlug()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }
}

