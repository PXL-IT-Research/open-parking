package be.pxl.parking.osmsearch;

import java.util.List;

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

	public static class OsmResultContract implements BaseColumns {
		public static final String COL_LAT = "lat";
		public static final String COL_LON = "lon";
		public static final String COL_NAME = "display_name";
		public static final String COL_TYPE = "type";
		public static final String COL_CLASS = "class";
		public static final String COL_IMPORTANCE = "importance";
	}

	private final String[] defaultColumns = { OsmResultContract._ID, OsmResultContract.COL_LAT,
			OsmResultContract.COL_LON, OsmResultContract.COL_NAME, OsmResultContract.COL_TYPE,
			OsmResultContract.COL_CLASS, OsmResultContract.COL_IMPORTANCE };

	@Override
	public boolean onCreate() {
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {

		if (projection == null || projection.length < 1) {
			// make sure we have some columns
			projection = defaultColumns;
		}

		MatrixCursor mxCur = new MatrixCursor(projection);
		
		// This returns the last segment of the Uri, which should be the query text entered by the user.
		String query = uri.getLastPathSegment();

		if (query != null) {
			List<OsmResult> resultList = OsmSearchUtils.searchOsm(query);
			RowBuilder row;
			for (OsmResult result : resultList) {
				row = mxCur.newRow();
				for (String column : projection) {
					if (OsmResultContract._ID.equals(column)) {
						row.add(result.getOsm_id());
					}
					if (OsmResultContract.COL_LAT.equals(column)) {
						row.add(result.getLat());
					}
					if (OsmResultContract.COL_LON.equals(column)) {
						row.add(result.getLon());
					}
					if (OsmResultContract.COL_NAME.equals(column)) {
						row.add(result.getDisplay_name());
					}
					if (OsmResultContract.COL_TYPE.equals(column)) {
						row.add(result.getType());
					}
					if (OsmResultContract.COL_CLASS.equals(column)) {
						row.add(result.getClass_name());
					}
					if (OsmResultContract.COL_IMPORTANCE.equals(column)) {
						row.add(result.getImportance());
					}
				}
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
