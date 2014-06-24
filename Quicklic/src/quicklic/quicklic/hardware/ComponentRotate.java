package quicklic.quicklic.hardware;

import quicklic.floating.api.R;
import android.content.Context;
import android.provider.Settings;

public class ComponentRotate {
	Context r_context;

	public ComponentRotate(Context context)
	{
		this.r_context = context;
	}
	/**
	 * @함수명 : onRotate
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : rotate 켜기
	 * @작성자 : SBKim
	 * @작성일 : 2014. 6. 24.
	 */
	private void onRotate()
	{
		android.provider.Settings.System.putInt(r_context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);	
	}
	/**
	 * @함수명 : offRotate
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : rotate 끄기
	 * @작성자 : SBKim
	 * @작성일 : 2014. 6. 24.
	 */
	private void offRotate()
	{
		android.provider.Settings.System.putInt(r_context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 1);
	}
	/**
	 * @함수명 : controlRotate
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : rotate on/off 상태 전환
	 * @작성자 : SBKim
	 * @작성일 : 2014. 6. 24.
	 */
	public void controlRotate()
	{
		if ( android.provider.Settings.System.getInt(r_context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1 )
		{
			onRotate();
		}
		else
		{
			offRotate();
		}
	}
	/**
	 * @함수명 : getDrawable
	 * @매개변수 :
	 * @반환 : int
	 * @기능(역할) : rotate 상태에 따른 drawable 반환
	 * @작성자 : SBKim
	 * @작성일 : 2014. 6. 24.
	 */
	public int getDrawable()
	{
		if ( android.provider.Settings.System.getInt(r_context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1 )
			return R.drawable.rotate_on;
		else
			return R.drawable.rotate_off;
	}
}
