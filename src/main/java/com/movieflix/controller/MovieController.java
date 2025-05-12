package com.movieflix.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieflix.dto.MovieDTO;
import com.movieflix.dto.MoviePageResponse;
import com.movieflix.service.MovieService;
import com.movieflix.utils.AppConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.movieflix.exceptions.EmptyFileException;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/movie")
public class MovieController {

    private final MovieService movieService;
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/add-movie")
    public ResponseEntity<MovieDTO> addMovie(@RequestPart MultipartFile file,
                                             @RequestPart String movieDTO) throws IOException, EmptyFileException {

        if(file.isEmpty()) {
            throw new EmptyFileException("File is empty! Please send another file!");
        }
        MovieDTO dto= convertToMovieDto(movieDTO);
        return new ResponseEntity<>(movieService.addMovie(dto,file), HttpStatus.CREATED);
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<MovieDTO> getMovie( @PathVariable Integer movieId ) {
        return ResponseEntity.ok(movieService.getMovie(movieId));
    }

    @GetMapping()
    public ResponseEntity<List<MovieDTO>> getAllMovie(  ) {
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    @PutMapping("/{movieId}")
    public ResponseEntity<MovieDTO> updateMovie(@PathVariable Integer movieId,
                                                @RequestPart MultipartFile file,
                                                @RequestPart String movieDTOobj) throws IOException {
        if(file.isEmpty()) file=null;
        MovieDTO dto= convertToMovieDto(movieDTOobj);

        return ResponseEntity.ok(movieService.updateMovie(movieId,dto,file));
    }

    @DeleteMapping("/{movieId}")
    public ResponseEntity<String> deleteMovie(@PathVariable Integer movieId) throws IOException {
        return ResponseEntity.ok(movieService.deleteMovie(movieId));
    }

    @GetMapping("/allMoviesPage")
    public ResponseEntity<MoviePageResponse> getMoviesWithPagination(
            @RequestParam(defaultValue =AppConstants.PAGE_NUMBER, required= false) Integer pageNumber,
            @RequestParam(defaultValue =AppConstants.PAGE_SIZE, required= false) Integer pageSize) {

        return ResponseEntity.ok(movieService.getAllMoviesWithPagination(pageNumber,pageSize));
    }

    @GetMapping("/allMoviesPageSort")
    public ResponseEntity<MoviePageResponse> getMoviesWithPaginationAndSorting(
            @RequestParam(defaultValue =AppConstants.PAGE_NUMBER, required= false) Integer pageNumber,
            @RequestParam(defaultValue =AppConstants.PAGE_SIZE, required= false) Integer pageSize,
            @RequestParam(defaultValue =AppConstants.SORT_BY, required= false) String sortBy,
            @RequestParam(defaultValue =AppConstants.SORT_ORDER, required= false) String sortOrder) {

        return ResponseEntity.ok(movieService.getAllMoviesWithPaginationAndSorting(pageNumber,pageSize,sortBy,sortOrder));
    }

    private MovieDTO convertToMovieDto(String movieDtoObj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(movieDtoObj, MovieDTO.class);
    }
}
