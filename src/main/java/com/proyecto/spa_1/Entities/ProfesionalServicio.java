package com.proyecto.spa_1.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;

@JsonPropertyOrder({"id", "profesional", "servicio"})
@Entity
@Table(name = "profesional_servicios", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"profesional_id", "servicio_id"}))
public class ProfesionalServicio {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "profesional_id", nullable = false)
    // Evitamos que al cargar la relación se traigan las listas pesadas del Profesional
    @JsonIgnoreProperties({"horarios", "citas", "hibernateLazyInitializer", "handler"})
    private Profesional profesional;

    @ManyToOne
    @JoinColumn(name = "servicio_id", nullable = false)
    // Evitamos que al cargar el servicio intente cargar las citas asociadas a ese servicio
    @JsonIgnoreProperties({"citas", "hibernateLazyInitializer", "handler"})
    private Servicio servicio;

    public ProfesionalServicio() {
    }

    public ProfesionalServicio(Long id, Profesional profesional, Servicio servicio) {
        this.id = id;
        this.profesional = profesional;
        this.servicio = servicio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Profesional getProfesional() {
        return profesional;
    }

    public void setProfesional(Profesional profesional) {
        this.profesional = profesional;
    }

    public Servicio getServicio() {
        return servicio;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }

    
}