package com.alura.literatura.service;

import com.alura.literatura.model.Libro;
import com.alura.literatura.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibroService {
    @Autowired
    LibroRepository libroRepository;
    public List<Libro> obtenerTodosLosLibros(){
        return libroRepository.findAll();
    }
}
