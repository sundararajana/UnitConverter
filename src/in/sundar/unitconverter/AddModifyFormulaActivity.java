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
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Activity to create a new user defined conversion formula or modify existing
 * formula.
 * 
 * @author sundar
 */
public class AddModifyFormulaActivity extends Activity {
	// formula name, input, output and script edit controls
	private EditText formulaNameText;
	private EditText inputText;
	private EditText outputText;
	private EditText formulaText;

	// Database helper
	private FormulaeDbAdapter dbHelper;

	// used for formula update mode
	private long formulaId = -1L;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_formula);

		dbHelper = new FormulaeDbAdapter(this);
		dbHelper.open();

		formulaNameText = (EditText) findViewById(R.id.newFormulaName);
		inputText = (EditText) findViewById(R.id.newFormulaInputName);
		outputText = (EditText) findViewById(R.id.newFormulaOutputName);
		formulaText = (EditText) findViewById(R.id.newFormulaCode);

		Bundle extras = getIntent().getExtras();
		// check if formula id is passed, if so it is modify mode
		if (extras != null && extras.containsKey(FormulaeDbAdapter.KEY_ROWID)) {
			// fill out details of existing formula
			fillExistingFormula(extras);
		}

		// on "Done" add a new formula or modify existing formula
		formulaText
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						AddModifyFormulaActivity.this.addModifyFormula();
						return true;
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_formula, menu);
		return true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		dbHelper.close();
	}

	// Utility for other classes to create an Intent for this activity
	static Intent makeIntent(Context ctx, long formulaId, String name,
			String input, String output, String formula) {
		Intent i = new Intent(ctx, AddModifyFormulaActivity.class);
		i.putExtra(FormulaeDbAdapter.KEY_ROWID, formulaId);
		i.putExtra(FormulaeDbAdapter.KEY_NAME, name);
		i.putExtra(FormulaeDbAdapter.KEY_INPUT, input);
		i.putExtra(FormulaeDbAdapter.KEY_OUTPUT, output);
		i.putExtra(FormulaeDbAdapter.KEY_FORMULA, formula);
		return i;
	}

	// fill out existing formula details from Bundle
	private void fillExistingFormula(Bundle extras) {
		formulaId = extras.getLong(FormulaeDbAdapter.KEY_ROWID);
		if (formulaId != -1) {
			String desc = extras.getString(FormulaeDbAdapter.KEY_NAME);
			formulaNameText.setText(desc);

			String input = extras.getString(FormulaeDbAdapter.KEY_INPUT);
			inputText.setText(input);

			String output = extras.getString(FormulaeDbAdapter.KEY_OUTPUT);
			outputText.setText(output);

			String forumla = extras.getString(FormulaeDbAdapter.KEY_FORMULA);
			formulaText.setText(forumla);

			this.setTitle(R.string.action_edit_formula);
		} else {
			formulaId = -1L;
		}
	}

	// add a new formula or modify existing formula
	private void addModifyFormula() {
		String desc = formulaNameText.getText().toString();
		String input = inputText.getText().toString();
		String output = outputText.getText().toString();
		String formula = formulaText.getText().toString();
		if (formulaId != -1L) {
			dbHelper.updateFormula(formulaId, desc, input, output, formula);
		} else {
			dbHelper.createFormula(desc, input, output, formula);
		}

		setResult(RESULT_OK);
		finish();
	}
}
