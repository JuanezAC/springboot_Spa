package com.proyecto.spa_1.Entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;

@JsonPropertyOrder({"id", "nombre", "especialidad", "activo", "correo", "telefono"})
@Entity
@Table(name = "profesionales")
public class Profesional {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String especialidad;

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(nullable = false)
    private String telefono;

    @Column(nullable = false)
    private Boolean activo = true;

    @OneToMany(mappedBy = "profesional", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<HorarioDisponible> horarios;

    // ← CAMBIAR: Usar @JsonIgnore en lugar de @JsonManagedReference
    @OneToMany(mappedBy = "profesional", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Cita> citas;

    public Profesional() {

    }

    public Profesional(Long id, String nombre, String especialidad, String correo, String telefono, Boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.correo = correo;
        this.telefono = telefono;
        this.activo = activo;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public List<Cita> getCitas() {
        return citas;
    }

    public void setCitas(List<Cita> citas) {
        this.citas = citas;
    }

    public List<HorarioDisponible> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<HorarioDisponible> horarios) {
        this.horarios = horarios;
    }

    


    
}
