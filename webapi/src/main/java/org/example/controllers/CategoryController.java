package org.example.controllers;

import lombok.AllArgsConstructor;
import org.example.dto.category.CategoryCreateDTO;
import org.example.dto.category.CategoryEditDTO;
import org.example.dto.category.CategoryItemDTO;
import org.example.entities.CategoryEntity;
import org.example.mapper.CategoryMapper;
import org.example.repositories.CategoryRepository;
import org.example.storage.FileSaveFormat;
import org.example.storage.StorageService;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/categories")
public class CategoryController {
    private final CategoryRepository categoryRepository;
    private final StorageService storageService;
    private final CategoryMapper categoryMapper;

    @GetMapping
    public ResponseEntity<List<CategoryItemDTO>> index() {
        List<CategoryItemDTO> list = categoryMapper.categoryListItemDTO(categoryRepository.findAll());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping(value="create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CategoryEntity> create(@ModelAttribute CategoryCreateDTO dto) {
        try {
            CategoryEntity category = new CategoryEntity();
            category.setName(dto.getName());
            String imageName = storageService.SaveImage(dto.getImage(), FileSaveFormat.WEBP);
            category.setImage(imageName);
            category.setDescription(dto.getDescription());
            category.setCreationTime(LocalDateTime.now());
            categoryRepository.save(category);
            return new ResponseEntity<>(category, HttpStatus.OK);
        } catch (Exception ex) {
            return  new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "edit/{categoryId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CategoryEntity> edit(@PathVariable Integer categoryId, @ModelAttribute CategoryEditDTO dto) {
        try {
            CategoryEntity existingCategory = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

            existingCategory.setName(dto.getName());
            if (dto.getImage() != null) {
                String imageName = storageService.SaveImage(dto.getImage(), FileSaveFormat.WEBP);
                existingCategory.setImage(imageName);
            }
            existingCategory.setDescription(dto.getDescription());
            existingCategory.setCreationTime(LocalDateTime.now());

            categoryRepository.save(existingCategory);

            return new ResponseEntity<>(existingCategory, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("delete/{categoryId}")
    public ResponseEntity<Void> delete(@PathVariable Integer categoryId) {
        try {
            if (!categoryRepository.existsById(categoryId)) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            categoryRepository.deleteById(categoryId);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
