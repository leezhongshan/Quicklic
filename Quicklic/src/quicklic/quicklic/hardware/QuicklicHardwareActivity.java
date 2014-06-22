package quicklic.quicklic.hardware;

import java.util.ArrayList;

import quicklic.floating.api.R;
import quicklic.quicklic.datastructure.Item;
import quicklic.quicklic.main.QuicklicActivity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class QuicklicHardwareActivity extends QuicklicActivity {

	private final int COMP_SOUND_MUTE = 1;
	private final int COMP_SOUND_INC = 2;
	private final int COMP_SOUND_DEC = 3;
	private final int COMP_WIFI = 4;
	private final int COMP_BLUETOOTH = 5;
	private final int COMP_ROTATE = 6;

	private ArrayList<Item> imageArrayList;
	private ComponentWifi componentWifi;
	private ComponentBluetooth componentBluetooth;

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

		imageArrayList.add(new Item(COMP_ROTATE, R.drawable.rotate_on));
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
				startActivity(new Intent(android.provider.Settings.ACTION_SOUND_SETTINGS));
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
			}
			onResume();
		}
	};

}