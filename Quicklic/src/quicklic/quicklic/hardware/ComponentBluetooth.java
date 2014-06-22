package quicklic.quicklic.hardware;

import android.bluetooth.BluetoothAdapter;
import android.util.Log;

public class ComponentBluetooth {

	BluetoothAdapter bluetooth;

	public ComponentBluetooth()
	{
		bluetooth = BluetoothAdapter.getDefaultAdapter();
	}

	private void onBluetooth()
	{
		bluetooth.enable();
	}

	private void offBluetooth()
	{
		bluetooth.disable();
	}

	public void controlBluetooth()
	{
		if ( bluetooth != null )
		{
			if ( !isEnabled() )
			{
				onBluetooth();
			}
			else
			{
				offBluetooth();
			}
		}
		else
		{
			Log.i("DEBUG_TAG", "블루투스지원안함");
		}
	}

	public boolean isEnabled()
	{
		return bluetooth.isEnabled();
	}
}
