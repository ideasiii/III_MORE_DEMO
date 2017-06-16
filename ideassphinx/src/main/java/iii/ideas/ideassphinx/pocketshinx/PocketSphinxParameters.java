package iii.ideas.ideassphinx.pocketshinx;

/**
 * Created by joe on 2017/4/11.
 */

public abstract class PocketSphinxParameters
{
    public static final int CLASS_POCKET_SPHINX = 7897;
    public static final int METHOD_POCKET_SPHINX = 0;
    
    public static String KEY_PHRASE = "hi ideas";
    public static String LANGUAGE_MODEL = "en";
    
    public static final String LANGUAGE_MODEL_EN = "en";
    public static final String LANGUAGE_MODEL_ZH = "zh-tw";
    
    //range is 1e-45f ~ 1
    public static final float MIN_THRESHOLD = 1e-45f;
    public static final float MAX_THRESHOLD = 1;
    public static float KEY_PHRASE_THRESHOLD = 1e-15f;
    
}
