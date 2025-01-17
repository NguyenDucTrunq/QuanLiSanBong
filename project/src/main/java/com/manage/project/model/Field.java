package com.manage.project.model;

import jakarta.persistence.*;

@Entity
@Table(name = "fields")
public class Field {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "price_per_hour")
    private Integer pricePerHour;  


    @Column
    private String type;  // 5 người, 7 người, 11 người

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private Boolean status;  // Trạng thái: Hoạt động / Không hoạt động

    // Constructors, Getters, and Setters
    public Field() {}

    public Field(String name, Integer pricePerHour, String type, String description, Boolean status) {
        this.name = name;
        this.pricePerHour = pricePerHour;
        this.type = type;
        this.description = description;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(Integer pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
