package quicklic.quicklic.quicklic;

import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class QuicklicFavorite {

	QuicklicActivity quicklicActivity;

	public QuicklicFavorite(QuicklicActivity quicklicActivity)
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