package quicklic.quicklic.quicklic;

import java.util.ArrayList;

import quicklic.floating.api.R;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class QuicklicMainActivity extends QuicklicActivity {

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

		imageArrayList.add(getResources().getDrawable(R.drawable.hardware));
		imageArrayList.add(getResources().getDrawable(R.drawable.scroll));
		imageArrayList.add(getResources().getDrawable(R.drawable.favorite));

		addViewsForBalance(imageArrayList.size(), imageArrayList, clickListener);
	}

	private OnClickListener clickListener = new OnClickListener()
	{
		private Intent intent;

		@Override
		public void onClick( View v )
		{
			if ( v.getId() == 0 )
			{
				System.out.println("[Hardware] " + v.getId());
				intent = new Intent(QuicklicMainActivity.this, QuicklicHardwareActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
			else if ( v.getId() == 1 )
			{
				System.out.println("[Scroll] " + v.getId());
				intent = new Intent(QuicklicMainActivity.this, QuicklicScrollActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
			else if ( v.getId() == 2 )
			{
				System.out.println("[Favorite] " + v.getId());
				intent = new Intent(QuicklicMainActivity.this, QuicklicFavoriteActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		}
	};

}
