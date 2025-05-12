package com.movieflix.service;

import com.movieflix.dto.MovieDTO;
import com.movieflix.dto.MoviePageResponse;
import com.movieflix.entities.Movie;
import com.movieflix.exceptions.FileExistsException;
import com.movieflix.exceptions.MovieNotFoundException;
import com.movieflix.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private FileService fileService;

    @Value("${project.poster}")
    private String path;

    @Value("${base.url}")
    private String baseUrl;

    public MovieServiceImpl(MovieRepository movieRepository, FileService fileService) {
        this.movieRepository = movieRepository;
        this.fileService = fileService;
    }

    @Override
    public MovieDTO addMovie(MovieDTO movieDTO, MultipartFile file) throws IOException {

        if(Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()))){
            throw new FileExistsException("File already exists! Please enter another file name!");
        }
        //1.upload the file
        String uploadedFileName=fileService.uploadFile(path,file);

        //2.set poster as fileName
        movieDTO.setPoster(uploadedFileName);

        //3.map dto to movie object
        Movie movie=new Movie(
                null,
                movieDTO.getTitle(),
                movieDTO.getDirector(),
                movieDTO.getStudio(),
                movieDTO.getMovieCast(),
                movieDTO.getReleaseYear(),
                movieDTO.getPoster()
        );

        //4.save the Movie Object
        Movie savedMovie = movieRepository.save(movie);

        //5.Generate the poster UrL
        String posterUrl=baseUrl+ "/file/"+uploadedFileName;

        //6.map Movie to dto
        MovieDTO response=new MovieDTO(
                savedMovie.getMovieId(),
                savedMovie.getTitle(),
                savedMovie.getDirector(),
                savedMovie.getStudio(),
                savedMovie.getMovieCast(),
                savedMovie.getReleaseYear(),
                savedMovie.getPoster(),
                posterUrl
        );

        return response;
    }

    @Override
    public MovieDTO getMovie(Integer movieId) {
        Movie movie= movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with id = " + movieId));

        String posterUrl=baseUrl+ "/file/"+movie.getPoster();

        MovieDTO response=new MovieDTO(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl
        );
        return response;
    }

    @Override
    public List<MovieDTO> getAllMovies() {
        List<Movie> movies=movieRepository.findAll();
        List<MovieDTO> movieDtos=new ArrayList<>();

        for(Movie movie:movies){
            String posterUrl=baseUrl+ "/file/"+movie.getPoster();
            MovieDTO movieDto=new MovieDTO(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl
            );
            movieDtos.add(movieDto);
        }
        return movieDtos;
    }

    @Override
    public MovieDTO updateMovie(Integer movieId, MovieDTO movieDTO, MultipartFile file) throws IOException {
        Movie movie= movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with id  = " + movieId));

        String fileName=movie.getPoster();
        if(file != null){
            Files.deleteIfExists(Paths.get(path + File.separator + fileName));
            fileName=fileService.uploadFile(path,file);
        }

        movieDTO.setPoster(fileName);
        Movie movie1=new Movie(
                movieDTO.getMovieId(),
                movieDTO.getTitle(),
                movieDTO.getDirector(),
                movieDTO.getStudio(),
                movieDTO.getMovieCast(),
                movieDTO.getReleaseYear(),
                movieDTO.getPoster()
        );
       Movie updatedMovie=movieRepository.save(movie1);

       String posterUrl=baseUrl+ "/file/"+movie.getPoster();
       MovieDTO response=new MovieDTO(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl
        );
       return response;
    }

    @Override
    public String deleteMovie(Integer movieId) throws IOException {
        Movie movie= movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found with id  = " + movieId));

        Integer id=movie.getMovieId();
        Files.deleteIfExists(Paths.get(path+File.separator+movie.getPoster()));
        movieRepository.delete(movie);

        return "Movie deleted with id = "+id;
    }

    @Override
    public MoviePageResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Movie> moviePage=movieRepository.findAll(pageable);
        List<Movie> movies=moviePage.getContent();

        List<MovieDTO> movieDtos=new ArrayList<>();
        for(Movie movie:movies) {
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDTO movieDto = new MovieDTO(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl
            );
            movieDtos.add(movieDto);
        }

        return new MoviePageResponse(movieDtos, pageNumber, pageSize,
                                     moviePage.getTotalElements(),moviePage.getTotalPages(),
                                     moviePage.isLast());
    }

    @Override
    public MoviePageResponse getAllMoviesWithPaginationAndSorting(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sort= sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                                                              : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Movie> moviePage=movieRepository.findAll(pageable);
        List<Movie> movies=moviePage.getContent();

        List<MovieDTO> movieDtos=new ArrayList<>();
        for(Movie movie:movies) {
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDTO movieDto = new MovieDTO(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl
            );
            movieDtos.add(movieDto);
        }

        return new MoviePageResponse(movieDtos, pageNumber, pageSize,
                moviePage.getTotalElements(),moviePage.getTotalPages(),
                moviePage.isLast());
    }
}
