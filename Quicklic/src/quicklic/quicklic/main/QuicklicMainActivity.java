package quicklic.quicklic.main;

import java.util.ArrayList;

import quicklic.floating.api.R;
import quicklic.quicklic.datastructure.Item;
import quicklic.quicklic.favorite.QuicklicFavoriteActivity;
import quicklic.quicklic.hardware.QuicklicHardwareActivity;
import quicklic.quicklic.keyboard.QuicklicKeyBoardService;
import quicklic.quicklic.util.QuicklicActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class QuicklicMainActivity extends QuicklicActivity {

	private final int HARDWARE = 0;
	private final int KEYBOARD = 1;
	private final int FAVORITE = 2;

	private final int HARDWARE_POWER = 100;

	private ArrayList<Item> imageArrayList;
	private boolean isKeyBoardService;
	private boolean isNotHomeKey;

	@Override
	public void onCreate()
	{
		super.onCreate();
		initialize();
	}

	private void initialize()
	{
		isKeyBoardService = false;
		isNotHomeKey = false;
		imageArrayList = new ArrayList<Item>();

		imageArrayList.add(new Item(HARDWARE, R.drawable.hardware));
		imageArrayList.add(new Item(KEYBOARD, R.drawable.keyboard));
		imageArrayList.add(new Item(FAVORITE, R.drawable.favorite));

		addViewsForBalance(imageArrayList.size(), imageArrayList, clickListener);
	}

	private OnClickListener clickListener = new OnClickListener()
	{
		private Intent intent;

		@Override
		public void onClick( View v )
		{
			isNotHomeKey = true;

			switch ( v.getId() )
			{
			case HARDWARE:
				intent = new Intent(getApplicationContext(), QuicklicMainActivity.class);
				stopService(intent);

				getWindowManager().removeView(getDetectLayout());

				intent = new Intent(QuicklicMainActivity.this, QuicklicHardwareActivity.class);
				startService(intent);
				break;

			case KEYBOARD:
				intent = new Intent(getApplicationContext(), QuicklicMainActivity.class);
				stopService(intent);

				getWindowManager().removeView(getDetectLayout());

				isKeyBoardService = true;
				intent = new Intent(QuicklicMainActivity.this, QuicklicKeyBoardService.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startService(intent);
				setFloatingVisibility(false);
				//				finish();
				break;

			case FAVORITE:
				intent = new Intent(getApplicationContext(), QuicklicMainActivity.class);
				stopService(intent);

				getWindowManager().removeView(getDetectLayout());
				intent = new Intent(QuicklicMainActivity.this, QuicklicFavoriteActivity.class);
				getQuicklicFrameLayout().setVisibility(View.INVISIBLE);
				startService(intent);

				break;

			default:
				break;
			}
		}
	};

	//	protected void onUserLeaveHint()
	//	{
	//		if ( !isNotHomeKey )
	//		{
	//			homeKeyPressed();
	//		}
	//	};
}
