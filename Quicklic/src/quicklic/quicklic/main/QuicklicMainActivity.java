package quicklic.quicklic.main;

import java.util.ArrayList;

import quicklic.floating.api.R;
import quicklic.quicklic.favorite.QuicklicFavoriteActivity;
import quicklic.quicklic.hardware.QuicklicHardwareActivity;
import quicklic.quicklic.scrollkey.QuicklicScrollKeyService;
import quicklic.quicklic.test.TestingFunction;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class QuicklicMainActivity extends QuicklicActivity {

	private ArrayList<Drawable> imageArrayList;

	private boolean isScrollService;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		// Activity가 생성될 때, Floating Image를 사라지게 함
		TestingFunction.getFloatingService().setVisibility(false);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_quicklic);

		initialize();
	}

	@Override
	protected void onResume()
	{
		// Activity가 재실행되었을 때, Main Layout이 보여지게 함.
		getQuicklicFrameLayout().setVisibility(View.VISIBLE);
		super.onResume();
	}

	@Override
	protected void onPause()
	{
		// Activity가 재실행되었을 때, Main Layout이 보여지게 함.
		getQuicklicFrameLayout().setVisibility(View.GONE);
		super.onPause();
	}

	@Override
	protected void onDestroy()
	{
		// Activity가 제거될 때, Floating Image를 보여지게 함
		System.out.println("Main Des");

		if ( !isScrollService )
			TestingFunction.getFloatingService().setVisibility(true);

		super.onDestroy();
	}

	/**
	 * @함수명 : onBackPressed
	 * @매개변수 :
	 * @기능(역할) : 뒤로가기 버튼을 눌렀을 때, quicklic 뷰가 다시 생성되도록 함
	 * @작성자 : JHPark
	 * @작성일 : 2014. 5. 9.
	 */
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
	}

	private void initialize()
	{
		isScrollService = false;

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
				isScrollService = true;

				System.out.println("[Scroll] " + v.getId());
				intent = new Intent(QuicklicMainActivity.this, QuicklicScrollKeyService.class);
				intent.putExtra("deviceWidth", getDeviceWidth());
				intent.putExtra("deviceHeight", getDeviceHeight());
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startService(intent);
				finish();
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
