package hu.tvarga.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

class GetAsyncTask extends AsyncTask<String, Void, String> {

	private static final String LOG_TAG = GetAsyncTask.class.getSimpleName();
	private final GetMoviesAsyncTaskDelegate delegate;

	GetAsyncTask(GetMoviesAsyncTaskDelegate delegate) {
		this.delegate = delegate;
	}

	@Override
	protected String doInBackground(String... strings) {
		String urlString = strings[0];

		HttpURLConnection urlConnection = null;
		BufferedReader bufferedReader = null;
		InputStreamReader inputStreamReader = null;

		String response = null;

		Uri uri = Uri.parse(urlString).buildUpon().build();
		URL url;
		try {
			url = new URL(uri.toString());
		}
		catch (MalformedURLException e) {
			Log.e(LOG_TAG, "Failed to get URL: " + urlString, e);
			return null;
		}
		try {
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.connect();

			InputStream inputStream = urlConnection.getInputStream();
			StringBuilder sb = new StringBuilder();
			if (inputStream == null) {
				return null;
			}
			inputStreamReader = new InputStreamReader(inputStream);
			bufferedReader = new BufferedReader(inputStreamReader);

			String line;
			while ((line = bufferedReader.readLine()) != null) {
				sb.append(line);
			}

			if (sb.length() == 0) {
				Log.d(LOG_TAG, "Got empty response");
				return null;
			}
			response = sb.toString();
			Log.d(LOG_TAG, "Response: " + response);
		}
		catch (IOException e) {
			Log.e(LOG_TAG, "Error ", e);
			return null;
		}
		finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				}
				catch (IOException e) {
					Log.e(LOG_TAG, "Failed to close bufferedReader", e);
				}
			}
			if (inputStreamReader != null) {
				try {
					inputStreamReader.close();
				}
				catch (IOException e) {
					Log.e(LOG_TAG, "Failed to close inputStreamReader", e);
				}
			}
		}
		return response;
	}

	@Override
	protected void onPostExecute(String s) {
		delegate.onPostExecute(s);
	}

	interface GetMoviesAsyncTaskDelegate {

		void onPostExecute(String result);
	}
}
