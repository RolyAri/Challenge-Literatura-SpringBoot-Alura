package com.alura.literatura.principal;

import com.alura.literatura.model.Datos;
import com.alura.literatura.model.DatosLibro;
import com.alura.literatura.model.Libro;
import com.alura.literatura.model.Persona;
import com.alura.literatura.repository.LibroRepository;
import com.alura.literatura.repository.PersonaRepository;
import com.alura.literatura.service.ConsumoAPI;
import com.alura.literatura.service.ConvierteDatos;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private final String BASE_URL = "http://gutendex.com/books/";
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos convierteDatos = new ConvierteDatos();
    private LibroRepository libroRepository;
    private PersonaRepository personaRepository;

    public Principal(LibroRepository libroRepository, PersonaRepository personaRepository) {
        this.libroRepository = libroRepository;
        this.personaRepository = personaRepository;
    }

    public void muestraElMenu(){
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar libro por titulo 
                    2 - Listar libros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos en determinado año
                    5 - Listar libros por idioma 
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarLibroPorTitulo();
                    break;
                case 2:
                    listarLibrosRegistrados();
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    break;
                case 6:
                    break;
                case 7:
                    break;
                case 8:
                    break;
                case 9:
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }

    }

    public void buscarDatos(){
        var json = consumoAPI.obtenerDatos(BASE_URL);
        var datos = convierteDatos.obtenerDatos(json, Datos.class);
        System.out.println(datos);
    }

    public void buscarLibroPorTitulo(){
        System.out.println("Ingrese el nombre del libro que desea buscar");
        String titulo = teclado.nextLine();
        var json = consumoAPI.obtenerDatos(BASE_URL+"?search="+titulo.replace(" ","%20"));
        Datos datos = convierteDatos.obtenerDatos(json, Datos.class);
        Optional<DatosLibro> datosLibro = datos.libros().stream().findFirst();
        if(datosLibro.isPresent()){
            Libro libro = new Libro(datosLibro.get());
            try {
                libroRepository.save(libro);
                System.out.println("""
                        ----- LIBRO -----
                          Título: %s,
                          Autor: %s,
                          Idioma: %s,
                          Numero de descargas : %d
                        -----------------
                        """.formatted(libro.getTitulo(), libro.getAutores().stream().map(a -> a.getNombre()).toList(), libro.getIdioma(), libro.getTotalDescargas()));
            }catch (DataIntegrityViolationException e){
                System.out.println("No se puede registrar el mismo libro más de una vez");
            }

        }
    }
    public void listarLibrosRegistrados(){
        List<Libro> libros = libroRepository.findAll();
        libros.forEach(libro -> {
            System.out.println("""
                        ----- LIBRO -----
                          Título: %s,
                          Autor: %s,
                          Idioma: %s,
                          Numero de descargas : %d
                        -----------------
                        """.formatted(libro.getTitulo(), libro.getAutores().stream().map(a -> a.getNombre()).toList(), libro.getIdioma(), libro.getTotalDescargas()));
        });
    }

    public void listarAutoresRegistrados(){
        List<Persona> autores = personaRepository.findAll();
        autores.forEach(autor -> {
            System.out.println("""
                    Autor: %s,
                    Fecha de nacimiento: %d,
                    Fecha de fallecimiento %d,
                    Libros: %s
                    """.formatted(autor.getNombre(), autor.getAnioNacimiento(), autor.getAnioFallecimiento(), autor.getLibro()));
        });
    }
}
