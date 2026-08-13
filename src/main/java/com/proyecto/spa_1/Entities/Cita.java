package com.proyecto.spa_1.Entities;

import java.time.LocalDate;
import java.time.LocalTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
@Table(name = "citas")
public class Cita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime hora;

    @Column(length = 255)
    private String observacion;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnoreProperties({"citas", "hibernateLazyInitializer", "handler"}) // ← CAMBIAR
    private Usuario usuario;
    
    @ManyToOne
    @JoinColumn(name = "profesional_id", nullable = false)
    @JsonIgnoreProperties({"horarios", "citas", "hibernateLazyInitializer", "handler"}) // ← CAMBIAR
    private Profesional profesional;
    
    @ManyToOne
    @JoinColumn(name = "servicio_id", nullable = false)
    @JsonIgnoreProperties({"citas", "hibernateLazyInitializer", "handler"}) // ← CAMBIAR
    private Servicio servicio;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "horario_id", unique = true) // unique=true obliga a que sea 1:1 en BD
    @JsonIgnoreProperties({"cita", "hibernateLazyInitializer", "handler"})
    private HorarioDisponible horario;

    public Cita() {

    }

    public Cita(Long id, LocalDate fecha, LocalTime hora, String observacion) {
        this.id = id;
        this.fecha = fecha;
        this.hora = hora;
        this.observacion = observacion;
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

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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

    public HorarioDisponible getHorario() {
        return horario;
    }

    public void setHorario(HorarioDisponible horario) {
        this.horario = horario;
    }

    

    
}