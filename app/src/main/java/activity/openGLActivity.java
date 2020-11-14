package activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.Toast;

import com.suyong.sunshineflat.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import CustomView.GLView;

public class openGLActivity extends AppCompatActivity{

    ImageButton left;
    ImageButton right;
    int currentBitmap = 0;
    int totalSize = 0;

    GLView mView;
    private List<Bitmap> bitmaps = new ArrayList<>();

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void refresh(List<Bitmap> bitmap) {
        bitmaps.addAll(bitmap);
        totalSize = bitmaps.size();
        mView = findViewById(R.id.opengl_view);
        mView.setImage(this,bitmaps.get(currentBitmap%totalSize));
        right = findViewById(R.id.xia_yizhang);
        left = findViewById(R.id.shang_yizhang);
        right.setOnClickListener((v)->{
            currentBitmap++;
            if(currentBitmap==bitmaps.size()) {
                currentBitmap--;
                Toast.makeText(this,"已经是最后一张！", Toast.LENGTH_SHORT).show();
                return;
            }
            mView.setImage(this,bitmaps.get(currentBitmap%totalSize));
        });

        left.setOnClickListener((v)->{
            currentBitmap--;
            if(currentBitmap==-1){
                currentBitmap++;
                Toast.makeText(this,"已经是第一张！", Toast.LENGTH_SHORT).show();
                return;
            }
            mView.setImage(this,bitmaps.get(currentBitmap%totalSize));
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_openglactivity);
        EventBus.getDefault().register(this);

    }



   @Override
   protected void onResume(){
       super.onResume();
       mView.onResume();
   }

   @Override
    protected void onPause(){
        super.onPause();
        mView.onPause();
   }

   @Override
    protected void onDestroy(){
        super.onDestroy();
       EventBus.getDefault().unregister(this);
   }


}
