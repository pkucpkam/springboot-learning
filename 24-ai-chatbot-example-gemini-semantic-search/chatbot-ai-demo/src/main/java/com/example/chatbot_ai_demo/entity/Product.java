package com.example.chatbot_ai_demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    private BigDecimal price;

    @ElementCollection
    private List<Double> embedding;


    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }

    public String getDescription() { return description; }

}
