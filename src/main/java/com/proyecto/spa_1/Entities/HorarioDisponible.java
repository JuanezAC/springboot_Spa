package com.proyecto.spa_1.Entities;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
@Table(name = "horarios_disponibles")
public class HorarioDisponible {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime hora;

    @Column(nullable = false)
    private Boolean disponible = true;

    @ManyToOne
    @JoinColumn(name = "profesional_id", nullable = false)
    @JsonIgnoreProperties({"horarios", "citas", "hibernateLazyInitializer", "handler"})
    private Profesional profesional;

    @OneToOne(mappedBy = "horario", cascade = CascadeType.REMOVE)
    @JsonIgnore // Evita serializar la cita al hacer GET /api/horarios
    private Cita cita;

    public HorarioDisponible() {

    }

    public HorarioDisponible(Long id, LocalDate fecha, LocalTime hora, Boolean disponible, Profesional profesional) {
        this.id = id;
        this.fecha = fecha;
        this.hora = hora;
        this.disponible = disponible;
        this.profesional = profesional;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    public Profesional getProfesional() {
        return profesional;
    }

    public void setProfesional(Profesional profesional) {
        this.profesional = profesional;
    }

    public Cita getCita() {
        return cita;
    }

    public void setCita(Cita cita) {
        this.cita = cita;
    }

    

    

    
}
