package com.iii.more.oobe.logic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sdk.ideas.common.Logs;

/**
 * Created by joe on 2017/11/2.
 */

public class OobeLogicElement
{
    public int state = -1;
    public String tts = "";
    public String imageFile = "";
    public String response = "";
    public String movie = "";
    public int wait = 0;
    private int regretTime = 3;
    
    public OobeLogicElement(int state, int wait, int regretTime, String tts, String imageFile, String
            response, String movie)
    {
        this.wait = wait;
        this.state = state;
        this.tts = tts;
        this.response = response;
        this.movie = movie;
        this.imageFile = imageFile;
        this.regretTime = regretTime;
    }
    
    public int getRegret()
    {
        Logs.showTrace("[OobeLogicElement]");
        regretTime--;
        Logs.showTrace("[OobeLogicElement] now regret: " + String.valueOf(regretTime));
        return regretTime;
    }
    
    public String getDisplayJsonString()
    {
        if (!imageFile.isEmpty())
        {
            JSONObject display = null;
            try
            {
                JSONObject animate = new JSONObject();
                animate.put("type", 2);
                
                animate.put("duration", 3000);
                animate.put("repeat", 0);
                animate.put("interpolate", 1);
                
                //create display json
                JSONObject data = new JSONObject();
                data.put("time", 0);
                data.put("host", "https://ryejuice.sytes.net/edubot/OCTOBO_Expressions/");
                data.put("color", "#FFA0C9EC");
                data.put("description", "快樂");
                data.put("animation", animate);
                data.put("text", new JSONObject());
                data.put("file", imageFile);
                JSONArray show = new JSONArray();
                show.put(data);
                display = new JSONObject();
                display.put("enable", 1);
                display.put("show", show);
                
                return display.toString();
            }
            catch (JSONException e)
            {
                return null;
            }
        }
        
        return null;
    }
    
    public void print()
    {
        Logs.showTrace("[OobeLogicHandler] Element: state: " + String.valueOf(state) + " tts:" + tts + " "
                + "imgFile: " + imageFile + " movie:" + movie);
        
    }
    
    
}
