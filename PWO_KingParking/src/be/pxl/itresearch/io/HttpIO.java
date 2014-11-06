package be.pxl.itresearch.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * 
 * @author Servaas Tilkin
 * @author Tristan Fransen
 */
public class HttpIO {

	/**
	 * Returns the content of the response to a http request.
	 * 
	 * @param url
	 * @return
	 */
	public static String getHttpGetRequestContent(String url) {
		BufferedReader reader = null;
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();

		try {
			HttpGet httpGet = new HttpGet(url);
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity msgEntity = response.getEntity();
				InputStream content = msgEntity.getContent();

				reader = new BufferedReader(new InputStreamReader(content));

				String line = reader.readLine();
				while (line != null) {
					builder.append(line);
					line = reader.readLine();
				}
			}
		} catch (ClientProtocolException cpe) {
			cpe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (IllegalArgumentException iae) {
			iae.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}

		return builder.toString();
	}

}
