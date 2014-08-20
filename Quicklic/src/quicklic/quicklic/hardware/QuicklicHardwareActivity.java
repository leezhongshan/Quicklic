package quicklic.quicklic.hardware;

import java.util.ArrayList;

import quicklic.floating.api.R;
import quicklic.quicklic.datastructure.Item;
import quicklic.quicklic.util.DeviceAdmin;
import quicklic.quicklic.util.QuicklicActivity;
import android.annotation.SuppressLint;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class QuicklicHardwareActivity extends QuicklicActivity {

	private final int HARDWARE_POWER = 100;

	private final int DELAY_TIME = 2000;
	private final int COMP_SOUND_RING = 1;
	private final int COMP_SOUND_INC = 2;
	private final int COMP_SOUND_DEC = 3;
	private final int COMP_WIFI = 4;
	private final int COMP_BLUETOOTH = 5;
	private final int COMP_ROTATE = 6;
	private final int COMP_GPS = 7;
	private final int COMP_POWER = 8;
	private final int COMP_HOME_KEY = 9;
	private final int COMP_AIR_PLANE = 10;

	private DevicePolicyManager devicePolicyManager;
	private ComponentName componentName;

	private ArrayList<Item> imageArrayList;
	private ComponentWifi componentWifi;
	private ComponentBluetooth componentBluetooth;
	private ComponentGPS componentGPS;
	private ComponentRotate componentRotate;
	private ComponentVolume componentVolume;
	private ComponentAirPlane componentAirPlane;

	@Override
	public void onCreate()
	{
		super.onCreate();
		initialize();

		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		registerReceiver(broadcastReceiver, filter);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		unregisterReceiver(broadcastReceiver);
	}

	@Override
	public int onStartCommand( Intent intent, int flags, int startId )
	{
		return START_NOT_STICKY;
	}

	@Override
	public void onConfigurationChanged( Configuration newConfig )
	{
		super.onConfigurationChanged(newConfig);
		resetQuicklic();
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
		getQuicklicFrameLayout().removeAllViews();
		initialize();
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
		/* For Component Power */
		devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		componentName = new ComponentName(this, DeviceAdmin.class);

		/* Etc */
		componentWifi = new ComponentWifi((WifiManager) getSystemService(Context.WIFI_SERVICE));
		componentBluetooth = new ComponentBluetooth();
		componentGPS = new ComponentGPS(getApplicationContext());
		componentRotate = new ComponentRotate(getApplicationContext());
		componentVolume = new ComponentVolume((AudioManager) getSystemService(Context.AUDIO_SERVICE));
		componentAirPlane = new ComponentAirPlane(getApplicationContext());

		imageArrayList = new ArrayList<Item>();
		imageArrayList.add(new Item(COMP_POWER, R.drawable.power));
		imageArrayList.add(new Item(COMP_WIFI, componentWifi.getDrawable()));
		imageArrayList.add(new Item(COMP_BLUETOOTH, componentBluetooth.getDrawable()));
		imageArrayList.add(new Item(COMP_GPS, componentGPS.getDrawable()));
		imageArrayList.add(new Item(COMP_AIR_PLANE, componentAirPlane.getDrawable()));
		imageArrayList.add(new Item(COMP_ROTATE, componentRotate.getDrawable()));
		imageArrayList.add(new Item(COMP_SOUND_DEC, R.drawable.sound_decrease));
		imageArrayList.add(new Item(COMP_SOUND_RING, componentVolume.getDrawable()));
		imageArrayList.add(new Item(COMP_SOUND_INC, R.drawable.sound_increase));
		imageArrayList.add(new Item(COMP_HOME_KEY, R.drawable.home));

		addViewsForBalance(imageArrayList.size(), imageArrayList, onClickListener);
	}

	// Bluetooth와 Wifi의 상태 변화를 감지하여, 화면을 reload 하는 효과를 준다.
	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive( Context context, Intent intent )
		{
			String action = intent.getAction();
			if ( BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action) )
			{
				resetQuicklic();
			}
			else if ( WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action) )
			{
				resetQuicklic();
			}
		}
	};

	public OnClickListener onClickListener = new OnClickListener()
	{
		@SuppressLint("HandlerLeak")
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

			case COMP_BLUETOOTH:
				componentBluetooth.controlBluetooth();
				return;

			case COMP_WIFI:
				componentWifi.controlWifi();
				return;

			case COMP_GPS:
				Intent gps = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				gps.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(gps);
				return;

			case COMP_HOME_KEY:
				Intent homekey = new Intent(Intent.ACTION_MAIN);
				homekey.addCategory(Intent.CATEGORY_HOME);
				homekey.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(homekey);
				setFloatingVisibility(true);
				return;

			case COMP_POWER:
				if ( !devicePolicyManager.isAdminActive(componentName) )
				{
					Intent power = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
					power.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
					power.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getResources().getString(R.string.hardware_device_admin_description));
					power.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(power);
				}
				else
				{
					devicePolicyManager.lockNow();
				}
				return;

			case COMP_AIR_PLANE:
				Intent airplane = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
				airplane.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(airplane);
				return;

			default:
				switch ( v.getId() )
				{
				case COMP_WIFI:
					componentWifi.controlWifi();
					break;
				}
				v.setEnabled(false);

				// Handler 에 Single Click 시 수행할 작업을 등록
				Message message = new Message();
				Handler handler = new Handler()
				{
					public void handleMessage( Message message )
					{
						resetQuicklic();
					}
				};
				// DOUBLE_PRESS_INTERVAL 시간동안 Handler 를 Delay 시킴.
				handler.sendMessageDelayed(message, DELAY_TIME);
				return;
			}
			resetQuicklic();
		}
	};

}