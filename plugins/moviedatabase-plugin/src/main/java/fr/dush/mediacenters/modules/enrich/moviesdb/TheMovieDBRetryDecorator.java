package fr.dush.mediacenters.modules.enrich.moviesdb;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.Alternative;
import java.util.List;

/**
 * If you have a bad internet connection, like me, this decorator retry some times each request before giving up.<br/>
 * TODO Using interceptor (AOP) should be a better choice...
 */
@Alternative
public class TheMovieDBRetryDecorator extends TheMovieDbApi {

    public static final Logger LOGGER = LoggerFactory.getLogger(TheMovieDBRetryDecorator.class);

    public static final int MAX_TRY = 5;

    private TheMovieDbApi theMovieDbApi;

    protected TheMovieDBRetryDecorator() throws MovieDbException {
        super("");
    }

    public TheMovieDBRetryDecorator(String apiKey) throws MovieDbException {
        super(apiKey);
        this.theMovieDbApi = new TheMovieDbApi(apiKey);
    }

    @Override
    public MovieDb getMovieInfo(final int movieId, final String language) throws MovieDbException {

        return retry(new MethodWithReturn<MovieDb>() {
            @Override
            public MovieDb doIt() throws MovieDbException {
                return theMovieDbApi.getMovieInfo(movieId, language);
            }
        });
    }

    @Override
    public MovieDb getMovieInfoImdb(final String imdbId, final String language) throws MovieDbException {
        return retry(new MethodWithReturn<MovieDb>() {
            @Override
            public MovieDb doIt() throws MovieDbException {
                return theMovieDbApi.getMovieInfoImdb(imdbId, language);
            }
        });
    }

    @Override
    public List<AlternativeTitle> getMovieAlternativeTitles(final int movieId,
                                                            final String country) throws MovieDbException {
        return retry(new MethodWithReturn<List<AlternativeTitle>>() {
            @Override
            public List<AlternativeTitle> doIt() throws MovieDbException {
                return theMovieDbApi.getMovieAlternativeTitles(movieId, country);
            }
        });
    }

    @Override
    public List<Person> getMovieCasts(final int movieId) throws MovieDbException {
        return retry(new MethodWithReturn<List<Person>>() {
            @Override
            public List<Person> doIt() throws MovieDbException {
                return theMovieDbApi.getMovieCasts(movieId);
            }
        });
    }

    @Override
    public List<Artwork> getMovieImages(final int movieId, final String language) throws MovieDbException {
        return retry(new MethodWithReturn<List<Artwork>>() {
            @Override
            public List<Artwork> doIt() throws MovieDbException {
                return theMovieDbApi.getMovieImages(movieId, language);
            }
        });
    }

    @Override
    public List<Keyword> getMovieKeywords(final int movieId) throws MovieDbException {
        return retry(new MethodWithReturn<List<Keyword>>() {
            @Override
            public List<Keyword> doIt() throws MovieDbException {
                return theMovieDbApi.getMovieKeywords(movieId);
            }
        });
    }

    @Override
    public List<ReleaseInfo> getMovieReleaseInfo(final int movieId, final String language) throws MovieDbException {
        return retry(new MethodWithReturn<List<ReleaseInfo>>() {
            @Override
            public List<ReleaseInfo> doIt() throws MovieDbException {
                return theMovieDbApi.getMovieReleaseInfo(movieId, language);
            }
        });
    }

    @Override
    public List<Trailer> getMovieTrailers(final int movieId, final String language) throws MovieDbException {
        return retry(new MethodWithReturn<List<Trailer>>() {
            @Override
            public List<Trailer> doIt() throws MovieDbException {
                return theMovieDbApi.getMovieTrailers(movieId, language);
            }
        });
    }

    @Override
    public List<Translation> getMovieTranslations(final int movieId) throws MovieDbException {
        return retry(new MethodWithReturn<List<Translation>>() {
            @Override
            public List<Translation> doIt() throws MovieDbException {
                return theMovieDbApi.getMovieTranslations(movieId);
            }
        });
    }

    @Override
    public List<MovieDb> getSimilarMovies(final int movieId, final String language,
                                          final int page) throws MovieDbException {
        return retry(new MethodWithReturn<List<MovieDb>>() {
            @Override
            public List<MovieDb> doIt() throws MovieDbException {
                return theMovieDbApi.getSimilarMovies(movieId, language, page);
            }
        });
    }

    @Override
    public List<MovieList> getMovieLists(final int movieId, final String language,
                                         final int page) throws MovieDbException {
        return retry(new MethodWithReturn<List<MovieList>>() {
            @Override
            public List<MovieList> doIt() throws MovieDbException {
                return theMovieDbApi.getMovieLists(movieId, language, page);
            }
        });
    }

    @Override
    public MovieDb getLatestMovie() throws MovieDbException {
        return retry(new MethodWithReturn<MovieDb>() {
            @Override
            public MovieDb doIt() throws MovieDbException {
                return theMovieDbApi.getLatestMovie();
            }
        });
    }

    @Override
    public CollectionInfo getCollectionInfo(final int collectionId, final String language) throws MovieDbException {
        return retry(new MethodWithReturn<CollectionInfo>() {
            @Override
            public CollectionInfo doIt() throws MovieDbException {
                return theMovieDbApi.getCollectionInfo(collectionId, language);
            }
        });
    }

    @Override
    public List<Artwork> getCollectionImages(final int collectionId, final String language) throws MovieDbException {
        return retry(new MethodWithReturn<List<Artwork>>() {
            @Override
            public List<Artwork> doIt() throws MovieDbException {
                return theMovieDbApi.getCollectionImages(collectionId, language);
            }
        });
    }

    @Override
    public Person getPersonInfo(final int personId) throws MovieDbException {
        return retry(new MethodWithReturn<Person>() {
            @Override
            public Person doIt() throws MovieDbException {
                return theMovieDbApi.getPersonInfo(personId);
            }
        });
    }

    @Override
    public List<Artwork> getPersonImages(final int personId) throws MovieDbException {
        return retry(new MethodWithReturn<List<Artwork>>() {
            @Override
            public List<Artwork> doIt() throws MovieDbException {
                return theMovieDbApi.getPersonImages(personId);
            }
        });
    }

    @Override
    public List<MovieDb> searchMovie(final String movieName, final int searchYear, final String language,
                                     final boolean includeAdult, final int page) throws MovieDbException {
        return retry(new MethodWithReturn<List<MovieDb>>() {
            @Override
            public List<MovieDb> doIt() throws MovieDbException {
                return theMovieDbApi.searchMovie(movieName, searchYear, language, includeAdult, page);
            }
        });
    }

    @Override
    public List<Collection> searchCollection(final String query, final String language,
                                             final int page) throws MovieDbException {
        return retry(new MethodWithReturn<List<Collection>>() {
            @Override
            public List<Collection> doIt() throws MovieDbException {
                return theMovieDbApi.searchCollection(query, language, page);
            }
        });
    }

    public <T> T retry(MethodWithReturn<T> methodWithReturn) throws MovieDbException {
        int n = 0;
        Exception exception = null;

        try {

            while (n++ < MAX_TRY) {
                try {
                    // Try to do it and accept 5 fails
                    return methodWithReturn.doIt();
                } catch (Exception e) {
                    LOGGER.warn("Error occurred: {}... Will retrying", e.getMessage());
                    exception = e;

                    Thread.sleep(5000);
                }
            }
        } catch (InterruptedException e1) {
            LOGGER.debug("Do not retry any more...", e1.getMessage());
        }

        throw new RuntimeException("Could not get information even after retrying {} times.", exception);
    }

    public interface MethodWithReturn<T> {

        T doIt() throws MovieDbException;
    }
}
