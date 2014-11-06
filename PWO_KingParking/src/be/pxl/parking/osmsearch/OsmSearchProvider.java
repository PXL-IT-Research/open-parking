package be.pxl.parking.osmsearch;

import java.util.List;
import java.util.Locale;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MatrixCursor.RowBuilder;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * This Content Provider uses network resources, so it should ALWAYS be called from the background (using CursorLoader,
 * AsyncTask, or other).
 * 
 * @author stilkin
 * 
 */
public class OsmSearchProvider extends ContentProvider {
	public static final String AUTHORITY = "be.pxl.parking.osmsearch";
	public static final String TABLE_NAME = "results";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

	private final String[] defaultColumns = { BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1,
			SearchManager.SUGGEST_COLUMN_TEXT_2, SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA };

	@Override
	public boolean onCreate() {
		return false;
	}

	/**
	 * Handle query requests from clients
	 * 
	 * @param uri
	 *            The default behavior is for system to pass this URI and append it with the query text. For example:
	 *            content://your.authority/optional.suggest.path/SUGGEST_URI_PATH_QUERY/puppies
	 * @param projection
	 *            Always null
	 * @param selection
	 *            Always null
	 * @param selectionArgs
	 *            Always null
	 * @param sortOrder
	 *            Always null
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {

		MatrixCursor mxCur = new MatrixCursor(defaultColumns);

		// This returns the last segment of the Uri, which should be the query text entered by the user.
		String query = uri.getLastPathSegment();

		if (query != null) {
			List<OsmResult> resultList = OsmSearchUtils.searchOsm(query);
			RowBuilder row;
			for (OsmResult result : resultList) {
				row = mxCur.newRow();
				row.add(result.getOsm_id());
				row.add(result.getDisplay_name());
				row.add(result.getType().toUpperCase(Locale.ENGLISH));
				row.add(OsmSearchUtils.convertOsmResultToJson(result));
			}
		}

		return mxCur;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		throw new UnsupportedOperationException("Web resource is read only");
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException("Web resource is read only");
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException("Web resource is read only");
	}

	@Override
	public String getType(Uri uri) {
		// default
		return "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + TABLE_NAME;
	}
}
