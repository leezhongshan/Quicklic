package quicklic.quicklic.hardware;

import java.util.ArrayList;

import quicklic.floating.api.R;
import quicklic.quicklic.datastructure.Item;
import quicklic.quicklic.test.SettingFloatingInterface;
import quicklic.quicklic.util.QuicklicActivity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class QuicklicHardwareActivity extends QuicklicActivity {

	private final int DELAY_TIME = 2000;
	private final int COMP_SOUND_RING = 1;
	private final int COMP_SOUND_INC = 2;
	private final int COMP_SOUND_DEC = 3;
	private final int COMP_WIFI = 4;
	private final int COMP_BLUETOOTH = 5;
	private final int COMP_ROTATE = 6;
	private final int COMP_GPS = 7;
	private final int COMP_HOME_KEY = 8;

	private boolean isActivity;
	private boolean isHomeKey;

	private ArrayList<Item> imageArrayList;
	private ComponentWifi componentWifi;
	private ComponentBluetooth componentBluetooth;
	private ComponentGPS componentGPS;
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

	/**
	 * @함수명 : resetQuicklic
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : 메인 뷰를 제외한 나머지 를 모두 제거
	 * @작성자 : JHPark
	 * @작성일 : 2014. 6. 25.
	 */
	private void resetQuicklic()
	{
		getQuicklicFrameLayout().removeViews(1, getViewCount());
	}

	/**
	 * @함수명 : initialize
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : Component 초기화 및 상태에 따른 그림 설정
	 * @작성자 : SBKim, THYang
	 * @작성일 : 2014. 6. 25.
	 */
	private void initialize()
	{
		isActivity = true;
		isHomeKey = false;

		componentWifi = new ComponentWifi((WifiManager) getSystemService(Context.WIFI_SERVICE));
		componentBluetooth = new ComponentBluetooth();
		componentGPS = new ComponentGPS(getApplicationContext());
		componentRotate = new ComponentRotate(getApplicationContext());
		componentVolume = new ComponentVolume((AudioManager) getSystemService(Context.AUDIO_SERVICE));

		imageArrayList = new ArrayList<Item>();
		imageArrayList.add(new Item(COMP_SOUND_DEC, R.drawable.sound_decrease));
		imageArrayList.add(new Item(COMP_WIFI, componentWifi.getDrawable()));
		imageArrayList.add(new Item(COMP_BLUETOOTH, componentBluetooth.getDrawable()));
		imageArrayList.add(new Item(COMP_GPS, componentGPS.getDrawable()));
		imageArrayList.add(new Item(COMP_ROTATE, componentRotate.getDrawable()));
		imageArrayList.add(new Item(COMP_SOUND_RING, componentVolume.getDrawable()));
		imageArrayList.add(new Item(COMP_SOUND_INC, R.drawable.sound_increase));
		imageArrayList.add(new Item(COMP_HOME_KEY, R.drawable.ic_launcher));

		addViewsForBalance(imageArrayList.size(), imageArrayList, onClickListener);
	}

	public OnClickListener onClickListener = new OnClickListener()
	{
		@Override
		public void onClick( View v )
		{
			/**
			 * 클릭된 view (즉, 컴포넌트)에 따라 기능 수행
			 */
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

			case COMP_GPS:
				isActivity = false;
				Intent gps = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				gps.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(gps);
				return;

			case COMP_HOME_KEY:
				isActivity = false;
				isHomeKey = true;
				Intent homekey = new Intent(Intent.ACTION_MAIN);
				homekey.addCategory(Intent.CATEGORY_HOME);
				homekey.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(homekey);
				return;
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
		if ( isActivity && SettingFloatingInterface.getFloatingService().getQuicklic().getVisibility() != View.VISIBLE )
		{
			homeKeyPressed();
		}
		if ( isHomeKey )
		{
			SettingFloatingInterface.getFloatingService().getQuicklic().setVisibility(View.VISIBLE);
			finish();
		}
	}
}