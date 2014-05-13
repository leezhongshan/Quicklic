package quicklic.quicklic.test;

import java.io.Serializable;

import quicklic.floating.api.FloatingInterface;
import quicklic.floating.api.FloatingServices;
import quicklic.quicklic.quicklic.QuicklicActivity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

public class TestingFunction implements Serializable, FloatingInterface
{
	private static final long serialVersionUID = 1L;

	private static FloatingServices floatingServices;

	@Override
	public void setContext( FloatingServices floatingServices )
	{
		TestingFunction.floatingServices = floatingServices;
	}

	@Override
	public void touched( View v )
	{
		Toast.makeText(floatingServices, "single", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(floatingServices, QuicklicActivity.class);
		//		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 서비스 도중 실행 시, New Task 플래그가 필요하다.
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // 한 번 호출된 Activity에 대해서는 중복 호출 되지 않는다. (중복 불가 적용)
		setQuicklicVisibility(false);
		floatingServices.startActivity(intent);
	}

	@Override
	public void doubleTouched( View v )
	{
		floatingServices.changeMoveToSide();
		Toast.makeText(floatingServices, "double", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void longTouched( View v )
	{
		Toast.makeText(floatingServices, "long", Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean setAnimation()
	{
		return true;
	}

	@Override
	public void setQuicklicVisibility( boolean bool )
	{
		floatingServices.setVisibility(bool);
	}

	//-------------------------------------------------------------------
	//  API 비 제공 메소드
	//-------------------------------------------------------------------

	public static FloatingServices getFloatingServices()
	{
		return floatingServices;
	}
}
