package quicklic.quicklic.test;

import java.io.Serializable;

import quicklic.floating.api.FloatingInterface;
import quicklic.floating.api.FloatingService;
import quicklic.floating.api.R;
import quicklic.quicklic.main.QuicklicMainActivity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

public class SettingFloatingInterface implements Serializable, FloatingInterface
{
	private static final long serialVersionUID = 1L;
	private static FloatingService floatingService;
	private Intent intent;

	@Override
	public void setContext( FloatingService floatingService )
	{
		SettingFloatingInterface.floatingService = floatingService;
	}

	@Override
	public int setDrawableQuicklic()
	{
		return R.drawable.floating;
	}

	@Override
	public float setSize()
	{
		return 0.14f;
	}

	@Override
	public boolean setAnimation()
	{
		return true;
	}

	@Override
	public void touched( View v )
	{
		intent = new Intent(floatingService, QuicklicMainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 서비스 도중 실행 시, New Task 플래그가 필요하다.
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // 한 번 호출된 Activity에 대해서는 중복 호출 되지 않는다. (중복 불가 적용)
		setQuicklicVisibility(false);
		floatingService.startActivity(intent);
	}

	@Override
	public void doubleTouched( View v )
	{
		boolean mode = floatingService.changeMoveToSide();
		if ( mode )
			Toast.makeText(floatingService, R.string.quicklic_magnet_mode, Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(floatingService, R.string.quicklic_floating_mode, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void longTouched( View v )
	{
		floatingService.stopQuicklicService();
	}

	@Override
	public void setQuicklicVisibility( boolean bool )
	{
		floatingService.setVisibility(bool);
	}

	//-------------------------------------------------------------------
	//  API 비 제공 메소드
	//-------------------------------------------------------------------

	public static FloatingService getFloatingService()
	{
		return floatingService;
	}

}
