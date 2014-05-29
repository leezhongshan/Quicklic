package quicklic.quicklic.quicklic.favorite;

import java.util.ArrayList;
import java.util.List;

import quicklic.floating.api.R;
import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ApkListActivity extends Activity implements OnItemClickListener
{
	private PackageManager packageManager;

	private ListView apkList;

	private PreferencesManager preferencesManager;

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favorite);

		packageManager = getPackageManager();
		List<PackageInfo> packageList = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);

		List<PackageInfo> packageList1 = new ArrayList<PackageInfo>();

		/* To filter out System apps */
		for ( PackageInfo pi : packageList )
		{
			boolean b = isSystemPackage(pi);
			if ( !b )
			{
				packageList1.add(pi);
			}
		}
		apkList = (ListView) findViewById(R.id.applist);
		apkList.setAdapter(new ApkAdapter(this, packageList1, packageManager));

		apkList.setOnItemClickListener(this);
	}

	/**
	 * Return whether the given PackgeInfo represents a system package or not. User-installed packages (Market or otherwise) should not be denoted as system packages.
	 * 
	 * @param pkgInfo
	 * @return boolean
	 */
	private boolean isSystemPackage( PackageInfo pkgInfo )
	{
		return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true : false;
	}

	@Override
	public void onItemClick( AdapterView<?> parent, View view, int position, long row )
	{
		PackageInfo packageInfo = (PackageInfo) parent.getItemAtPosition(position);

		preferencesManager = new PreferencesManager();
		preferencesManager.setPreference(packageInfo.packageName, getApplicationContext());

		finish();
	}

}