package quicklic.quicklic.favorite;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager extends Activity
{
	SharedPreferences pref;
	SharedPreferences.Editor editor;

	public PreferencesManager(Context context)
	{
		pref = context.getSharedPreferences("appData", MODE_PRIVATE);
		editor = pref.edit();
		if ( pref.getInt("appNum", -1) < 0 )
		{
			editor.putInt("appNum", 0);
			editor.commit();
		}
	}

	public int getNumPreferences( Context context )
	{
		return pref.getInt("appNum", 0);
	}

	public void setPreference( String pkgName, Context context )
	{
		int num = getNumPreferences(context);

		editor.putString("appData" + num, pkgName);
		editor.putInt("appNum", ++num);
		editor.commit();
	}

	public String getAppPreferences( Context context, int pkgNum )
	{
		return pref.getString("appData" + pkgNum, null);
	}

	public void removeAppPreferences( Context context, int pkgNum )
	{
		int num = getNumPreferences(context);

		for ( int j = pkgNum; j < num; j++ )
		{
			editor.putString("appData" + j, getAppPreferences(context, j + 1));
		}
		editor.remove("appData" + num);
		editor.putInt("appNum", --num);
		editor.commit();
	}
}