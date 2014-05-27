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

public class QuicklicFavoriteActivity extends QuicklicActivity {

	private ArrayList<Drawable> imageArrayList;
	private ArrayList<String> pkgArrayList;
	private PreferencesManager preferencesManager;
	private PackageManager packageManager;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_quicklic);
	}

	private void initialize()
	{
		imageArrayList = new ArrayList<Drawable>();

		pkgArrayList = new ArrayList<String>();

		imageArrayList.add(getResources().getDrawable(R.drawable.favorite_test));

		getPreference();

		addViewsForBalance(imageArrayList.size(), imageArrayList, onClickListener);
	}

	@Override
	protected void onResume()
	{
		resetQuicklic();

		initialize();

		super.onResume();
		//		preferencesManager = new PreferencesManager();
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
				// TODO Auto-generated catch block
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
			if ( v.getId() == 0 )
			{
				System.out.println("[Hardware] " + v.getId());
				intent = new Intent(QuicklicFavoriteActivity.this, ApkListActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
			else
			{
				Intent intent = packageManager.getLaunchIntentForPackage(preferencesManager.getAppPreferences(getApplicationContext(), v.getId()));
				startActivity(intent);
			}
		}
	};

}