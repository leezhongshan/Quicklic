package quicklic.quicklic.test;

import quicklic.floating.api.FloatingService;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class StartService extends Activity {

	private SettingFloatingInterface testingFunction;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		testingFunction = new SettingFloatingInterface();
		Intent intent = new Intent(StartService.this, FloatingService.class);
		intent.putExtra("interface", testingFunction);
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
