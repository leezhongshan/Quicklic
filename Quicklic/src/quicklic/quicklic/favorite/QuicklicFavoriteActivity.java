package quicklic.quicklic.favorite;

import java.util.ArrayList;

import quicklic.floating.api.R;
import quicklic.quicklic.datastructure.Item;
import quicklic.quicklic.test.SettingFloatingInterface;
import quicklic.quicklic.util.QuicklicActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_quicklic);
		activity = QuicklicFavoriteActivity.this;
		imageView = new ImageView(this);

		initialize();
		initializeView();
		setCenterView();
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		resetQuicklic();

		setCenterView();

		initializeView();
	}

	private void resetQuicklic()
	{
		getQuicklicFrameLayout().removeViews(1, getViewCount());
	}

	private void initialize()
	{
		preferencesManager = new PreferencesManager(this);
		packageManager = getPackageManager();

		imageArrayList = new ArrayList<Item>();
		pkgArrayList = new ArrayList<String>();
		delEnabled = false;
	}

	private void initializeView()
	{
		isActivity = false;

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

		for ( int i = 0; i < item_count && !isItemFull(i); i++ )
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
		private Intent intent;

		@Override
		public void onClick( View v )
		{
			if ( v == getCenterView() )
			{
				if ( isItemFull(item_count) )
				{
					Toast.makeText(getApplicationContext(), R.string.err_limited_item_count, Toast.LENGTH_SHORT).show();
					return;
				}

				isActivity = true;
				intent = new Intent(QuicklicFavoriteActivity.this, ApkListActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
			else
			{
				if ( delEnabled )
				{
					preferencesManager.removeAppPreferences(getApplicationContext(), v.getId());
					onResume();
				}
				else
				{
					/* 실행할 수 없는 앱을 추가한 상태에서
					 *  앱 실행을 요청했을 때,
					 *  예외처리를 하여서 service는 죽지 않으며, 사용자에게 Toast로 알림.
					 */
					try
					{
						String packageName = preferencesManager.getAppPreferences(getApplicationContext(), v.getId());
						Intent intent = packageManager.getLaunchIntentForPackage(packageName);
						startActivity(intent);

						SettingFloatingInterface.getFloatingService().getQuicklic().setVisibility(View.VISIBLE);
						finish();
					}
					catch (Exception e)
					{
						Toast.makeText(getApplicationContext(), R.string.favorite_run_no, Toast.LENGTH_SHORT).show();
					}
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

	protected void onUserLeaveHint()
	{
		if ( !isActivity )
		{
			if ( SettingFloatingInterface.getFloatingService().getQuicklic().getVisibility() != View.VISIBLE )
			{
				homeKeyPressed();
			}
		}
	};
}