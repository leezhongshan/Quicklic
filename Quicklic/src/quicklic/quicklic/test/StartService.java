package quicklic.quicklic.test;

import quicklic.floating.api.FloatingService;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class StartService extends Activity
{

	TestingFunction testingFunction = new TestingFunction();

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		Intent intent = new Intent(StartService.this, FloatingService.class);
		intent.putExtra("push", testingFunction);
		startService(intent);
		overridePendingTransition(0, 0);
		finish();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		finish();
	}

}
