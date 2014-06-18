package quicklic.quicklic.hardware;

import java.util.ArrayList;

import quicklic.floating.api.R;
import quicklic.quicklic.main.QuicklicActivity;
import quicklic.quicklic.test.TestingFunction;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class QuicklicHardwareActivity extends QuicklicActivity {

	private ArrayList<Drawable> imageArrayList;
	WifiManager wifi;
	BluetoothAdapter bluetooth;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_quicklic);

		initialize();
	}

	private void onWifi()
	{
		wifi.setWifiEnabled(true);
	}

	private void offWifi()
	{
		wifi.setWifiEnabled(false);
	}

	private void controlWifi()
	{
		if ( !wifi.isWifiEnabled() )
		{
			if ( wifi.getWifiState() != WifiManager.WIFI_STATE_ENABLED )
			{
				onWifi();
				System.out.println("on");
			}
		}
		else
		{
			offWifi();
			System.out.println("off");
		}
	}

	private void controlBluetooth()
	{
		if ( bluetooth == null )
		{
			Log.i("DEBUG_TAG", "블루투스지원안함");
		}
		else
		{
			if ( !bluetooth.isEnabled() )
			{
				bluetooth.enable();
				Log.i("DEBUG_TAG", "블루투스ON");
			}
			else
			{
				bluetooth.disable();
				Log.i("DEBUG_TAG", "블루투스OFF");
			}
		}
	}

	private void initialize()
	{
		imageArrayList = new ArrayList<Drawable>();
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		bluetooth = BluetoothAdapter.getDefaultAdapter();

		// TODO
		imageArrayList.add(getResources().getDrawable(R.drawable.hardware_test));
		imageArrayList.add(getResources().getDrawable(R.drawable.volume_test));
		imageArrayList.add(getResources().getDrawable(R.drawable.wifi_test));
		if ( bluetooth != null )
		{
			imageArrayList.add(getResources().getDrawable(R.drawable.bluetooth_test));
		}
		addViewsForBalance(imageArrayList.size(), imageArrayList, onClickListener);
	}

	private OnClickListener onClickListener = new OnClickListener()
	{
		@Override
		public void onClick( View v )
		{
			//TODO
			if ( v.getId() == 0 )
			{

			}
			if ( v.getId() == 1 )
			{
				startActivity(new Intent(android.provider.Settings.ACTION_SOUND_SETTINGS));
			}
			if ( v.getId() == 2 )
			{
				controlWifi();
			}
			if ( v.getId() == 3 )
			{
				controlBluetooth();
			}

		}
	};

	protected void onUserLeaveHint()
	{
		TestingFunction.getFloatingService().getQuicklic().setVisibility(View.VISIBLE);
		finish();

		Toast.makeText(this, "5초의 딜레이가 있습니다.", Toast.LENGTH_LONG).show();
	};

}