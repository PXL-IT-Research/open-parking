package be.pxl.it.network;

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
	private IAsyncCallback<String> resultListener;
	private Context context;

	public FileReaderAsyncTask(IAsyncCallback<String> callback, Context ctx) {
		this.resultListener = callback;
		this.context = ctx;
	}

	private synchronized Context getContext() {
		return context;
	}

	@Override
	protected String doInBackground(String... args) {
		if (args.length < 1) {
			return null;
		}

		String fileName = args[0]; // get filename from params

		BufferedReader buf_in = null;
		StringBuilder content = new StringBuilder();

		try {
			FileInputStream io_in = getContext().openFileInput(fileName);
			buf_in = new BufferedReader(new InputStreamReader(io_in));
			String line = buf_in.readLine();
			while (line != null) {
				content.append(line);
				line = buf_in.readLine();
			}
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (buf_in != null) {
					buf_in.close();
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
