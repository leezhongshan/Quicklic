package quicklic.quicklic.hardware;

import quicklic.floating.api.R;
import quicklic.quicklic.test.SettingFloatingInterface;
import android.app.Activity;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ComponentPower extends Activity {

	private final int REQ_ACTIVATE_DEVICE_ADMIN = 100;

	private Context context;
	private DevicePolicyManager mPolicy;
	private ComponentName mReceiverComponent;

	public ComponentPower(Context context, DevicePolicyManager mPolicy, ComponentName mReceiverComponent)
	{
		this.context = context;
		this.mPolicy = mPolicy;
		this.mReceiverComponent = mReceiverComponent;
	}

	public Intent createIntent()
	{
		Intent activateDeviceAdminIntent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);

		activateDeviceAdminIntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mReceiverComponent);
		activateDeviceAdminIntent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "dd");//getResources().getString(R.string.device_admin_description));

		return activateDeviceAdminIntent;
	}

	public void screenOff()
	{
		mPolicy.lockNow();

		if ( SettingFloatingInterface.getFloatingService() != null )
		{
			SettingFloatingInterface.getFloatingService().setVisibility(true);
		}
	}

	public void getAdministratorForPower()
	{
		if ( !mPolicy.isAdminActive(mReceiverComponent) )
		{
			Intent activateDeviceAdminIntent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);

			activateDeviceAdminIntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mReceiverComponent);
			activateDeviceAdminIntent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, context.getResources().getString(R.string.device_admin_description));

			context.startActivity(activateDeviceAdminIntent);
		}
		else
		{
			mPolicy.lockNow();

			if ( SettingFloatingInterface.getFloatingService() != null )
			{
				SettingFloatingInterface.getFloatingService().setVisibility(true);
			}
		}
	}

	//	public static class DeviceAdmin extends DeviceAdminReceiver
	//	{
	//			public DeviceAdmin()
	//			{
	//	
	//			}
	//	
	//			@Override
	//			public void onEnabled( Context context, Intent intent )
	//			{
	//				Toast.makeText(context, "기기관리자가 활성화 되었습니다.", Toast.LENGTH_SHORT).show();
	//				super.onEnabled(context, intent);
	//			}
	//	
	//			@Override
	//			public void onDisabled( Context context, Intent intent )
	//			{
	//				Toast.makeText(context, "기기관리자가 비활성화 되었습니다.", Toast.LENGTH_SHORT).show();
	//				super.onDisabled(context, intent);
	//			}
	//	}

}
