package be.pxl.it.network;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.os.AsyncTask;

/**
 * Class that performs a file write in the background.
 * Call execute with two parameters: file name, file content (String)
 * @author Servaas Tilkin
 */
public class FileWriterAsyncTask extends AsyncTask<String, Void, Boolean> {
	private IAsyncCallback<Boolean> resultListener;
	private Context context;

	public FileWriterAsyncTask(IAsyncCallback<Boolean> callback, Context ctx) {
		this.resultListener = callback;
		this.context = ctx;
	}

	public synchronized Context getContext() {
		return context;
	}

	@Override
	protected Boolean doInBackground(String... args) {
		if (args.length < 2) {
			return false;
		}

		String fileName = args[0]; // get filename from params
		String content = args[1]; // get content from params

		BufferedWriter buf_out = null;

		try {
			FileOutputStream io_out = getContext().openFileOutput(fileName, Context.MODE_PRIVATE);
			buf_out = new BufferedWriter(new OutputStreamWriter(io_out));
			buf_out.write(content);
			return true;
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
			return false;
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return false;
		} finally {
			try {
				if (buf_out != null) {
					buf_out.close();
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
