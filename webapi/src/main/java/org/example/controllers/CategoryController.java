package org.example.controllers;

import lombok.AllArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.example.dto.category.CategoryCreateDTO;
import org.example.dto.category.CategoryEditDTO;
import org.example.dto.category.CategoryItemDTO;
import org.example.entities.CategoryEntity;
import org.example.mapper.CategoryMapper;
import org.example.repositories.CategoryRepository;
import org.example.storage.FileSaveFormat;
import org.example.storage.StorageService;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    @GetMapping("/{id}")
    public ResponseEntity<CategoryItemDTO> getById(@PathVariable Integer id) {
        Optional<CategoryEntity> categoryOptional = categoryRepository.findById(id);

        if (categoryOptional.isPresent()) {
            CategoryItemDTO categoryItemDTO = categoryMapper.categoryItemDTO(categoryOptional.get());
            return new ResponseEntity<>(categoryItemDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
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

    @PutMapping(value="edit/{categoryId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CategoryItemDTO> edit(@ModelAttribute CategoryEditDTO model) {
        var old = categoryRepository.findById(model.getId()).orElse(null);
        if (old == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        var entity = categoryMapper.categoryEditDto(model);
        if(model.getFile()==null) {
            entity.setImage(old.getImage());
        }
        else {
            try {
                storageService.deleteImage(old.getImage());
                String fileName = storageService.SaveImage(model.getFile(), FileSaveFormat.WEBP);
                entity.setImage(fileName);
            }
            catch (Exception exception) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        entity.setCreationTime(old.getCreationTime());
        categoryRepository.save(entity);
        var result = categoryMapper.categoryItemDTO(entity);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("delete/{categoryId}")
    public ResponseEntity<Void> delete(@PathVariable Integer categoryId) {
        try {
            Optional<CategoryEntity> categoryOptional = categoryRepository.findById(categoryId);

            if (!categoryOptional.isPresent()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            CategoryEntity category = categoryOptional.get();

            String photoFileName = category.getImage();

            categoryRepository.deleteById(categoryId);

            String uploadingPath = System.getProperty("user.dir") + "\\uploading";

            String [] sizes = {"32", "150", "300", "600", "1200"};

            for(var size : sizes) {
                String photoWithSize = size + "_" + photoFileName;
                Path photoPath = Paths.get(uploadingPath, photoWithSize);
                Files.deleteIfExists(photoPath);
            }

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<CategoryItemDTO>> searchByName(@RequestParam(required = false) String name,
                                                              Pageable pageable) {
        Page<CategoryEntity> categories = categoryRepository.findByNameContainingIgnoreCase(name, pageable);
        Page<CategoryItemDTO> result = categories.map(categoryMapper::categoryItemDTO);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
