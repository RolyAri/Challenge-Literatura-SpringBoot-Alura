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
import java.util.stream.Collectors;

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
                    listarAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresVivosPorAnio();
                    break;
                case 5:
                    listarLibrosPorIdioma();
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
            System.out.println(libro);
            try {
//                Persona autorExistente = personaRepository.findByNombre(libro.getAutor().getNombre());
//                if (autorExistente != null) {
//                    personaRepository.merge(autorExistente);
//                }
                libroRepository.save(libro);
                System.out.println("""
                        ----- LIBRO -----
                          Título: %s,
                          Autor: %s,
                          Idioma: %s,
                          Numero de descargas : %d
                        -----------------
                        """.formatted(libro.getTitulo(), libro.getAutor().getNombre(), libro.getIdioma(), libro.getTotalDescargas()));
            }catch (DataIntegrityViolationException e){
                System.out.println("No se puede registrar el mismo libro más de una vez");
            }
        }else {
            System.out.println("""
                    -----------------
                    LIBRO NO ENCONTRADO
                    -----------------
                    """);
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
                        """.formatted(libro.getTitulo(), libro.getAutor().getNombre(), libro.getIdioma(), libro.getTotalDescargas()));
        });
        if(libros.size() <= 0){
            System.out.println("""
                    -----------------
                    NO HAY LIBROS REGISTRADOS
                    -----------------
                    """);
        }
    }

    public void listarAutoresRegistrados(){
        List<Persona> autores = personaRepository.findAll();
        autores.forEach(autor -> {
            System.out.println("""
                    Autor: %s,
                    Fecha de nacimiento: %d,
                    Fecha de fallecimiento %d,
                    Libros: %s
                    """.formatted(autor.getNombre(), autor.getAnioNacimiento(), autor.getAnioFallecimiento(), autor.getLibros().stream().map(l -> l.getTitulo()).collect(Collectors.toList())));
        });
        if(autores.size() <= 0){
            System.out.println("""
                    -----------------
                    NO HAY AUTORES REGISTRADOS
                    -----------------
                    """);
        }
    }

    public void listarAutoresVivosPorAnio(){
        System.out.println("Ingrese el año vivo de autor(es) que desea buscar");
        var anio = teclado.nextInt();
        teclado.nextLine();
        List<Persona> autoresVivos = personaRepository.findByAnioVivo(anio);
        autoresVivos.forEach(autor -> {
            System.out.println("""
                    Autor: %s,
                    Fecha de nacimiento: %d,
                    Fecha de fallecimiento %d,
                    Libros: %s
                    """.formatted(autor.getNombre(), autor.getAnioNacimiento(), autor.getAnioFallecimiento(), autor.getLibros().stream().map(l -> l.getTitulo()).collect(Collectors.toList())));
        });
        if(autoresVivos.size() <= 0){
            System.out.println("""
                    -----------------
                    NO HAY AUTORES VIVOS EN EL AÑO %d
                    -----------------
                    """.formatted(anio));
        }
    }

    public void listarLibrosPorIdioma(){
        System.out.println("""
                    Ingrese el idioma para buscar los libros:
                    es - español
                    en - inglés
                    fr - francés
                    pt - portugués
                    """);
        var idioma = teclado.nextLine();
        List<Libro> librosPorIdioma = libroRepository.findByIdioma(idioma);
        librosPorIdioma.forEach(libro -> {
            System.out.println("""
                        ----- LIBRO -----
                          Título: %s,
                          Autor: %s,
                          Idioma: %s,
                          Numero de descargas : %d
                        -----------------
                        """.formatted(libro.getTitulo(), libro.getAutor().getNombre(), libro.getIdioma(), libro.getTotalDescargas()));
        });
        if (librosPorIdioma.size() <= 0){
            System.out.println("""
                    -----------------
                    NO HAY LIBROS REGISTRADOS EN IDIOMA %s
                    -----------------
                    """.formatted(idioma.toUpperCase()));
        }
    }
}
