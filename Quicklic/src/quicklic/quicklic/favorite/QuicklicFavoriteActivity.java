package quicklic.quicklic.favorite;

import java.util.ArrayList;

import quicklic.floating.api.R;
import quicklic.quicklic.datastructure.Item;
import quicklic.quicklic.main.QuicklicActivity;
import quicklic.quicklic.test.TestingFunction;
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

	private ArrayList<Item> imageArrayList;
	private ArrayList<String> pkgArrayList;
	private PreferencesManager preferencesManager;
	private PackageManager packageManager;
	private boolean delEnabled;
	private int item_count;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_quicklic);

		initialize();
	}

	@Override
	protected void onResume()
	{
		resetQuicklic();
		initializeView();

		super.onResume();
	}

	private void initialize()
	{
		delEnabled = false;
	}

	private void initializeView()
	{
		imageArrayList = new ArrayList<Item>();
		pkgArrayList = new ArrayList<String>();

		getPreference();

		if ( !delEnabled )
			setCenterAddView();
		else
			setCenterDeleteView();

		addViewsForBalance(imageArrayList.size(), imageArrayList, clickListener);
	}

	private void resetQuicklic()
	{
		getQuicklicFrameLayout().removeViews(1, getViewCount());
	}

	private void setCenterAddView()
	{
		System.out.println("add");

		ImageView imageView = new ImageView(this);
		imageView.setBackgroundResource(R.drawable.favorite_add);
		imageView.setOnClickListener(clickListener);
		imageView.setOnLongClickListener(onLongClickListener);

		setCenterView(imageView);
	}

	private void setCenterDeleteView()
	{
		System.out.println("del");

		ImageView imageView = new ImageView(this);
		imageView.setBackgroundResource(R.drawable.favorite_delete);
		imageView.setOnClickListener(clickListener);
		imageView.setOnLongClickListener(onLongClickListener);

		setCenterView(imageView);
	}

	private void getPreference()
	{
		preferencesManager = new PreferencesManager();
		packageManager = getPackageManager();
		item_count = preferencesManager.getNumPreferences(this) + 1;
		for ( int i = 0; i < item_count && !isItemFull(i); i++ )
		{
			pkgArrayList.add(preferencesManager.getAppPreferences(this, i));
			try
			{
				Drawable appIcon = packageManager.getApplicationIcon(pkgArrayList.get(i));
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
					Intent intent = packageManager.getLaunchIntentForPackage(preferencesManager.getAppPreferences(getApplicationContext(), v.getId()));
					startActivity(intent);
					TestingFunction.getFloatingService().setVisibility(true);
					finish();
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
			return true;
		}
	};

}