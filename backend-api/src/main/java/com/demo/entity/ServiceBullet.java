package com.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "service_bullets")
public class ServiceBullet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private CareService service;

    @Column(name = "bullet_text", nullable = false, columnDefinition = "TEXT")
    private String bulletText;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    public ServiceBullet() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public CareService getService() { return service; }
    public void setService(CareService service) { this.service = service; }

    public String getBulletText() { return bulletText; }
    public void setBulletText(String bulletText) { this.bulletText = bulletText; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
}
