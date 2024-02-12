package org.example.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="tbl_productsImages")
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="imagePath", nullable = false)
    private String imagePath;

    @ManyToOne
    @JoinColumn(name="product_id",nullable = false)
    private Product product;
}
