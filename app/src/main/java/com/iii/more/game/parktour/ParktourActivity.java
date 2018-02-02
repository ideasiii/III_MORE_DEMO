package com.iii.more.game.parktour;

import android.app.Activity;
import android.os.Bundle;

import com.iii.more.game.module.RobotHead;
import com.iii.more.game.module.TrackerHandler;
import com.iii.more.game.module.Utility;
import com.iii.more.main.MainApplication;
import com.iii.more.main.R;

public class ParktourActivity extends Activity
{
    private MainApplication application = null;
    private RobotHead robotHead = null;
    public static TrackerHandler trackerHandler = null;
    private FaceView faceView = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Utility.fullScreenNoBar(this);
        faceView = new FaceView(this);
        setContentView(faceView);
        faceView.setBackgroundResource(R.drawable.iii_zoo_101);
    }
}
