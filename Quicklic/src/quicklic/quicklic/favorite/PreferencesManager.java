package quicklic.quicklic.favorite;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager extends Activity
{
	private SharedPreferences pref;
	private SharedPreferences.Editor editor;

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

	/**
	 * @함수명 : getNumPreferences
	 * @매개변수 : Context context
	 * @반환 : int
	 * @기능(역할) : Favorite에 등록 되어진 어플리케이션의 개수를 반환
	 * @작성자 : JHPark
	 * @작성일 : 2014. 5. 20.
	 */
	public int getNumPreferences( Context context )
	{
		return pref.getInt("appNum", 0);
	}

	/**
	 * @함수명 : setPreference
	 * @매개변수 : String pkgName, Context context
	 * @반환 : void
	 * @기능(역할) : Favorite에 사용되는 어플리케이션 추가
	 * @작성자 : JHPark
	 * @작성일 : 2014. 5. 20.
	 */
	public void setPreference( String pkgName, Context context )
	{
		int num = getNumPreferences(context);

		editor.putString("appData" + num, pkgName);
		editor.putInt("appNum", ++num);
		editor.commit();
	}

	/**
	 * @함수명 : getAppPreferences
	 * @매개변수 : Context context, int pkgNum
	 * @반환 : String
	 * @기능(역할) : Favorite에서 선택된 어플리케이션의 PackageName을 반환
	 * @작성자 : JHPark
	 * @작성일 : 2014. 5. 20.
	 */
	public String getAppPreferences( Context context, int pkgNum )
	{
		return pref.getString("appData" + pkgNum, null);
	}

	/**
	 * @함수명 : removeAppPreferences
	 * @매개변수 : Context context, int pkgNum
	 * @반환 : void
	 * @기능(역할) : Favorite에서 선택된 어플리케이션 삭제
	 * @작성자 : JHPark
	 * @작성일 : 2014. 5. 20.
	 */
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