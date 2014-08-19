package quicklic.quicklic.favorite;

import java.util.ArrayList;

import quicklic.floating.api.R;
import quicklic.quicklic.datastructure.Item;
import quicklic.quicklic.util.QuicklicActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class QuicklicFavoriteActivity extends QuicklicActivity {

	private PreferencesManager preferencesManager;
	private PackageManager packageManager;

	private ArrayList<Item> imageArrayList;
	private ArrayList<String> pkgArrayList;

	private boolean delEnabled;
	private boolean isActivity;
	private int item_count;
	private ImageView imageView;
	public static Activity activity;

	@Override
	public void onCreate()
	{
		super.onCreate();

		System.out.println("create");

		imageView = new ImageView(this);
		initialize();
		initializeView();
	}

	@Override
	public int onStartCommand( Intent intent, int flags, int startId )
	{
<<<<<<< HEAD
		System.out.println("start");
=======
		delEnabled = intent.getBooleanExtra("center", false);
		System.out.println(delEnabled);
		setCenterView();

>>>>>>> origin/ActivityToService
		return START_NOT_STICKY;
	}

	@Override
	public void onRebind( Intent intent )
	{
		// TODO Auto-generated method stub
		System.out.println("Rebind");
		super.onRebind(intent);
	}

	@Override
	public void onConfigurationChanged( Configuration newConfig )
	{
		// TODO Auto-generated method stub
		System.out.println("Config");
		super.onConfigurationChanged(newConfig);
	}

	private void onResume()
	{
		resetQuicklic();
		initializeView();
		setCenterView();
	}

	private void resetQuicklic()
	{
		if ( getQuicklicFrameLayout() != null )
		{
			getQuicklicFrameLayout().removeAllViews();
		}
	}

	private void initialize()
	{
		preferencesManager = new PreferencesManager(this);
		packageManager = getPackageManager();

		imageArrayList = new ArrayList<Item>();
		pkgArrayList = new ArrayList<String>();
	}

	private void initializeView()
	{
		getPreference();
		addViewsForBalance(imageArrayList.size(), imageArrayList, clickListener);
	}

	/**
	 * @함수명 : setCenterView
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : 삭제모드에 따라서 + / - 를 전환
	 * @작성자 : JHPark, THYang
	 * @작성일 : 2014. 6. 26.
	 */
	private void setCenterView()
	{
		if ( !delEnabled )
			imageView.setBackgroundResource(R.drawable.favorite_add);
		else
			imageView.setBackgroundResource(R.drawable.favorite_delete);

		imageView.setId(0);
		imageView.setOnClickListener(clickListener);
		imageView.setOnLongClickListener(onLongClickListener);

		setCenterView(imageView);
	}

	/**
	 * @함수명 : getPreference
	 * @매개변수 :
	 * @반환 : void
	 * @기능(역할) : Favorite에 보여줄 어플리케이션을 모두 가져오기
	 * @작성자 : JHPark
	 * @작성일 : 2014. 5. 22.
	 */
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

	private OnClickListener clickListener = new OnClickListener()
	{
		@Override
		public void onClick( View v )
		{
			if ( v == getCenterView() ) // Add / Delete Button
			{
				if ( !delEnabled )
				{
					getWindowManager().removeView(getDetectLayout());

					if ( isItemFull(item_count) ) // check full count
					{
						Toast.makeText(getApplicationContext(), R.string.err_limited_item_count, Toast.LENGTH_SHORT).show();
					}
					isActivity = true;
					Intent apkIntent = new Intent(QuicklicFavoriteActivity.this, ApkListActivity.class);
					apkIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					apkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(apkIntent);

					Intent stopIntent = new Intent(getApplicationContext(), QuicklicFavoriteActivity.class);
					stopService(stopIntent);
				}
			}
			else
			{
				getWindowManager().removeView(getDetectLayout());

				if ( !delEnabled )
				{

					/* 실행할 수 없는 앱을 추가한 상태에서
					 *  앱 실행을 요청했을 때,
					 *  예외처리를 하여서 service는 죽지 않으며, 사용자에게 Toast로 알림.
					 */
					try
					{
						String packageName = preferencesManager.getAppPreferences(getApplicationContext(), v.getId());
						Intent runIntent = packageManager.getLaunchIntentForPackage(packageName);
						startActivity(runIntent);

						setFloatingVisibility(true);
					}
					catch (Exception e)
					{
						Toast.makeText(getApplicationContext(), R.string.favorite_run_no, Toast.LENGTH_SHORT).show();
					}
<<<<<<< HEAD
					//					Intent stopIntent = new Intent(getApplicationContext(), QuicklicFavoriteActivity.class);
					//					stopService(stopIntent);

					onResume();
=======
					Intent stopIntent = new Intent(getApplicationContext(), QuicklicFavoriteActivity.class);
					stopService(stopIntent);
>>>>>>> origin/ActivityToService
				}
				else
				{
					preferencesManager.removeAppPreferences(getApplicationContext(), v.getId());
					//					onResume();

					Intent stopIntent = new Intent(getApplicationContext(), QuicklicFavoriteActivity.class);
					stopService(stopIntent);

<<<<<<< HEAD
					//					Intent stopIntent = new Intent(getApplicationContext(), QuicklicFavoriteActivity.class);
					//					stopService(stopIntent);
=======
					Intent restartIntent = new Intent(getApplicationContext(), QuicklicFavoriteActivity.class);
					restartIntent.putExtra("center", delEnabled);
					System.out.println(delEnabled);
					startService(restartIntent);
>>>>>>> origin/ActivityToService

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

			onResume();
			return true;
		}
	};

}