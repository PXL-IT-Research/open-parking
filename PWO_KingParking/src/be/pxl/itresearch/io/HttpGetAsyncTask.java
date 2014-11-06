package be.pxl.itresearch.io;

import android.os.AsyncTask;

/**
 * Class that performs a GET request in the background
 * 
 * @author Tristan Fransen
 * @author Servaas Tilkin
 */
public class HttpGetAsyncTask extends AsyncTask<String, Void, String> {
	private IAsyncCallback<String> resultListener;

	public HttpGetAsyncTask(IAsyncCallback<String> uiClass) {
		this.resultListener = uiClass;
	}

	@Override
	protected String doInBackground(String... args) {
		if (args.length < 1) {
			return null;
		}

		String url = args[0]; // get url from params

		return HttpIO.getHttpGetRequestContent(url);
	}

	@Override
	protected void onPostExecute(String result) {
		if (this.resultListener != null) {
			this.resultListener.onOperationCompleted(result);
		}
	}
}
