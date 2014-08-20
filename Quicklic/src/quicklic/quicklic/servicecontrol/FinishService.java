package quicklic.quicklic.servicecontrol;

import quicklic.floating.api.FloatingService;
import quicklic.floating.api.R;
import quicklic.quicklic.favorite.QuicklicFavoriteService;
import quicklic.quicklic.hardware.QuicklicHardwareService;
import quicklic.quicklic.main.QuicklicMainService;
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

		intent = new Intent(FinishService.this, QuicklicMainService.class);
		stopService(intent);

		intent = new Intent(FinishService.this, QuicklicFavoriteService.class);
		stopService(intent);

		intent = new Intent(FinishService.this, QuicklicHardwareService.class);
		stopService(intent);

		Toast.makeText(getApplicationContext(), R.string.quit_quicklic, Toast.LENGTH_SHORT).show();

		finish();
	}
}
