package quicklic.quicklic.hardware;

import java.util.ArrayList;

import quicklic.floating.api.R;
import quicklic.quicklic.datastructure.Item;
import quicklic.quicklic.main.QuicklicActivity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;

public class QuicklicHardwareActivity extends QuicklicActivity {

	private final int DELAY_TIME = 2000;
	private final int COMP_SOUND_MUTE = 1;
	private final int COMP_SOUND_INC = 2;
	private final int COMP_SOUND_DEC = 3;
	private final int COMP_WIFI = 4;
	private final int COMP_BLUETOOTH = 5;
	private final int COMP_ROTATE = 6;
	private final int COMP_GPS = 7;

	private ArrayList<Item> imageArrayList;
	private ComponentWifi componentWifi;
	private ComponentBluetooth componentBluetooth;
	private ComponentGPS componentGPS;
	private ComponentRotate componentRotate;
	private ComponentSound componentSound;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_quicklic);
	}

	@Override
	protected void onResume()
	{
		resetQuicklic();
		super.onResume();
		initialize();
	}

	private void resetQuicklic()
	{
		getQuicklicFrameLayout().removeViews(1, getViewCount());
	}

	private void initialize()
	{
		int resId;
		componentWifi = new ComponentWifi((WifiManager) getSystemService(Context.WIFI_SERVICE));
		componentBluetooth = new ComponentBluetooth();
		componentGPS = new ComponentGPS();
		componentRotate = new ComponentRotate();
		componentSound = new ComponentSound();

		imageArrayList = new ArrayList<Item>();
		imageArrayList.add(new Item(COMP_SOUND_DEC, R.drawable.sound_decrease));

		if ( !componentWifi.isEnabled() )
			resId = R.drawable.wifi_off;
		else
			resId = R.drawable.wifi_on;
		imageArrayList.add(new Item(COMP_WIFI, resId));

		if ( !componentBluetooth.isEnabled() )
			resId = R.drawable.bluetooth_off;
		else
			resId = R.drawable.bluetooth_on;
		imageArrayList.add(new Item(COMP_BLUETOOTH, resId));

		imageArrayList.add(new Item(COMP_ROTATE, R.drawable.rotate_off));
		imageArrayList.add(new Item(COMP_GPS, R.drawable.gps_off));

		imageArrayList.add(new Item(COMP_SOUND_MUTE, R.drawable.sound_mute));
		imageArrayList.add(new Item(COMP_SOUND_INC, R.drawable.sound_increase));

		addViewsForBalance(imageArrayList.size(), imageArrayList, onClickListener);
	}

	public OnClickListener onClickListener = new OnClickListener()
	{
		@Override
		public void onClick( View v )
		{
			switch ( v.getId() )
			{
			case COMP_SOUND_MUTE:
				break;

			case COMP_SOUND_INC:

				break;

			case COMP_SOUND_DEC:
				break;

			case COMP_WIFI:
				componentWifi.controlWifi();
				break;

			case COMP_BLUETOOTH:
				componentBluetooth.controlBluetooth();
				break;

			case COMP_ROTATE:
				break;

			case COMP_GPS:
				break;
			}
			v.setEnabled(false);

			// Handler 에 Single Click 시 수행할 작업을 등록
			Message message = new Message();
			Handler handler = new Handler()
			{
				public void handleMessage( Message message )
				{
					onResume();
				}
			};
			// DOUBLE_PRESS_INTERVAL 시간동안 Handler 를 Delay 시킴.
			handler.sendMessageDelayed(message, DELAY_TIME);

		}
	};

}