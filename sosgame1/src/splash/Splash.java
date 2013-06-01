package splash;

import com.example.sosgame1.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Splash extends SurfaceView implements SurfaceHolder.Callback{

	private Bitmap test;

	private Context context;
	
	public Splash(Context context) {
		super(context);
		this.context = context;
		
		test = BitmapFactory.decodeResource(context.getResources(), R.drawable.test);
		
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);	
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}



}
