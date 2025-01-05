package com.alura.literatura.repository;

import com.alura.literatura.model.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PersonaRepository extends JpaRepository<Persona, Long> {
    Persona findByNombre(String nombre);
    List<Persona> findByAnioFallecimientoLessThanEqual(int anio);
    @Query("SELECT p FROM Persona p WHERE :anio BETWEEN p.anioNacimiento AND COALESCE(p.anioFallecimiento, :anio)")
    List<Persona> findByAnioVivo(@Param("anio") Integer anio);
}
