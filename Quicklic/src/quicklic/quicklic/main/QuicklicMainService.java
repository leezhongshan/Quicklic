package quicklic.quicklic.main;

import java.util.ArrayList;

import quicklic.floating.api.R;
import quicklic.quicklic.datastructure.Item;
import quicklic.quicklic.favorite.QuicklicFavoriteService;
import quicklic.quicklic.hardware.QuicklicHardwareService;
import quicklic.quicklic.keyboard.QuicklicKeyBoardService;
import quicklic.quicklic.util.QuicklicBase;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.View;
import android.view.View.OnClickListener;

public class QuicklicMainService extends QuicklicBase {

	private final int HARDWARE = 0;
	private final int KEYBOARD = 1;
	private final int FAVORITE = 2;

	private ArrayList<Item> imageArrayList;

	@Override
	public void onCreate()
	{
		super.onCreate();
		initialize();
	}

	@Override
	public void onConfigurationChanged( Configuration newConfig )
	{
		super.onConfigurationChanged(newConfig);
		addViewsForBalance(imageArrayList.size(), imageArrayList, clickListener);
	}

	private void initialize()
	{
		imageArrayList = new ArrayList<Item>();
		imageArrayList.add(new Item(HARDWARE, R.drawable.hardware));
		imageArrayList.add(new Item(KEYBOARD, R.drawable.keyboard));
		imageArrayList.add(new Item(FAVORITE, R.drawable.favorite));

		addViewsForBalance(imageArrayList.size(), imageArrayList, clickListener);
	}

	private void restartService( Class<?> cls )
	{
		Intent intent = new Intent(getApplicationContext(), QuicklicMainService.class);
		stopService(intent);

		intent = new Intent(QuicklicMainService.this, cls);
		startService(intent);
	}

	private OnClickListener clickListener = new OnClickListener()
	{
		@Override
		public void onClick( View v )
		{
			getWindowManager().removeView(getDetectLayout());

			switch ( v.getId() )
			{
			case HARDWARE:
				restartService(QuicklicHardwareService.class);
				break;

			case KEYBOARD:
				restartService(QuicklicKeyBoardService.class);
				setFloatingVisibility(false);
				break;

			case FAVORITE:
				restartService(QuicklicFavoriteService.class);
				break;

			default:
				break;
			}
		}
	};
}
