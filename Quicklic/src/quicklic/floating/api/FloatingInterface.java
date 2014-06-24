package quicklic.floating.api;

import android.view.View;

public interface FloatingInterface
{
	public void setContext( FloatingService floatingServices );

	/**
	 * @함수명 : setDrawableQuiclic
	 * @매개변수 :
	 * @반환 : int
	 * @기능(역할) : Quicklic 이미지 설정
	 * @작성자 : THYang
	 * @작성일 : 2014. 6. 25.
	 */
	public int setDrawableQuicklic();

	/**
	 * @함수명 : setSize
	 * @매개변수 :
	 * @반환 : float
	 * @기능(역할) : Floating 이미지 크기 지정
	 * @작성자 : JHPark
	 * @작성일 : 2014. 6. 25.
	 */
	public float setSize();

	/**
	 * @함수명 : setAnimation
	 * @매개변수 :
	 * @반환 : boolean
	 * @기능(역할) : 처음 Animation 사용 설정
	 * @작성자 : JHPark
	 * @작성일 : 2014. 6. 25.
	 */
	public boolean setAnimation();

	public void touched( View v );

	public void doubleTouched( View v );

	public void longTouched( View v );

	public void setQuicklicVisibility( boolean bool );
}
