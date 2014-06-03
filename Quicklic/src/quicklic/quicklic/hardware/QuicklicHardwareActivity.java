package quicklic.quicklic.hardware;

import java.util.ArrayList;

import quicklic.floating.api.R;
<<<<<<< HEAD:Quicklic/src/quicklic/quicklic/hardware/QuicklicHardwareActivity.java
import quicklic.quicklic.main.QuicklicActivity;
=======
import android.bluetooth.BluetoothAdapter;
>>>>>>> first_branch:Quicklic/src/quicklic/quicklic/quicklic/QuicklicHardwareActivity.java
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

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
<<<<<<< HEAD:Quicklic/src/quicklic/quicklic/hardware/QuicklicHardwareActivity.java
=======
		bluetooth = BluetoothAdapter.getDefaultAdapter();
>>>>>>> first_branch:Quicklic/src/quicklic/quicklic/quicklic/QuicklicHardwareActivity.java

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
<<<<<<< HEAD:Quicklic/src/quicklic/quicklic/hardware/QuicklicHardwareActivity.java
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
=======
				controlWifi();
			}
			if ( v.getId() == 3 )
			{
				controlBluetooth();
>>>>>>> first_branch:Quicklic/src/quicklic/quicklic/quicklic/QuicklicHardwareActivity.java
			}

		}
	};

}