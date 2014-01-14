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

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import simpleexpr.ExprParser;

/**
 * This activity is just a test for expression evaluator. This can be exercised
 * from "main" screen via menu.
 * 
 * @author sundar
 */
public class ExprEvalActivity extends Activity {

	// storage for variables
	private Map<String, Double> vars;
	// evaluated script code
	private EditText evalText;
	// label for result
	private TextView answerText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_expreval);
		vars = new HashMap<String, Double>();
		evalText = (EditText) findViewById(R.id.exprEvalText);
		answerText = (TextView) findViewById(R.id.exprEvalAnswerText);

		// on pressing "Done" evaluate script and update result
		evalText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				ExprEvalActivity.this.onEval();
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(evalText.getWindowToken(), 0);
				return true;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.expreval, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// going to formula list screen
		case R.id.action_formula_list: {
			Intent i = new Intent(this, FormulaeListActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(i);
		}
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	// evaluate expression and update result
	private void onEval() {
		String code = evalText.getText().toString();
		try {
			String ans = Double.toString(ExprParser.eval(code, vars));
			answerText.setText(ans);
		} catch (Exception e) {
			String msg = e.getMessage();
			if (msg == null) {
				msg = e.toString();
			}
			answerText.setText(Html.fromHtml("<font color='red'>" + msg
					+ "</font>"));
		}
	}
}