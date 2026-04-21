package com.proyect.final_proyect_spa4.entities;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@JsonPropertyOrder({"id", "fecha", "hora", "observacion", "usuario", "profesional", "servicio"})
@Entity
@Table(name = "citas", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"profesional_id", "fecha", "hora"})
})
public class Cita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate fecha;
    private LocalTime hora;
    private String observacion;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "profesional_id", referencedColumnName = "id")
    private Profesional profesional;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "servicio_id", referencedColumnName = "id")
    private Servicio servicio;

    public Cita(){
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
}
