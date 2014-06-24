package quicklic.quicklic.test;

import quicklic.floating.api.FloatingService;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class FinishService extends Activity
{
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		Intent intent = new Intent(FinishService.this, FloatingService.class);
		stopService(intent);

		Toast.makeText(getApplicationContext(), "Quicklic을 종료하였습니다.", Toast.LENGTH_SHORT).show();

		finish();
	}
}
