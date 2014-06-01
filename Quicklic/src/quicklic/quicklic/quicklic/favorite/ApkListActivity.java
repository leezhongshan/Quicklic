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
	private ApkAdapter apkAdapter;
	private ListView apkList;

	private PreferencesManager preferencesManager;

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favorite);

		initialize();
		initializeApkList();
	}

	private void initialize()
	{
		apkList = (ListView) findViewById(R.id.applist);
		packageManager = getPackageManager();
		preferencesManager = new PreferencesManager();
	}

	private void initializeApkList()
	{
		List<PackageInfo> packageList = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);
		List<PackageInfo> packageTempList = new ArrayList<PackageInfo>();

		/* To filter out the System Application */
		for ( PackageInfo packageInfo : packageList )
		{
			if ( !isSystemPackage(packageInfo) )
			{
				packageTempList.add(packageInfo);
			}
		}
		apkAdapter = new ApkAdapter(this, R.layout.apklist_item, packageTempList, packageManager);
		apkList.setAdapter(apkAdapter);
		apkList.setOnItemClickListener(this);
	}

	/**
	 * @함수명 : isSystemPackage
	 * @매개변수 :
	 * @반환 : boolean
	 * @기능(역할) : Return whether the given PackgeInfo represents a system package or not. User-installed packages (Market or otherwise) should not be denoted as system packages.
	 * @작성자 : 13 JHPark
	 * @작성일 : 2014. 6. 2.
	 */
	private boolean isSystemPackage( PackageInfo pkgInfo )
	{
		return (pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
	}

	@Override
	public void onItemClick( AdapterView<?> parent, View view, int position, long row )
	{
		PackageInfo packageInfo = (PackageInfo) parent.getItemAtPosition(position);
		preferencesManager.setPreference(packageInfo.packageName, getApplicationContext());
		finish();
	}
}