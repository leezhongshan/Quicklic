package quicklic.quicklic.util;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class DeviceMetricActivity extends Activity {

	private WindowManager windowManager;
	private Display windowDisplay;
	private int deviceWidth;
	private int deviceHeight;

	@Override
	public void setContentView( int layoutResID )
	{
		displayMetrics();

		getWindow().setFormat(PixelFormat.RGBA_8888);
		getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
		getWindow().setWindowAnimations(android.R.style.Animation_Dialog);

		super.setContentView(layoutResID);
	}

	protected int getOrientation()
	{
		return windowDisplay.getRotation();
	}

	/**
	 * @함수명 : getDeviceWidth
	 * @매개변수 :
	 * @반환 : int
	 * @기능(역할) : device 가로길이 반환
	 * @작성자 : THYang
	 * @작성일 : 2014. 5. 21.
	 */
	protected int getDeviceWidth()
	{
		return deviceWidth;
	}

	/**
	 * @함수명 : getDeviceHeight
	 * @매개변수 :
	 * @반환 : int
	 * @기능(역할) : device 세로길이 반환
	 * @작성자 : THYang
	 * @작성일 : 2014. 5. 21.
	 */
	protected int getDeviceHeight()
	{
		return deviceHeight;
	}

	/**
	 * @함수명 : getUWindowManager
	 * @매개변수 :
	 * @반환 : WindowManager
	 * @기능(역할) : windowManager 객체 가져오기
	 * @작성자 : THYang
	 * @작성일 : 2014. 5. 29.
	 */
	protected WindowManager getUWindowManager()
	{
		return windowManager;
	}

	/**
	 * @함수명 : displayMetrics
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : 사용자의 Display 사이즈 정보 가져오기
	 * @작성자 : THYang
	 * @작성일 : 2014. 5. 5.
	 */
	protected void displayMetrics()
	{
		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

		windowDisplay = windowManager.getDefaultDisplay();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		windowDisplay.getMetrics(displayMetrics);

		deviceWidth = displayMetrics.widthPixels;
		deviceHeight = displayMetrics.heightPixels;
	}
}
