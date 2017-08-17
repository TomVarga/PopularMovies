package hu.tvarga.popularmovies.dataaccess.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static hu.tvarga.popularmovies.dataaccess.database.MovieContract.MovieEntry;

class MoviesDbHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "movies.db";

	private static final int DATABASE_VERSION = 4;
	private static final String TEXT_NOT_NULL = " TEXT NOT NULL, ";

	MoviesDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		final String sqlCreateMovieTable =

				"CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +

						MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

						MovieEntry.COLUMN_RELEASE_DATE + TEXT_NOT_NULL +
						MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
						MovieEntry.COLUMN_VOTE_AVERAGE + TEXT_NOT_NULL +
						MovieEntry.COLUMN_POSTER_PATH + TEXT_NOT_NULL + MovieEntry.COLUMN_OVERVIEW +
						TEXT_NOT_NULL + MovieEntry.COLUMN_ORIGINAL_TITLE + TEXT_NOT_NULL +
						MovieEntry.COLUMN_FAVORITE + " BOOLEAN NOT NULL, " +

						" UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";
		sqLiteDatabase.execSQL(sqlCreateMovieTable);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
		onCreate(sqLiteDatabase);
	}
}
