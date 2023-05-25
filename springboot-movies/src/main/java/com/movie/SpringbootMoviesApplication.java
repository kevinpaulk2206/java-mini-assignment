package com.movie;

import com.movie.middleware.Timing;
import com.movie.model.Movie;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;


import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringbootMoviesApplication implements WebMvcConfigurer {

	@Bean
	public Timing timingMiddleware() {
		return new Timing();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(timingMiddleware());
	}
}




// Reading csv file
class CSVRead {
	public static void main(String[] args) {
		String csvFile = "C:/Users/kpaulk/Documents/movies.csv";
		// Creating list called Movie
		List<Movie> movies = new ArrayList<>();

		try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
			String[] nextLine;
			reader.readNext();

			while ((nextLine = reader.readNext()) != null) {

				String imdb_title_id = nextLine[0];
				String title = nextLine[1];
				String original_title = nextLine[2];
				String year = nextLine[3];
				String date_published = nextLine[4];
				String genre = nextLine[5];
				String duration = nextLine[6];
				String country = nextLine[7];
				String language = nextLine[8];
				String director = nextLine[9];
				String writer = nextLine[10];
				String production_company = nextLine[11];
				String actors = nextLine[12];
				String description = nextLine[13];
				String avg_vote = nextLine[14];
				String votes = nextLine[15];
				String budget = nextLine[16];
				String usa_gross_income = nextLine[17];
				String worlwide_gross_income = nextLine[18];
				String metascore = nextLine[19];
				String reviews_from_users = nextLine[20];
				String reviews_from_critics = nextLine[21];


				Movie movie = new Movie();
				movie.setImdbTitleId(imdb_title_id);
				movie.setTitle(title);
				movie.setOriginalTitle(original_title);
				movie.setYear(year);
				movie.setDatePublished(date_published);
				movie.setGenre(genre);
				movie.setDuration(duration);
				movie.setCountry(country);
				movie.setLanguage(language);
				movie.setDirector(director);
				movie.setWriter(writer);
				movie.setProductionCompany(production_company);
				movie.setActors(actors);
				movie.setDescription(description);
				movie.setAverageVote(avg_vote);
				movie.setVotes(votes);
				movie.setBudget(budget);
				movie.setUsaGrossIncome(usa_gross_income);
				movie.setWorlwideGrossIncome(worlwide_gross_income);
				movie.setMetascore(metascore);
				movie.setReviewsFromUsers(reviews_from_users);
				movie.setReviewsFromCritics(reviews_from_critics);

				movies.add(movie);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CsvValidationException e) {
			throw new RuntimeException(e);
		}
		for (Movie movie : movies) {
			System.out.println(movie);
		}
	}
}




