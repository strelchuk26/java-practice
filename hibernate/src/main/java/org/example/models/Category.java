package org.example.models;

import java.util.*;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name="tbl_categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;
    @Column(length = 200, nullable = false)
    private String name;
    @Column(length = 200)
    private String image;
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Product> products;
}