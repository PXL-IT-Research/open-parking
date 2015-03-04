package be.pxl.itresearch.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.os.AsyncTask;

/**
 * Class that performs a file read in the background
 * Call execute with one parameters: file name
 * @author Servaas Tilkin
 */
public class FileReaderAsyncTask extends AsyncTask<String, Void, String> {
	final private IAsyncCallback<String> resultListener;
	final private Context context;

	public FileReaderAsyncTask(IAsyncCallback<String> callback, Context ctx) {
		this.resultListener = callback;
		this.context = ctx;
	}

	@Override
	protected String doInBackground(String... args) {
		if (args.length < 1) {
			return null;
		}

		final String fileName = args[0]; // get filename from params

		BufferedReader bufIn = null;
		final StringBuilder content = new StringBuilder();

		try {
			final FileInputStream ioIn = context.openFileInput(fileName);
			bufIn = new BufferedReader(new InputStreamReader(ioIn));
			String line = bufIn.readLine();
			while (line != null) {
				content.append(line);
				line = bufIn.readLine();
			}
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (bufIn != null) {
					bufIn.close();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}

		return content.toString();
	}

	@Override
	protected void onPostExecute(String result) {
		if (this.resultListener != null) {
			this.resultListener.onOperationCompleted(result);
		}
	}
}
