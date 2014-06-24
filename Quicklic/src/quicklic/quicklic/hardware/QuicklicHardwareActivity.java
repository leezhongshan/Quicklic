package quicklic.quicklic.hardware;

import java.util.ArrayList;

import quicklic.floating.api.R;
import quicklic.quicklic.datastructure.Item;
import quicklic.quicklic.main.QuicklicActivity;
import quicklic.quicklic.test.SettingFloatingInterface;
import android.content.Context;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class QuicklicHardwareActivity extends QuicklicActivity {

	private final int DELAY_TIME = 2000;
	private final int COMP_SOUND_RING = 1;
	private final int COMP_SOUND_INC = 2;
	private final int COMP_SOUND_DEC = 3;
	private final int COMP_WIFI = 4;
	private final int COMP_BLUETOOTH = 5;
	private final int COMP_ROTATE = 6;

	private ArrayList<Item> imageArrayList;
	private ComponentWifi componentWifi;
	private ComponentBluetooth componentBluetooth;
	private ComponentRotate componentRotate;
	private ComponentVolume componentVolume;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_quicklic);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		resetQuicklic();
		initialize();
	}

	private void resetQuicklic()
	{
		getQuicklicFrameLayout().removeViews(1, getViewCount());
	}

	private void initialize()
	{
		componentWifi = new ComponentWifi((WifiManager) getSystemService(Context.WIFI_SERVICE));
		componentBluetooth = new ComponentBluetooth();
		componentRotate = new ComponentRotate(getApplicationContext());
		componentVolume = new ComponentVolume((AudioManager) getSystemService(Context.AUDIO_SERVICE));

		imageArrayList = new ArrayList<Item>();
		imageArrayList.add(new Item(COMP_SOUND_DEC, R.drawable.sound_decrease));
		imageArrayList.add(new Item(COMP_WIFI, componentWifi.getDrawable()));
		imageArrayList.add(new Item(COMP_BLUETOOTH, componentBluetooth.getDrawable()));
		imageArrayList.add(new Item(COMP_ROTATE, componentRotate.getDrawable()));
		imageArrayList.add(new Item(COMP_SOUND_RING, componentVolume.getDrawable()));
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
			case COMP_SOUND_RING:
				componentVolume.controlVolume();
				break;

			case COMP_SOUND_INC:
				if ( componentVolume.isMaxVolume() )
					Toast.makeText(getApplicationContext(), R.string.hardware_volume_max, Toast.LENGTH_SHORT).show();
				else
					componentVolume.upVolume();
				break;

			case COMP_SOUND_DEC:
				if ( componentVolume.isMinVolume() )
					Toast.makeText(getApplicationContext(), R.string.hardware_volume_min, Toast.LENGTH_SHORT).show();
				else
					componentVolume.downVolume();
				break;

			case COMP_ROTATE:
				componentRotate.controlRotate();
				break;

			default:
				switch ( v.getId() )
				{
				case COMP_WIFI:
					componentWifi.controlWifi();
					break;

				case COMP_BLUETOOTH:
					componentBluetooth.controlBluetooth();
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
				return;
			}
			onResume();
		}
	};

	protected void onUserLeaveHint()
	{
		if ( SettingFloatingInterface.getFloatingService().getQuicklic().getVisibility() != View.VISIBLE )
		{
			homeKeyPressed();
		}
		finish();
	}
}