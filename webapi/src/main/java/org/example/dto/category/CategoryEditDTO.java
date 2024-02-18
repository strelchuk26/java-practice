package org.example.dto.category;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CategoryEditDTO {
    private String name;
    private MultipartFile image;
    private String description;
    private String dateCreated;
}