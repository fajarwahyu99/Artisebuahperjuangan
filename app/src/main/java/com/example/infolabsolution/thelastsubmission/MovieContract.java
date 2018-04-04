package com.example.infolabsolution.thelastsubmission;



import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
public class MovieContract {


    public static final String CONTENT_AUTHORITY = "com.example.infolabsolution.thelastsubmission";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final String PATH_MOVIE = "movie";

    public static final String PATH_REVIEW = "review";

    public static final String PATH_TRAILER = "trailer";

    public static final String PATH_CACHE_MOVIE_MOST_POPULAR = "cache_movie_most_popular";

    public static final String PATH_CACHE_MOVIE_TOP_RATED = "cache_movie_top_rated";
    public static final class FavMovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";

        public static final String COLUMN_MOVIE_POSTER_IMAGE_THUMBNAIL
                = "movie_poster_image_thumbnail";

        public static final String COLUMN_A_PLOT_SYNOPSIS = "a_plot_synopsis";

        public static final String COLUMN_USER_RATING = "user_rating";

        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_NUMBER_OF_REVIEWS = "number_of_review";
        public static final String COLUMN_NUMBER_OF_TRAILERS = "number_of_trailer";
    }

    public static final class ReviewEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_REVIEW)
                .build();

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final String TABLE_NAME = "review";
        public static final String INDEX_NAME = "review_index";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_AUTHOR = "author";

        public static final String COLUMN_REVIEW_CONTENT = "review_content";

    }

    public static final class TrailerEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_TRAILER)
                .build();

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;

        public static final String TABLE_NAME = "trailer";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_KEY_OF_TRAILER = "key_of_trailer";

    }
    public static final class CacheMovieMostPopularEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_CACHE_MOVIE_MOST_POPULAR)
                .build();
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CACHE_MOVIE_MOST_POPULAR;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CACHE_MOVIE_MOST_POPULAR;

        public static final String TABLE_NAME = "cache_movie_most_popular";

        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";

        public static final String COLUMN_MOVIE_POSTER_IMAGE_THUMBNAIL
                = "movie_poster_image_thumbnail";

        public static final String COLUMN_A_PLOT_SYNOPSIS = "a_plot_synopsis";

        public static final String COLUMN_USER_RATING = "user_rating";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_MOVIE_ID = "movie_id";
    }

    public static final class CacheMovieTopRatedEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_CACHE_MOVIE_TOP_RATED)
                .build();

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CACHE_MOVIE_TOP_RATED;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CACHE_MOVIE_TOP_RATED;

        public static final String TABLE_NAME = "cache_movie_top_rated";

        public static final String COLUMN_POSTER_PATH = "poster_path";

        public static final String COLUMN_ORIGINAL_TITLE = "original_title";

        public static final String COLUMN_MOVIE_POSTER_IMAGE_THUMBNAIL
                = "movie_poster_image_thumbnail";

        public static final String COLUMN_A_PLOT_SYNOPSIS = "a_plot_synopsis";

        public static final String COLUMN_USER_RATING = "user_rating";

        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_MOVIE_ID = "movie_id";
    }
}

