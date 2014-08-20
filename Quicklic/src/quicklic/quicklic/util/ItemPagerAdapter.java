/**
 * Product by Kyle
 */
package quicklic.quicklic.util;

import java.util.ArrayList;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;

public class ItemPagerAdapter extends PagerAdapter {

	private int pagerCount = 0;
	private ArrayList<FrameLayout> quickPageArrayList;

	public ItemPagerAdapter(Context context, int pagerCount, ArrayList<FrameLayout> quickPageArrayList)
	{
		super();

		this.pagerCount = pagerCount;
		this.quickPageArrayList = quickPageArrayList;
	}

	@Override
	public int getCount()
	{
		return pagerCount;
	}

	@Override
	public boolean isViewFromObject( View view, Object object )
	{
		return view == object;
	}

	@Override
	public Object instantiateItem( View view, int position )
	{
		((ViewPager) view).addView(quickPageArrayList.get(position), 0);

		return quickPageArrayList.get(position);
	}

	@Override
	public void destroyItem( View view, int position, Object object )
	{
		((ViewPager) view).removeView((View) object);
	}

	@Override
	public void restoreState( Parcelable arg0, ClassLoader arg1 )
	{
	}

	@Override
	public Parcelable saveState()
	{
		return null;
	}

	@Override
	public void startUpdate( View arg0 )
	{
	}

	@Override
	public void finishUpdate( View arg0 )
	{
	}
}
