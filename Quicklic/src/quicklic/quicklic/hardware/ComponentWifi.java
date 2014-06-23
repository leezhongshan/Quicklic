package quicklic.quicklic.hardware;

import android.app.Activity;
import android.net.wifi.WifiManager;

public class ComponentWifi extends Activity {

	WifiManager wifi;

	public ComponentWifi(WifiManager wifiManager)
	{
		wifi = wifiManager;
	}

	private void onWifi()
	{
		wifi.setWifiEnabled(true);
	}

	private void offWifi()
	{
		wifi.setWifiEnabled(false);
	}

	public boolean isEnabled()
	{
		return wifi.isWifiEnabled();
	}

	public void controlWifi()
	{
		if ( !isEnabled() )
		{
			if ( wifi.getWifiState() != WifiManager.WIFI_STATE_ENABLED )
			{
				onWifi();
			}
		}
		else
		{
			offWifi();
		}
	}
}
