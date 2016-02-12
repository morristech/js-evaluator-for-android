package com.evgenii.jsevaluatortests;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.evgenii.jsevaluator.JsEvaluator;
import com.evgenii.jsevaluator.interfaces.JsCallback;

public class EvaluateJsStringActivity extends Activity {
	JsEvaluator mJsEvaluator;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_evaluate_js_string);

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		mJsEvaluator = new JsEvaluator(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	public void onEvaluateClicked(View view) {
        evaluateOnUiThread();
        blockUiActivityAndEvaluate();
	}

    private void blockUiActivityAndEvaluate() {
        JsEvaluator jsEvaluator = new JsEvaluator(this);
        String resultValue = jsEvaluator.blockUIThreadAndEvaluate(5_000, getEditText().getText().toString());
        final TextView jsResultTextView = (TextView) findViewById(R.id.js_result_block_ui_thread_text_view);
        jsResultTextView.setText(String.format("Block UI thread result: %s",
                resultValue));
    }

    private void evaluateOnUiThread() {
        mJsEvaluator.evaluateAndRespondOnUiThread(getEditText().getText().toString(), new JsCallback() {
            @Override
            public void onResult(final String resultValue) {
                final TextView jsResultTextView = (TextView) findViewById(R.id.js_result_ui_thread_text_view);
                jsResultTextView.setText(String.format("UI thread result: %s",
                        resultValue));

                evaluateInBackgroundThread();
            }
        });
    }

    private void evaluateInBackgroundThread() {
        // Evaluate on background thread
        mJsEvaluator.evaluateAndRespondOnBackgroundThread(getEditText().getText().toString(), new JsCallback() {
            @Override
            public void onResult(final String resultValue) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        final TextView jsResultTextView = (TextView) findViewById(R.id.js_result_background_thread_text_view);
                        jsResultTextView.setText(String.format("Background thread result: %s",
                                resultValue));
                    }
                });
            }
        });
    }

    private EditText getEditText() {
        return (EditText) findViewById(R.id.edit_java_script);
    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
