package quicklic.quicklic.quicklic;

import java.util.ArrayList;

import quicklic.floating.api.R;
import quicklic.quicklic.quicklic.favorite.ApkListActivity;
import quicklic.quicklic.quicklic.favorite.PreferencesManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class QuicklicFavoriteActivity extends QuicklicActivity {

	private ArrayList<Drawable> imageArrayList;
	private ArrayList<String> pkgArrayList;
	private PreferencesManager preferencesManager;
	private PackageManager packageManager;
	private boolean delEnabled;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_quicklic);
		delEnabled = false;
	}

	private void initialize()
	{
		imageArrayList = new ArrayList<Drawable>();
		pkgArrayList = new ArrayList<String>();

		getPreference();
		setCenterView();
		addViewsForBalance(imageArrayList.size(), imageArrayList, onClickListener);
	}

	private void setCenterView()
	{
		ImageView imageView = new ImageView(this);
		imageView.setId(99);
		imageView.setOnClickListener(onClickListener);
		imageView.setOnLongClickListener(onLongClickListener);
		imageView.setBackgroundResource(R.drawable.favorite_test);
		setCenterView(imageView);
	}

	@Override
	protected void onResume()
	{
		resetQuicklic();
		initialize();

		super.onResume();
		preferencesManager = new PreferencesManager();
	}

	private void resetQuicklic()
	{
		getQuicklicFrameLayout().removeViews(1, getViewCount());
	}

	private void getPreference()
	{
		preferencesManager = new PreferencesManager();

		packageManager = getPackageManager();

		for ( int i = 0; i < preferencesManager.getNumPreferences(this) + 1 && i < 10; i++ )
		{
			pkgArrayList.add(preferencesManager.getAppPreferences(this, i));

			Log.e("", "" + pkgArrayList.get(0));

			try
			{
				Drawable appIcon = packageManager.getApplicationIcon(pkgArrayList.get(i));
				imageArrayList.add(appIcon);
			}
			catch (NameNotFoundException e)
			{
				e.printStackTrace();
			}

		}

	}

	private OnClickListener onClickListener = new OnClickListener()
	{

		private Intent intent;

		@Override
		public void onClick( View v )
		{
			if ( v.getId() == 99 )
			{
				intent = new Intent(QuicklicFavoriteActivity.this, ApkListActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
			else
			{
				if ( delEnabled )
				{
					preferencesManager.removeAppPreferences(getApplicationContext(), v.getId() + 1);
					onResume();
				}
				else
				{
					Intent intent = packageManager.getLaunchIntentForPackage(preferencesManager.getAppPreferences(getApplicationContext(), v.getId() + 1));
					startActivity(intent);
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
				Toast.makeText(getApplicationContext(), "삭제가 비활성화 되었습니다.", Toast.LENGTH_SHORT).show();
			}
			else
			{
				delEnabled = true;
				Toast.makeText(getApplicationContext(), "삭제가 활성화 되었습니다.", Toast.LENGTH_SHORT).show();
			}
			return true;
		}
	};

}