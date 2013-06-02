package splash;

import com.example.sosgame1.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Splash {

	private Bitmap test;

	private Context context;
	
	public Splash() {
		this.context = context;
		
		test = BitmapFactory.decodeResource(context.getResources(), R.drawable.test);

	}




}
