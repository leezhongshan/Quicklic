package quicklic.quicklic.quicklic;

import android.view.View;
import android.view.View.OnClickListener;

public class QuicklicHardware {

	QuicklicActivity quicklicActivity;

	public QuicklicHardware(QuicklicActivity quicklicActivity)
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