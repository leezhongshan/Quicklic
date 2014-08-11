package quicklic.quicklic.hardware;

import quicklic.floating.api.R;
import quicklic.quicklic.util.DeviceAdmin;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class ComponentPower extends Activity {

	private ComponentName componentName;
	private DevicePolicyManager devicePolicyManager;

	private final int HARDWARE_POWER = 100;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		componentName = new ComponentName(this, DeviceAdmin.class);

		if ( !devicePolicyManager.isAdminActive(componentName) )
		{
			Intent activateDeviceAdminIntent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			activateDeviceAdminIntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
			activateDeviceAdminIntent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getResources().getString(R.string.hardware_device_admin_description));
			activateDeviceAdminIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(activateDeviceAdminIntent);
		}
		else
		{
			devicePolicyManager.lockNow();
			//TODO
			setResult(HARDWARE_POWER);
			finish();
		}

		finish();
	};

}
