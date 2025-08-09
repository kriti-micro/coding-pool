package java8_stream;

import java.util.*;

public class MovieSortExample {
    public static void main(String[] args) {
        List<Movie> listOfMovies = Arrays.asList(
                new Movie("Inception", 9),
                new Movie("Avengers", 8),
                new Movie("Batman", 7),
                new Movie("Zootopia", 8)
        );


        System.out.println(" // Modern way Sort by rating and name alphabetically ");
        listOfMovies.sort(Comparator
                .comparing(Movie::getRating)
                .thenComparing(Movie::getName));

        // Print result
        listOfMovies.forEach(System.out::println);

        System.out.println(" // Old Way Sort by name alphabetically ");
        //Sorting with old way
        List<Movie> listOfMovies1 = Arrays.asList(
                new Movie("Inception", 9),
                new Movie("Avengers", 8),
                new Movie("Batman", 7),
                new Movie("Zootopia", 8)
        );

        // Using lambda expression
        Collections.sort(listOfMovies1, (o1, o2) -> o1.getName().compareTo(o2.getName()));
        listOfMovies1.forEach(System.out::println);

    }
}

