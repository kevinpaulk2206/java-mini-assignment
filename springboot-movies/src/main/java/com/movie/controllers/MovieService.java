package com.movie.controllers;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.movie.model.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MovieService {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    public Movie getMovieById(String id) {
        return dynamoDBMapper.load(Movie.class, id);
    }

    public Movie createMovie(Movie movie) {
        dynamoDBMapper.save(movie);
        return movie;
    }

    public Movie updateMovie(String id, Movie movie) {
        Movie existingMovie = getMovieById(id);
        if (existingMovie != null) {
            movie.setImdb_title_id(id);
            dynamoDBMapper.save(movie);
            return movie;
        } else {
            return null;
        }
    }

    public boolean deleteMovie(String id) {
        Movie existingMovie = getMovieById(id);
        if (existingMovie != null) {
            dynamoDBMapper.delete(existingMovie);
            return true;
        } else {
            return false;
        }
    }
}

