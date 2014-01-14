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

import simpleexpr.ExprParser;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Screen for user to use a specific selected formula.
 * 
 * @author sundar
 */
public class FormulaUseActivity extends Activity {
	// formula title
	private TextView titleText;
	// input label
	private TextView inputLabelText;
	private TextView resultLabelText;
	// formula input value
	private EditText formulaInput;

	// label and formula texts from Intent
	private String inputLabel;
	private String resultLabel;
	private String formula;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_formula_use);

		titleText = (TextView) findViewById(R.id.formulaTitle);
		inputLabelText = (TextView) findViewById(R.id.formulaInputLabel);
		formulaInput = (EditText) findViewById(R.id.formulaInput);
		resultLabelText = (TextView) findViewById(R.id.formulaResultLabel);

		// fill formula details from Bundle
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			// make title bold
			String formulaName = extras.getString(FormulaeDbAdapter.KEY_NAME);
			if (formulaName == null) {
				finish();
			}

			this.setTitle(formulaName);

			String formula = extras.getString(FormulaeDbAdapter.KEY_FORMULA);
			titleText.setText(Html.fromHtml("<b>" + formula + "</b>"));

			this.inputLabel = extras.getString(FormulaeDbAdapter.KEY_INPUT);
			inputLabelText.setText(inputLabel);

			this.resultLabel = extras.getString(FormulaeDbAdapter.KEY_OUTPUT);
			resultLabelText.setText(resultLabel);
			this.formula = extras.getString(FormulaeDbAdapter.KEY_FORMULA);
		}

		// on 'Done' key pressed, evaluate formula and update result
		formulaInput
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						FormulaUseActivity.this.onEval();
						InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(
								formulaInput.getWindowToken(), 0);
						return true;
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.formula_use, menu);
		return true;
	}

	// Utility for other classes to create an Intent for this activity
	static Intent makeIntent(Context ctx, long formulaId, String name,
			String input, String output, String formula) {
		Intent i = new Intent(ctx, FormulaUseActivity.class);
		i.putExtra(FormulaeDbAdapter.KEY_ROWID, formulaId);
		i.putExtra(FormulaeDbAdapter.KEY_NAME, name);
		i.putExtra(FormulaeDbAdapter.KEY_INPUT, input);
		i.putExtra(FormulaeDbAdapter.KEY_OUTPUT, output);
		i.putExtra(FormulaeDbAdapter.KEY_FORMULA, formula);
		return i;
	}

	// Evaluate formula with input specified and update result
	private void onEval() {
		// evaluate formula code but before that set input variable value
		String code = inputLabel + " = " + formulaInput.getText().toString()
				+ ", " + formula;
		try {
			String res = Double.toString(ExprParser.eval(code));
			resultLabelText.setText(resultLabel + " : " + res);
		} catch (Exception e) {
			// something went wrong! show error!
			String msg = e.getMessage();
			if (msg == null) {
				msg = e.toString();
			}
			resultLabelText.setText(Html.fromHtml("<font color='red'>" + msg
					+ "</font>"));
		}
	}
}
