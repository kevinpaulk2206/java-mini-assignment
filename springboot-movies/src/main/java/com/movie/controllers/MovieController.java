package com.movie.controllers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.movie.model.Movie;
import jdk.javadoc.internal.doclets.toolkit.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.image.ByteLookupTable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final AmazonDynamoDB amazonDynamoDB;
    private final DynamoDBMapper dynamoDBMapper;

    public MovieController(AmazonDynamoDB amazonDynamoDB, DynamoDBMapper dynamoDBMapper) {
        this.amazonDynamoDB = amazonDynamoDB;
        this.dynamoDBMapper = dynamoDBMapper;
    }

    //problem statement 1
    @GetMapping("/directed-by/{director}/{startYear}/{endYear}")
    public List<String> getMoviesByDirectorAndYearRange(
            @PathVariable String director,
            @PathVariable int startYear,
            @PathVariable int endYear) {

        DynamoDB dynamoDB = null;
        Table movieTable = dynamoDB.getTable("Movie"); // Replace "Movie" with your DynamoDB table name

        List<String> movieTitles = new ArrayList<>();

        // Define the scan parameters
        ScanSpec scanSpec = new ScanSpec()
                .withFilterExpression("director = :director and #yr between :startYear and :endYear")
                .withNameMap(new NameMap().with("#yr", "year"))
                .withValueMap(new ValueMap()
                        .withString(":director", director)
                        .withInt(":startYear", startYear)
                        .withInt(":endYear", endYear));


        ItemCollection<ScanOutcome> scanOutcome = movieTable.scan(scanSpec);

        return movieTitles;
    }

    //problem statement 2

    @GetMapping("/report")
    public ResponseEntity<List<Movie>> generateReport(@RequestParam int reviewThreshold) {
        DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
        Table movieTable = dynamoDB.getTable("Movie"); // Replace "Movie" with your DynamoDB table name

        List<Movie> movies = new ArrayList<>();

        ScanSpec scanSpec = new ScanSpec();

        ItemCollection<ScanOutcome> scanOutcome = movieTable.scan(scanSpec);

        scanOutcome.forEach(item -> {
            Movie movie = mapToMovie(item.asMap());
            if (movie.getLanguage().equalsIgnoreCase("English") && movie.getReviewsFromUsers() > reviewThreshold) {
                movies.add(movie);
            }
        });

        movies.sort(Comparator.comparing(Movie::getReviewsFromUsers).reversed());

        return ResponseEntity.ok(movies);
    }


    //problem statement 3

    @GetMapping("/top-movies/{count}")
    public ResponseEntity<List<Movie>> getTopMovies(@PathVariable int count) {
        DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
        Table movieTable = dynamoDB.getTable("Movie");

        List<Movie> movies = new ArrayList<>();

        ItemCollection<ScanOutcome> scanOutcome = movieTable.scan();

        for (Item item : scanOutcome) {
            Movie movie = mapToMovie(item.asMap());
            movies.add(movie);
        }

        movies.sort(Comparator.comparing(Movie::getBudget).reversed());
        List<Movie> topMovies = movies.stream().limit(count).collect(Collectors.toList());

        return ResponseEntity.ok(topMovies);
    }

    private Movie mapToMovie(java.util.Map<String, Object> itemAttributes) {
        Movie movie = new Movie();
        movie.setTitle((String) itemAttributes.get("title"));
        movie.setLanguage((String) itemAttributes.get("language"));
        movie.setReviewsFromUsers((int) itemAttributes.get("userReviews"));
        movie.setBudget((String) itemAttributes.get("budget"));

        return movie;
    }


    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable String id) {
        DynamoDBMapperConfig mapperConfig = DynamoDBMapperConfig.builder()
                .withTableNameOverride(DynamoDBMapperConfig.TableNameOverride.withTableNameReplacement("movies"))
                .build();
        Movie movie = dynamoDBMapper.load(Movie.class, id, mapperConfig);
        if (movie != null) {
            return ResponseEntity.ok(movie);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Movie> createMovie(@RequestBody Movie movie) {
        DynamoDBMapperConfig mapperConfig = DynamoDBMapperConfig.builder()
                .withTableNameOverride(DynamoDBMapperConfig.TableNameOverride.withTableNameReplacement("movies"))
                .build();
        dynamoDBMapper.save(movie, mapperConfig);
        return ResponseEntity.status(HttpStatus.CREATED).body(movie);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable String id, @RequestBody Movie movie) {
        DynamoDBMapperConfig mapperConfig = DynamoDBMapperConfig.builder()
                .withTableNameOverride(DynamoDBMapperConfig.TableNameOverride.withTableNameReplacement("movies"))
                .build();
        movie.setImdb_title_id(id);
        dynamoDBMapper.save(movie, mapperConfig);
        return ResponseEntity.ok(movie);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable String id) {
        DynamoDBMapperConfig mapperConfig = DynamoDBMapperConfig.builder()
                .withTableNameOverride(DynamoDBMapperConfig.TableNameOverride.withTableNameReplacement("movies"))
                .build();
        Movie movie = new Movie();
        movie.setImdb_title_id(id);
        dynamoDBMapper.delete(movie, mapperConfig);
        return ResponseEntity.noContent().build();
    }

    //token authentication
    private static final String TOKEN = "GhFDJPeUjJ";

    @GetMapping("/api/auth")
    public ResponseEntity<String> myEndpoint(@RequestHeader("Authorization") String token) {
        // Validating the token
        if (token.equals(TOKEN)) {

            return ResponseEntity.ok("Access granted!");
        } else {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }
}





