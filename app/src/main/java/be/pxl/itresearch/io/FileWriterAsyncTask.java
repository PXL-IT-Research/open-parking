package be.pxl.itresearch.io;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.os.AsyncTask;

/**
 * Class that performs a file write in the background. Call execute with two parameters: file name, file content
 * (String)
 * 
 * @author Servaas Tilkin
 */
public class FileWriterAsyncTask extends AsyncTask<String, Void, Boolean> {
	private final IAsyncCallback<Boolean> resultListener;
	private final Context context;
	public static final int MIN_ARGS = 2;

	public FileWriterAsyncTask(IAsyncCallback<Boolean> callback, Context ctx) {
		this.resultListener = callback;
		this.context = ctx;
	}

	public synchronized Context getContext() {
		return context;
	}

	@Override
	protected Boolean doInBackground(String... args) {
		if (args.length < MIN_ARGS) {
			return false;
		}

		final String fileName = args[0]; // get filename from params
		final String content = args[1]; // get content from params

		BufferedWriter bufOut = null;

		try {
			final FileOutputStream ioOut = getContext().openFileOutput(fileName,
					Context.MODE_PRIVATE);
			bufOut = new BufferedWriter(new OutputStreamWriter(ioOut));
			bufOut.write(content);
			return true;
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
			return false;
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return false;
		} finally {
			try {
				if (bufOut != null) {
					bufOut.close();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (this.resultListener != null) {
			this.resultListener.onOperationCompleted(result);
		}
	}
}
