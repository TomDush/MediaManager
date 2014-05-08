package fr.dush.mediamanager.plugins.enrich.moviesdb;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.model.*;
import com.omertron.themoviedbapi.results.TmdbResultsList;
import fr.dush.mediamanager.tools.RetryApi;

import javax.enterprise.inject.Alternative;

/**
 * If you have a bad internet connection, like me, this decorator retry some times each request before giving up.<br/>
 * TODO Using interceptor (AOP) should be a better choice...
 */
@Alternative
public class TheMovieDBRetryDecorator extends TheMovieDbApi {

    private TheMovieDbApi theMovieDbApi;

    protected TheMovieDBRetryDecorator() throws MovieDbException {
        super("");
    }

    public TheMovieDBRetryDecorator(String apiKey) throws MovieDbException {
        super(apiKey);
        this.theMovieDbApi = new TheMovieDbApi(apiKey);
    }

    @Override
    public MovieDb getMovieInfo(final int movieId, final String language, final String... appendToResponse) throws
            MovieDbException {

        return RetryApi.retry(new RetryApi.MethodWithReturn<MovieDb>() {
            @Override
            public MovieDb doIt() throws MovieDbException {
                return theMovieDbApi.getMovieInfo(movieId, language, appendToResponse);
            }
        });
    }

    @Override
    public MovieDb getMovieInfoImdb(final String imdbId, final String language, final String... appendToResponse)
            throws MovieDbException {
        return RetryApi.retry(new RetryApi.MethodWithReturn<MovieDb>() {
            @Override
            public MovieDb doIt() throws MovieDbException {
                return theMovieDbApi.getMovieInfoImdb(imdbId, language, appendToResponse);
            }
        });
    }

    @Override
    public TmdbResultsList<AlternativeTitle> getMovieAlternativeTitles(final int movieId, final String country,
                                                                       final String... appendToResponse) throws
            MovieDbException {
        return RetryApi.retry(new RetryApi.MethodWithReturn<TmdbResultsList<AlternativeTitle>>() {
            @Override
            public TmdbResultsList<AlternativeTitle> doIt() throws MovieDbException {
                return theMovieDbApi.getMovieAlternativeTitles(movieId, country, appendToResponse);
            }
        });
    }

    @Override
    public TmdbResultsList<Person> getMovieCasts(final int movieId, final String... appendToResponse) throws
            MovieDbException {
        return RetryApi.retry(new RetryApi.MethodWithReturn<TmdbResultsList<Person>>() {
            @Override
            public TmdbResultsList<Person> doIt() throws MovieDbException {
                return theMovieDbApi.getMovieCasts(movieId, appendToResponse);
            }
        });
    }

    @Override
    public TmdbResultsList<Artwork> getMovieImages(final int movieId, final String language,
                                                   final String... appendToResponse) throws MovieDbException {
        return RetryApi.retry(new RetryApi.MethodWithReturn<TmdbResultsList<Artwork>>() {
            @Override
            public TmdbResultsList<Artwork> doIt() throws MovieDbException {
                return theMovieDbApi.getMovieImages(movieId, language, appendToResponse);
            }
        });
    }

    @Override
    public TmdbResultsList<Keyword> getMovieKeywords(final int movieId, final String... appendToResponse) throws
            MovieDbException {
        return RetryApi.retry(new RetryApi.MethodWithReturn<TmdbResultsList<Keyword>>() {
            @Override
            public TmdbResultsList<Keyword> doIt() throws MovieDbException {
                return theMovieDbApi.getMovieKeywords(movieId, appendToResponse);
            }
        });
    }

    @Override
    public TmdbResultsList<ReleaseInfo> getMovieReleaseInfo(final int movieId, final String language,
                                                            final String... appendToResponse) throws MovieDbException {
        return RetryApi.retry(new RetryApi.MethodWithReturn<TmdbResultsList<ReleaseInfo>>() {
            @Override
            public TmdbResultsList<ReleaseInfo> doIt() throws MovieDbException {
                return theMovieDbApi.getMovieReleaseInfo(movieId, language, appendToResponse);
            }
        });
    }

    @Override
    public TmdbResultsList<Trailer> getMovieTrailers(final int movieId, final String language,
                                                     final String... appendToResponse) throws MovieDbException {
        return RetryApi.retry(new RetryApi.MethodWithReturn<TmdbResultsList<Trailer>>() {
            @Override
            public TmdbResultsList<Trailer> doIt() throws MovieDbException {
                return theMovieDbApi.getMovieTrailers(movieId, language, appendToResponse);
            }
        });
    }

    @Override
    public TmdbResultsList<Translation> getMovieTranslations(final int movieId, final String... appendToResponse)
            throws MovieDbException {
        return RetryApi.retry(new RetryApi.MethodWithReturn<TmdbResultsList<Translation>>() {
            @Override
            public TmdbResultsList<Translation> doIt() throws MovieDbException {
                return theMovieDbApi.getMovieTranslations(movieId, appendToResponse);
            }
        });
    }

    @Override
    public TmdbResultsList<MovieDb> getSimilarMovies(final int movieId, final String language, final int page,
                                                     final String... appendToResponse) throws MovieDbException {
        return RetryApi.retry(new RetryApi.MethodWithReturn<TmdbResultsList<MovieDb>>() {
            @Override
            public TmdbResultsList<MovieDb> doIt() throws MovieDbException {
                return theMovieDbApi.getSimilarMovies(movieId, language, page, appendToResponse);
            }
        });
    }

    @Override
    public TmdbResultsList<MovieList> getMovieLists(final int movieId, final String language, final int page,
                                                    final String... appendToResponse) throws MovieDbException {
        return RetryApi.retry(new RetryApi.MethodWithReturn<TmdbResultsList<MovieList>>() {
            @Override
            public TmdbResultsList<MovieList> doIt() throws MovieDbException {
                return theMovieDbApi.getMovieLists(movieId, language, page, appendToResponse);
            }
        });
    }

    @Override
    public MovieDb getLatestMovie() throws MovieDbException {
        return RetryApi.retry(new RetryApi.MethodWithReturn<MovieDb>() {
            @Override
            public MovieDb doIt() throws MovieDbException {
                return theMovieDbApi.getLatestMovie();
            }
        });
    }

    @Override
    public CollectionInfo getCollectionInfo(final int collectionId, final String language) throws MovieDbException {
        return RetryApi.retry(new RetryApi.MethodWithReturn<CollectionInfo>() {
            @Override
            public CollectionInfo doIt() throws MovieDbException {
                return theMovieDbApi.getCollectionInfo(collectionId, language);
            }
        });
    }

    @Override
    public TmdbResultsList<Artwork> getCollectionImages(final int collectionId,
                                                        final String language) throws MovieDbException {
        return RetryApi.retry(new RetryApi.MethodWithReturn<TmdbResultsList<Artwork>>() {
            @Override
            public TmdbResultsList<Artwork> doIt() throws MovieDbException {
                return theMovieDbApi.getCollectionImages(collectionId, language);
            }
        });
    }

    @Override
    public Person getPersonInfo(final int personId, String... appendToResponse) throws MovieDbException {
        return RetryApi.retry(new RetryApi.MethodWithReturn<Person>() {
            @Override
            public Person doIt() throws MovieDbException {
                return theMovieDbApi.getPersonInfo(personId);
            }
        });
    }

    @Override
    public TmdbResultsList<Artwork> getPersonImages(final int personId) throws MovieDbException {
        return RetryApi.retry(new RetryApi.MethodWithReturn<TmdbResultsList<Artwork>>() {
            @Override
            public TmdbResultsList<Artwork> doIt() throws MovieDbException {
                return theMovieDbApi.getPersonImages(personId);
            }
        });
    }

    @Override
    public TmdbResultsList<MovieDb> searchMovie(final String movieName, final int searchYear, final String language,
                                                final boolean includeAdult, final int page) throws MovieDbException {
        return RetryApi.retry(new RetryApi.MethodWithReturn<TmdbResultsList<MovieDb>>() {
            @Override
            public TmdbResultsList<MovieDb> doIt() throws MovieDbException {
                return theMovieDbApi.searchMovie(movieName, searchYear, language, includeAdult, page);
            }
        });
    }

    @Override
    public TmdbResultsList<Collection> searchCollection(final String query, final String language,
                                                        final int page) throws MovieDbException {
        return RetryApi.retry(new RetryApi.MethodWithReturn<TmdbResultsList<Collection>>() {
            @Override
            public TmdbResultsList<Collection> doIt() throws MovieDbException {
                return theMovieDbApi.searchCollection(query, language, page);
            }
        });
    }

}
