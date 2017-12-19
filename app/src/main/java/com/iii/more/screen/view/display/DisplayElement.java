package com.iii.more.screen.view.display;

import org.json.JSONObject;

import sdk.ideas.common.Logs;

/**
 * Created by joe on 2017/7/12
 */

public class DisplayElement
{
    public int timeDuring = -1;
    public int nextTime = -1;
    public String imageURL = null;
    public int backgroundColor = 0;
    public String description = null;
    public JSONObject animation = null;
    public JSONObject text = null;
    
    public DisplayElement(int timeDuring, int nextTime, String imageURL,  int backgroundColor, String description, JSONObject animation, JSONObject text)
    {
        this.animation = animation;
        this.backgroundColor = backgroundColor;
        this.description = description;
        this.imageURL = imageURL;
        this.nextTime = nextTime;
        this.timeDuring = timeDuring;
        this.text = text;
        
    }
    
    public void print()
    {
        Logs.showTrace("[DisplayElement] imageURL:" + imageURL + " color:" + String.valueOf(backgroundColor) + " timeDuring:" + String.valueOf(timeDuring) + " nextTime:" +
                String.valueOf(nextTime) + " url:" + imageURL + " text:" + text.toString() +
                " animation:" + animation.toString());
    }
    
}
