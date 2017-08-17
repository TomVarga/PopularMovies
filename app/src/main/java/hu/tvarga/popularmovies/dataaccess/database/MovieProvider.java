package hu.tvarga.popularmovies.dataaccess.database;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MovieProvider extends ContentProvider {

	public static final String FILTER = " = ? ";
	public static final int CODE_MOVIE = 100;
	public static final int CODE_MOVIE_WITH_ID = 101;

	private static final UriMatcher sUriMatcher = buildUriMatcher();

	private MoviesDbHelper openHelper;

	public static UriMatcher buildUriMatcher() {

		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = MovieContract.CONTENT_AUTHORITY;

		matcher.addURI(authority, MovieContract.PATH_MOVIE, CODE_MOVIE);
		matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", CODE_MOVIE_WITH_ID);

		return matcher;
	}

	@Override
	public boolean onCreate() {
		openHelper = new MoviesDbHelper(getContext());
		return true;
	}

	@Override
	public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
		final SQLiteDatabase db = openHelper.getWritableDatabase();

		int i = sUriMatcher.match(uri);
		if (i == CODE_MOVIE) {
			db.beginTransaction();
			int rowsInserted = 0;
			try {
				for (ContentValues value : values) {
					long id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
					if (id != -1) {
						rowsInserted++;
					}
				}
				db.setTransactionSuccessful();
			}
			finally {
				db.endTransaction();
			}

			if (rowsInserted > 0) {
				Context context = getContext();
				if (context != null) {
					context.getContentResolver().notifyChange(uri, null);
				}
			}

			return rowsInserted;
		}
		else {
			return super.bulkInsert(uri, values);
		}
	}

	@Override
	public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
			@Nullable String selection, @Nullable String[] selectionArgs) {
		final SQLiteDatabase db = openHelper.getWritableDatabase();

		int i = sUriMatcher.match(uri);
		if (i == CODE_MOVIE_WITH_ID) {
			db.beginTransaction();
			int rowsUpdated = 0;

			String movieId = uri.getLastPathSegment();

			String[] selectionArguments = new String[]{movieId};

			try {
				long id = db.update(MovieContract.MovieEntry.TABLE_NAME, contentValues,
						MovieContract.MovieEntry.COLUMN_MOVIE_ID + FILTER, selectionArguments);
				if (id != -1) {
					rowsUpdated++;
				}
				db.setTransactionSuccessful();
			}
			finally {
				db.endTransaction();
			}

			if (rowsUpdated > 0) {
				Context context = getContext();
				if (context != null) {
					context.getContentResolver().notifyChange(uri, null);
				}
			}

			return rowsUpdated;
		}
		else {
			return 0;
		}
	}

	@Nullable
	@Override
	public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
			@Nullable String[] selectionArgs, @Nullable String sortOrder) {
		Cursor cursor;

		switch (sUriMatcher.match(uri)) {

			case CODE_MOVIE_WITH_ID:

				String id = uri.getLastPathSegment();

				String[] selectionArguments = new String[]{id};

				cursor = openHelper.getReadableDatabase().query(MovieContract.MovieEntry.TABLE_NAME,
						projection, MovieContract.MovieEntry.COLUMN_MOVIE_ID + FILTER,
						selectionArguments, null, null, sortOrder);

				break;

			case CODE_MOVIE:
				cursor = openHelper.getReadableDatabase().query(MovieContract.MovieEntry.TABLE_NAME,
						projection, selection, selectionArgs, null, null, sortOrder);

				break;

			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}

		Context context = getContext();
		if (context != null) {
			cursor.setNotificationUri(context.getContentResolver(), uri);
		}
		return cursor;
	}

	@Nullable
	@Override
	public String getType(@NonNull Uri uri) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Nullable
	@Override
	public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
		final SQLiteDatabase db = openHelper.getWritableDatabase();

		int i = sUriMatcher.match(uri);
		if (i == CODE_MOVIE) {
			db.beginTransaction();
			int rowsInserted = 0;
			try {
				long id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues);
				if (id != -1) {
					rowsInserted++;
				}
				db.setTransactionSuccessful();
			}
			finally {
				db.endTransaction();
			}

			if (rowsInserted > 0) {
				Context context = getContext();
				if (context != null) {
					context.getContentResolver().notifyChange(uri, null);
				}
			}
		}
		return uri;
	}

	@Override
	public int delete(@NonNull Uri uri, @Nullable String selection,
			@Nullable String[] selectionArgs) {
		int numRowsDeleted;

		String selectionOfTransaction = selection;

		if (null == selection) {
			selectionOfTransaction = "1";
		}

		switch (sUriMatcher.match(uri)) {

			case CODE_MOVIE:
				numRowsDeleted = openHelper.getWritableDatabase().delete(
						MovieContract.MovieEntry.TABLE_NAME, selectionOfTransaction, selectionArgs);

				break;

			case CODE_MOVIE_WITH_ID:
				String id = uri.getLastPathSegment();
				String[] selectionArguments = new String[]{id};
				numRowsDeleted = openHelper.getWritableDatabase().delete(
						MovieContract.MovieEntry.TABLE_NAME,
						MovieContract.MovieEntry.COLUMN_MOVIE_ID + FILTER, selectionArguments);
				break;

			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}

		if (numRowsDeleted != 0) {
			Context context = getContext();
			if (context != null) {
				context.getContentResolver().notifyChange(uri, null);
			}
		}

		return numRowsDeleted;
	}

	@Override
	@TargetApi(11)
	public void shutdown() {
		openHelper.close();
		super.shutdown();
	}
}
