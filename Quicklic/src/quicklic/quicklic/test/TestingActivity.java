package quicklic.quicklic.test;

import quicklic.floating.api.FloatingServices;
import quicklic.floating.api.R;
import quicklic.quicklic.quicklic.QuicklicScrollActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TestingActivity extends Activity
{

	TestingFunction testingFunction = new TestingFunction();

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button launch = (Button) findViewById(R.id.button1);
		Button stop = (Button) findViewById(R.id.button2);

		launch.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick( View v )
			{
				Intent intent = new Intent(TestingActivity.this, FloatingServices.class);
				intent.putExtra("push", testingFunction);
				startService(intent);
				finish();
			}
		});

		stop.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick( View v )
			{
				stopService(new Intent(TestingActivity.this, FloatingServices.class));
			}
		});

	}

	@Override
	protected void onPause()
	{
		super.onPause();
		finish();
	}

}
