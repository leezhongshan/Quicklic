package quicklic.quicklic.util;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class DeviceMetricQuicklic extends Service {

	private WindowManager windowManager;
	private WindowManager.LayoutParams layoutParams;
	private Display windowDisplay;
	private int deviceWidth;
	private int deviceHeight;

	@Override
	public IBinder onBind( Intent arg0 )
	{
		return null;
	}

	@Override
	public void onCreate()
	{
		displayMetrics();
		createLayoutParams();
		super.onCreate();
	}


	@Override
	public void onConfigurationChanged( Configuration newConfig )
	{
		displayMetrics();
		createLayoutParams();
		super.onConfigurationChanged(newConfig);
	}

	protected int getOrientation()
	{
		return windowDisplay.getRotation();
	}


	protected int getDeviceWidth()
	{
		return deviceWidth;
	}


	protected int getDeviceHeight()
	{
		return deviceHeight;
	}


	protected WindowManager getUWindowManager()
	{
		return windowManager;
	}


	protected void displayMetrics()
	{
		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

		windowDisplay = windowManager.getDefaultDisplay();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		windowDisplay.getMetrics(displayMetrics);

		deviceWidth = displayMetrics.widthPixels;
		deviceHeight = displayMetrics.heightPixels;
	}

	private void createLayoutParams()
	{
		layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
				PixelFormat.RGBA_8888);

		layoutParams.windowAnimations = android.R.style.Animation_Dialog;
		layoutParams.width = deviceWidth;
		layoutParams.height = deviceHeight;
	}

	public WindowManager.LayoutParams getLayoutParams()
	{
		return layoutParams;
	}

	public WindowManager getWindowManager()
	{
		return windowManager;
	}
}
