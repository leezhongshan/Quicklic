package quicklic.quicklic.quicklic;

import java.util.ArrayList;

import quicklic.floating.api.R;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class QuicklicFavoriteActivity extends QuicklicActivity {

	private ArrayList<Drawable> imageArrayList;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_quicklic);

		initialize();
	}

	private void initialize()
	{
		imageArrayList = new ArrayList<Drawable>();

		imageArrayList.add(getResources().getDrawable(R.drawable.favorite_test));

		addViewsForBalance(imageArrayList.size(), imageArrayList, onClickListener);
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