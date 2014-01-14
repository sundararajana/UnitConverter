/*
 * Copyright (C) 2014 Sundararajan Athijegannathan
 * 
 * This file is part of UnitConverter.
 * 
 * UnitConverter is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UnitConverter is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UnitConverter.  If not, see <http://www.gnu.org/licenses/>.
 */

package in.sundar.unitconverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Database helper for formulae storage. Table creation and CRUD operation
 * helpers.
 * 
 * @author sundar
 */
public class FormulaeDbAdapter {
	// initial formulae file. Initial formulae are
	// loaded from this 'assets' file.
	private static final String INIT_FORMULAE = "init_formulae";

	// database name, version and table name
	private static final String DATABASE_NAME = "data";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_TABLE = "formulae";

	// column names
	// Name of the formula
	public static final String KEY_NAME = "name";
	// name of the input
	public static final String KEY_INPUT = "input";
	// name of the output
	public static final String KEY_OUTPUT = "output";
	// formula (expression) to convert input to output
	public static final String KEY_FORMULA = "formula";

	// primary key
	public static final String KEY_ROWID = "_id";

	// column names in an array
	public static final String[] DATABASE_COLUMNS = new String[] { KEY_ROWID,
			KEY_NAME, KEY_INPUT, KEY_OUTPUT, KEY_FORMULA };

	// for logging
	private static final String TAG = "FormulaeDbAdapter";

	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;

	// Database creation SQL
	private static final String DATABASE_CREATE = "create table formulae (_id integer primary key autoincrement, "
			+ "name text not null, input text not null, output text not null, formula text not null);";

	// current app context
	private final Context ctx;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		private final Context ctx;
		private SQLiteDatabase db;

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			this.ctx = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
			this.db = db;
			try {
				loadInitialData();
			} catch (IOException ioExp) {
				throw new RuntimeException(ioExp);
			}
		}

		// load initial formulae from an 'asserts' file
		// Colon separated column texts.
		private void loadInitialData() throws IOException {
			InputStream stream = ctx.getAssets().open(INIT_FORMULAE);
			if (stream == null) {
				return;
			}

			BufferedReader buffer = new BufferedReader(new InputStreamReader(
					stream));
			String line = "";
			String str1 = "INSERT INTO " + DATABASE_TABLE
					+ " (name, input, output, formula) values(";
			String str2 = ");";
			db.beginTransaction();
			while ((line = buffer.readLine()) != null) {
				// skip comment lines
				if (line.charAt(0) == '#') {
					continue;
				}

				StringBuilder sb = new StringBuilder(str1);
				String[] str = line.split(":");
				sb.append("'" + str[0] + "','");
				sb.append(str[1] + "','");
				sb.append(str[2] + "','");
				sb.append(str[3] + "'");
				sb.append(str2);
				Log.d(TAG, sb.toString());
				db.execSQL(sb.toString());
			}
			db.setTransactionSuccessful();
			db.endTransaction();
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS formulae");
			onCreate(db);
		}
	}

	public FormulaeDbAdapter(Context ctx) {
		this.ctx = ctx;
	}

	public FormulaeDbAdapter open() throws SQLException {
		dbHelper = new DatabaseHelper(ctx);
		db = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}

	public long createFormula(String name, String input, String output,
			String formula) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_NAME, name);
		initialValues.put(KEY_INPUT, input);
		initialValues.put(KEY_OUTPUT, output);
		initialValues.put(KEY_FORMULA, formula);

		return db.insert(DATABASE_TABLE, null, initialValues);
	}

	public boolean deleteFormula(long rowId) {
		return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}

	public Cursor fetchAllFormulae() {
		return db.query(DATABASE_TABLE, DATABASE_COLUMNS, null, null, null,
				null, null);
	}

	public Cursor fetchFormula(long rowId) throws SQLException {
		Cursor cursor = db.query(true, DATABASE_TABLE, DATABASE_COLUMNS,
				KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	public boolean updateFormula(long rowId, String name, String input,
			String output, String formula) {
		ContentValues args = new ContentValues();
		args.put(KEY_NAME, name);
		args.put(KEY_INPUT, input);
		args.put(KEY_OUTPUT, output);
		args.put(KEY_FORMULA, formula);

		return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}
}
