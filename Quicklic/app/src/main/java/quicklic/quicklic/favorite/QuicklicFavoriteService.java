package quicklic.quicklic.favorite;

import java.util.ArrayList;

import quicklic.floating.api.R;
import quicklic.quicklic.datastructure.Item;
import quicklic.quicklic.util.BaseQuicklic;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class QuicklicFavoriteService extends BaseQuicklic {

	private PreferencesManager preferencesManager;
	private PackageManager packageManager;

	private ArrayList<Item> imageArrayList;
	private ArrayList<String> pkgArrayList;

	private boolean delEnabled;
	private boolean isAdded;

	private int item_count;
	private int current_page;

	@Override
	public int onStartCommand( Intent intent, int flags, int startId )
	{
		setIsMain(false);
		initialize(intent);
		initializeImage();

		return START_NOT_STICKY;
	}

	@Override
	public void onConfigurationChanged( Configuration newConfig )
	{
		super.onConfigurationChanged(newConfig);
		resetQuicklic();
	}

	private void initializeImage()
	{
		setCenterView();
		initializeView();
		initializeViewPager();
	}


	private void resetQuicklic()
	{
		isAdded = false;

		current_page = getViewPager().getCurrentItem();

		if ( getQuicklicFrameLayout() != null )
		{
			getQuicklicFrameLayout().removeAllViews();
		}

		initializeImage();
	}

	private void initialize( Intent intent )
	{
		setIsMain(true);

		preferencesManager = new PreferencesManager(this);
		packageManager = getPackageManager();

		imageArrayList = new ArrayList<Item>();
		pkgArrayList = new ArrayList<String>();

		current_page = intent.getIntExtra("page", 0);
		isAdded = intent.getBooleanExtra("add", false);
	}

	private void initializeView()
	{
		getPreference();
		addViewsForBalance(imageArrayList.size(), imageArrayList, clickListener);
	}


	private void initializeViewPager()
	{

		if ( !delEnabled )
		{

			if ( !isAdded )
			{
				getViewPager().setCurrentItem(current_page);
			}

			else
			{
				getViewPager().setCurrentItem(getViewCount());
			}
		}
		else
		{

			getViewPager().setCurrentItem(current_page);
		}
	}

    private void getPreference()
	{
		pkgArrayList.clear();
		imageArrayList.clear();
		item_count = preferencesManager.getNumPreferences(this);

		for ( int i = 0; i < item_count; i++ )
		{
			String packageName = preferencesManager.getAppPreferences(this, i);
			pkgArrayList.add(packageName);
			try
			{
				Drawable appIcon = packageManager.getApplicationIcon(packageName);
				imageArrayList.add(new Item(i, appIcon));
			}
			catch (NameNotFoundException e)
			{
				e.printStackTrace();
			}
		}
	}


	private void setCenterView()
	{
		ImageView imageView = new ImageView(this);

		if ( !delEnabled )
			imageView.setBackgroundResource(R.drawable.favorite_add);
		else
			imageView.setBackgroundResource(R.drawable.favorite_delete);

		imageView.setId(0);
		imageView.setOnClickListener(clickListener);
		imageView.setOnLongClickListener(onLongClickListener);

		setCenterView(imageView);
	}




	private void stopService()
	{
		Intent stopIntent = new Intent(getApplicationContext(), QuicklicFavoriteService.class);
		stopService(stopIntent);
	}

	private OnClickListener clickListener = new OnClickListener()
	{
		@Override
		public void onClick( View v )
		{
			// Center Button Click
			if ( v == getCenterView() )
			{
				if ( !delEnabled )
				{

					getWindowManager().removeView(getDetectLayout());

					if ( isItemFull(item_count) ) // check full count
					{
						Toast.makeText(getApplicationContext(), R.string.err_limited_item_count, Toast.LENGTH_SHORT).show();
					}

					Intent apk = new Intent(QuicklicFavoriteService.this, ApkListActivity.class);
					apk.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					apk.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					apk.putExtra("page", getViewPager().getCurrentItem());
					startActivity(apk);

					stopService();
				}
			}
			// Application Click
			else
			{
				if ( !delEnabled ) // ADD Mode
				{
					try
					{

						String packageName = preferencesManager.getAppPreferences(getApplicationContext(), v.getId());
						Intent runIntent = packageManager.getLaunchIntentForPackage(packageName);
						startActivity(runIntent);

						setFloatingVisibility(true);
						stopService();

						getWindowManager().removeView(getDetectLayout());
					}
					catch (Exception e)
					{
						Toast.makeText(getApplicationContext(), R.string.favorite_run_no, Toast.LENGTH_SHORT).show();
					}
				}
				else
				{
					preferencesManager.removeAppPreferences(getApplicationContext(), v.getId());
					resetQuicklic();
				}
			}
		}
	};


	private OnLongClickListener onLongClickListener = new OnLongClickListener()
	{
		@Override
		public boolean onLongClick( View v )
		{
			if ( delEnabled )
			{
				delEnabled = false;
				Toast.makeText(getApplicationContext(), R.string.favorite_disable_delete, Toast.LENGTH_SHORT).show();
			}
			else
			{
				delEnabled = true;
				Toast.makeText(getApplicationContext(), R.string.favorite_enable_delete, Toast.LENGTH_SHORT).show();
			}
			resetQuicklic();
			return true;
		}
	};

}