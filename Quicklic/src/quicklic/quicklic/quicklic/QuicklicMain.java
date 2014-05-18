package quicklic.quicklic.quicklic;

import java.util.ArrayList;

import quicklic.floating.api.R;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;

public class QuicklicMain {

	private QuicklicActivity quicklicActivity;
	private QuicklicHardware quicklicHardware;
	private QuicklicFavorite quicklicFavorite;
	private QuicklicScroll quicklicScroll;
	private ArrayList<Drawable> imageList;

	public QuicklicMain(QuicklicActivity quicklicActivity)
	{
		this.quicklicActivity = quicklicActivity;

		init();
	}

	private void init()
	{
		imageList = new ArrayList<Drawable>();

		imageList.add(quicklicActivity.getResources().getDrawable(R.drawable.hardware));
		imageList.add(quicklicActivity.getResources().getDrawable(R.drawable.scroll));
		imageList.add(quicklicActivity.getResources().getDrawable(R.drawable.favorite));

		quicklicActivity.addViewsForBalance(3, imageList, onClickListener);
	}

	private OnClickListener onClickListener = new OnClickListener()
	{

		@Override
		public void onClick( View v )
		{
			if ( v.getId() == 0 )
			{
				quicklicActivity.getQuicklicFrameLayout().removeViews(1, quicklicActivity.getViewCount());

				System.out.println("[Hardware]" + v.getId());

				quicklicHardware = new QuicklicHardware(quicklicActivity);
			}
			else if ( v.getId() == 1 )
			{
				quicklicActivity.getQuicklicFrameLayout().removeViews(1, quicklicActivity.getViewCount());

				System.out.println("[Scroll]" + v.getId());

				quicklicScroll = new QuicklicScroll(quicklicActivity);
			}
			else if ( v.getId() == 2 )
			{
				quicklicActivity.getQuicklicFrameLayout().removeViews(1, quicklicActivity.getViewCount());

				System.out.println("[Favorite]" + v.getId());

				quicklicFavorite = new QuicklicFavorite(quicklicActivity);
			}
		}
	};

}
