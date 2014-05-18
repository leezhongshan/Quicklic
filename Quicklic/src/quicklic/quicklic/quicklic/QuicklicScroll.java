package quicklic.quicklic.quicklic;

import android.view.View;
import android.view.View.OnClickListener;

public class QuicklicScroll {

	QuicklicActivity quicklicActivity;

	public QuicklicScroll(QuicklicActivity quicklicActivity)
	{
		this.quicklicActivity = quicklicActivity;
		quicklicActivity.addViewsForBalance(8, onClickListener);
	}

	private OnClickListener onClickListener = new OnClickListener()
	{
		@Override
		public void onClick( View v )
		{
			//TODO
		}
	};

}