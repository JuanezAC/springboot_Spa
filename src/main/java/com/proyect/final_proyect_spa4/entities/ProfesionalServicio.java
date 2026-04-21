package com.proyect.final_proyect_spa4.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;

@JsonPropertyOrder({ "id", "profesional", "servicio" })
@Entity
@Table(name = "profesional_servicios", uniqueConstraints = @UniqueConstraint(columnNames = { "profesional_id",
        "servicio_id" }))
public class ProfesionalServicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "profesional_id", nullable = false)
    @JsonBackReference(value = "profesional-proservicios")
    private Profesional profesional;

    @ManyToOne
    @JoinColumn(name = "servicio_id", nullable = false)
    @JsonBackReference(value = "servicios-proservicios")
    private Servicio servicio;

    public ProfesionalServicio() {
    }

    public ProfesionalServicio(Long id, Profesional profesional, Servicio servicio) {
        this.id = id;
        this.profesional = profesional;
        this.servicio = servicio;
    }

    // Getters y Setters
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
