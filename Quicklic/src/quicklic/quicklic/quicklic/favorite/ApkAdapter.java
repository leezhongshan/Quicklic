package quicklic.quicklic.quicklic.favorite;

import java.util.List;

import quicklic.quicklic.util.ViewHolder;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ApkAdapter extends BaseAdapter
{
	List<PackageInfo> packageList;
	Activity context;
	PackageManager packageManager;

	public ApkAdapter(Activity context, List<PackageInfo> packageList, PackageManager packageManager)
	{
		super();
		this.context = context;
		this.packageList = packageList;
		this.packageManager = packageManager;
	}

	public int getCount()
	{
		return packageList.size();
	}

	public Object getItem( int position )
	{
		return packageList.get(position);
	}

	public long getItemId( int position )
	{
		return position;
	}

	public View getView( int position, View convertView, ViewGroup parent )
	{
		ViewHolder holder;
		LayoutInflater inflater = context.getLayoutInflater();

		if ( convertView == null )
		{
			//            convertView = inflater.inflate(R.layout.apklist_item, null);
			//            holder = new ViewHolder();
			//            
			//            holder.apkName = (TextView) convertView.findViewById(R.id.appname);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		PackageInfo packageInfo = (PackageInfo) getItem(position);
		Drawable appIcon = packageManager.getApplicationIcon(packageInfo.applicationInfo);
		String appName = packageManager.getApplicationLabel(packageInfo.applicationInfo).toString();
		appIcon.setBounds(0, 0, 80, 80);
		//        holder.apkName.setCompoundDrawables(appIcon, null, null, null);
		//        holder.apkName.setCompoundDrawablePadding(5);
		//        holder.apkName.setText(appName);

		return convertView;
	}
}