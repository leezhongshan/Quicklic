package quicklic.quicklic.main;

import java.util.ArrayList;

import quicklic.floating.api.R;
import quicklic.quicklic.datastructure.Item;
import quicklic.quicklic.favorite.QuicklicFavoriteActivity;
import quicklic.quicklic.hardware.QuicklicHardwareActivity;
import quicklic.quicklic.scrollkey.QuicklicScrollKeyService;
import quicklic.quicklic.test.SettingFloatingInterface;
import quicklic.quicklic.util.QuicklicActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class QuicklicMainActivity extends QuicklicActivity {

	private final int HARDWARE = 0;
	private final int SCROLL = 1;
	private final int FAVORITE = 2;

	private ArrayList<Item> imageArrayList;
	private boolean isScrollService;
	private boolean isNotHomeKey;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		// Activity가 생성될 때, Floating Image를 사라지게 함
		SettingFloatingInterface.getFloatingService().setVisibility(false);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_quicklic);

		initialize();
	}

	protected void onResume()
	{
		isNotHomeKey = false;
		// Activity가 재실행되었을 때, Main Layout이 보여지게 함.
		if ( SettingFloatingInterface.getFloatingService().getQuicklic().getVisibility() == View.VISIBLE )
		{
			getQuicklicFrameLayout().setVisibility(View.INVISIBLE);
		}
		else
		{
			getQuicklicFrameLayout().setVisibility(View.VISIBLE);
		}
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
		if ( !isScrollService )
			SettingFloatingInterface.getFloatingService().setVisibility(true);

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
		isNotHomeKey = false;
		imageArrayList = new ArrayList<Item>();

		imageArrayList.add(new Item(HARDWARE, R.drawable.hardware));
		imageArrayList.add(new Item(SCROLL, R.drawable.scroll));
		imageArrayList.add(new Item(FAVORITE, R.drawable.favorite));

		addViewsForBalance(imageArrayList.size(), imageArrayList, clickListener);
	}

	private OnClickListener clickListener = new OnClickListener()
	{
		private Intent intent;

		@Override
		public void onClick( View v )
		{
			isNotHomeKey = true;

			switch ( v.getId() )
			{
			case HARDWARE:
				intent = new Intent(QuicklicMainActivity.this, QuicklicHardwareActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				break;

			case SCROLL:
				isScrollService = true;

				intent = new Intent(QuicklicMainActivity.this, QuicklicScrollKeyService.class);
				intent.putExtra("deviceWidth", getDeviceWidth());
				intent.putExtra("deviceHeight", getDeviceHeight());
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startService(intent);
				finish();
				break;

			case FAVORITE:
				intent = new Intent(QuicklicMainActivity.this, QuicklicFavoriteActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				getQuicklicFrameLayout().setVisibility(View.INVISIBLE);
				startActivityForResult(intent, 2);
				break;

			default:
				break;
			}
		}
	};

	protected void onUserLeaveHint()
	{
		if ( !isNotHomeKey )
		{
			homeKeyPressed();
		}
	};
}
