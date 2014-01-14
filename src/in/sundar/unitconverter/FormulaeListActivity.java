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

import android.os.Bundle;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

/**
 * Initial screen of the application. Shows the list of available formulae as a
 * list. On selection a specific formula can be used by user.
 * 
 * @author sundar
 */
public class FormulaeListActivity extends ListActivity {
	// result code for other activities started from here
	private static final int FORMULA_ADD = 1;
	private static final int FORMULA_EDIT = 2;

	// context menu ids
	private static final int EDIT_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;

	// database helper
	private FormulaeDbAdapter dbHelper;
	private Cursor formulaeCursor;
	private SimpleCursorAdapter formulaeCursorAdapter;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_formulae_list);

		dbHelper = new FormulaeDbAdapter(this);
		dbHelper.open();

		// fill in initial formulae from DB
		fillData();

		// Show only formula name column
		String[] from = new String[] { FormulaeDbAdapter.KEY_NAME };
		int[] to = new int[] { R.id.formulaRowText };

		// Create a simple cursor adapter and set it to display
		formulaeCursorAdapter = new SimpleCursorAdapter(this,
				R.layout.formulae_row, formulaeCursor, from, to);
		setListAdapter(formulaeCursorAdapter);

		registerForContextMenu(getListView());
	}

	// menu options
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.formulae_list, menu);
		return true;
	}

	// act on menu option selected
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Expression evaluator screen
		case R.id.action_expreval: {
			Intent i = new Intent(this, ExprEvalActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(i);
		}
			break;
		// New user defined formula addition screen
		case R.id.action_add_formula: {
			Intent i = new Intent(this, AddModifyFormulaActivity.class);
			startActivityForResult(i, FORMULA_ADD);
		}
		}

		return super.onOptionsItemSelected(item);
	}

	// context menu to delete a formula
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, EDIT_ID, Menu.NONE, R.string.action_edit_formula);
		menu.add(0, DELETE_ID, Menu.NONE, R.string.action_delete_formula);
	}

	// act on context menu option selection
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// edit formula context menu
		case EDIT_ID: {
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			startFormulaEditActivity(info.id, info.position);
			return true;
		}

		// delete formula context menu
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			confirmAndDeleteFormula(info.id);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	// selecting a formula list item
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// goto the selected formula's screen
		Cursor c = formulaeCursor;
		c.moveToPosition(position);
		Intent i = FormulaUseActivity.makeIntent(this, id, c.getString(c
				.getColumnIndexOrThrow(FormulaeDbAdapter.KEY_NAME)),
				c.getString(c
						.getColumnIndexOrThrow(FormulaeDbAdapter.KEY_INPUT)),
				c.getString(c
						.getColumnIndexOrThrow(FormulaeDbAdapter.KEY_OUTPUT)),
				c.getString(c
						.getColumnIndexOrThrow(FormulaeDbAdapter.KEY_FORMULA)));
		startActivity(i);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		fillData();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		dbHelper.close();
	}

	private void startFormulaEditActivity(long id, int position) {
		// goto the selected formula's edit screen
		Cursor c = formulaeCursor;
		c.moveToPosition(position);
		Intent i = AddModifyFormulaActivity.makeIntent(this, id, c.getString(c
				.getColumnIndexOrThrow(FormulaeDbAdapter.KEY_NAME)),
				c.getString(c
						.getColumnIndexOrThrow(FormulaeDbAdapter.KEY_INPUT)),
				c.getString(c
						.getColumnIndexOrThrow(FormulaeDbAdapter.KEY_OUTPUT)),
				c.getString(c
						.getColumnIndexOrThrow(FormulaeDbAdapter.KEY_FORMULA)));
		startActivityForResult(i, FORMULA_EDIT);
	}

	// (re-)fills formulae list from database
	@SuppressWarnings("deprecation")
	private void fillData() {
		// Get all the formulae from the database to make item list
		formulaeCursor = dbHelper.fetchAllFormulae();
		startManagingCursor(formulaeCursor);

		if (formulaeCursorAdapter != null) {
			Cursor oldCursor = formulaeCursorAdapter.swapCursor(formulaeCursor);
			stopManagingCursor(oldCursor);
			oldCursor.close();
		}
	}

	// actual formula delete and UI update
	private void deleteFormula(long id) {
		boolean deleted = dbHelper.deleteFormula(id);
		if (deleted) {
			fillData();
		}
	}

	// show a dialog box to confirm and then delete
	private void confirmAndDeleteFormula(final long formulaId) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		// set title
		alertDialogBuilder.setTitle("Do you really want to delete?");

		// set dialog message
		alertDialogBuilder
				.setMessage("Click yes to delete!")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								deleteFormula(formulaId);
								dialog.cancel();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}
}
