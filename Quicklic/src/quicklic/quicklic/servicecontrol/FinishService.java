package quicklic.quicklic.servicecontrol;

import quicklic.floating.api.FloatingService;
import quicklic.floating.api.R;
import quicklic.quicklic.favorite.QuicklicFavoriteActivity;
import quicklic.quicklic.hardware.QuicklicHardwareActivity;
import quicklic.quicklic.main.QuicklicMainActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class FinishService extends Activity {

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		Intent intent;

		intent = new Intent(FinishService.this, FloatingService.class);
		stopService(intent);

		intent = new Intent(FinishService.this, QuicklicMainActivity.class);
		stopService(intent);

		intent = new Intent(FinishService.this, QuicklicFavoriteActivity.class);
		stopService(intent);

		intent = new Intent(FinishService.this, QuicklicHardwareActivity.class);
		stopService(intent);

		Toast.makeText(getApplicationContext(), R.string.quit_quicklic, Toast.LENGTH_SHORT).show();

		finish();
	}
}
