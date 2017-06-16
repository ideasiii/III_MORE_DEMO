
package iii.ideas.ideassphinx.pocketshinx;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.ListenReceiverAction;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;


public class PocketSphinxHandler extends BaseHandler implements
        RecognitionListener, ListenReceiverAction
{
    private static final String KWS_SEARCH = "wakeup";
    
    private SpeechRecognizer recognizer = null;
    private boolean isStart = false;
    
    public PocketSphinxHandler(Context context)
    {
        super(context);
    }
    
    public void setKeyWord(String keyWord)
    {
        PocketSphinxParameters.KEY_PHRASE = keyWord;
    }
    
    public void setLanguageLocation(String location)
    {
        PocketSphinxParameters.LANGUAGE_MODEL = location;
    }
    
    public static void setKeyWordThreshold(float threshold)
    {
        if (threshold <= PocketSphinxParameters.MIN_THRESHOLD)
        {
            PocketSphinxParameters.KEY_PHRASE_THRESHOLD = PocketSphinxParameters.MIN_THRESHOLD;
        }
        else if (threshold >= PocketSphinxParameters.MAX_THRESHOLD)
        {
            PocketSphinxParameters.KEY_PHRASE_THRESHOLD = PocketSphinxParameters.MAX_THRESHOLD;
        }
        else
        {
            PocketSphinxParameters.KEY_PHRASE_THRESHOLD = threshold;
        }
        Logs.showTrace("[PocketSphinxHandler] now Theshold");
    }
    
    private void runRecognizerSetup()
    {
        new AsyncTask<Void, Void, Exception>()
        {
            @Override
            protected Exception doInBackground(Void... params)
            {
                try
                {
                    Assets assets = new Assets(PocketSphinxHandler.super.mContext);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                }
                catch (IOException e)
                {
                    return e;
                }
                return null;
            }
            
            @Override
            protected void onPostExecute(Exception result)
            {
                if (result != null)
                {
                    HashMap<String, String> message = new HashMap<>();
                    message.put("message", result.getMessage());
                    callBackMessage(ResponseCode.ERR_UNKNOWN, PocketSphinxParameters.CLASS_POCKET_SPHINX, PocketSphinxParameters.METHOD_POCKET_SPHINX, message);
                    
                }
                else
                {
                    switchSearch(KWS_SEARCH);
                }
            }
        }.execute();
    }
    
    
    @Override
    public void onPartialResult(Hypothesis hypothesis)
    {
        if (hypothesis == null)
        {
            return;
        }
        
        String text = hypothesis.getHypstr();
        if (text.equals(PocketSphinxParameters.KEY_PHRASE))
        {
            Logs.showTrace("[PocketSphinxHandler]Sphinx get****" + PocketSphinxParameters.KEY_PHRASE + "****");
            isStart = false;
            recognizer.stop();
            recognizer.shutdown();
        }
        else
        {
            Logs.showTrace("[PocketSphinxHandler]Sphinx got:"+text);
        }
    }
    
    
    @Override
    public void onResult(Hypothesis hypothesis)
    {
        if (hypothesis != null)
        {
            String text = hypothesis.getHypstr();
            HashMap<String, String> message = new HashMap<>();
            message.put("message", text);
            callBackMessage(ResponseCode.ERR_SUCCESS, PocketSphinxParameters.CLASS_POCKET_SPHINX, PocketSphinxParameters.METHOD_POCKET_SPHINX, message);
        }
    }
    
    @Override
    public void onBeginningOfSpeech()
    {
    }
    
    
    @Override
    public void onEndOfSpeech()
    {
        
    }
    
    private void switchSearch(String searchName)
    {
        recognizer.stop();
        
        if (searchName.equals(KWS_SEARCH))
        {
            recognizer.startListening(searchName);
        }
    }
    
    private void setupRecognizer(File assetsDir) throws IOException
    {
        String acousticModel = "";
        String dictionary = "";
        switch (PocketSphinxParameters.LANGUAGE_MODEL)
        {
            case PocketSphinxParameters.LANGUAGE_MODEL_EN:
                acousticModel = "en-us-ptm";
                dictionary = "cmudict-en-us.dict";
                break;
            case PocketSphinxParameters.LANGUAGE_MODEL_ZH:
                acousticModel = "zh-tw-ptm";
                dictionary = "cmudict-zh-tw.dic";
                break;
            default:
                acousticModel = "en-us-ptm";
                dictionary = "cmudict-en-us.dict";
                break;
        }
        
        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, acousticModel))
                .setDictionary(new File(assetsDir, dictionary))
                .setRawLogDir(assetsDir)
                .setKeywordThreshold(PocketSphinxParameters.KEY_PHRASE_THRESHOLD)
                .getRecognizer();
        recognizer.addListener(this);
        recognizer.addKeyphraseSearch(KWS_SEARCH, PocketSphinxParameters.KEY_PHRASE);
    }
    
    @Override
    public void onError(Exception error)
    {
        HashMap<String, String> message = new HashMap<>();
        message.put("message", error.getMessage());
        callBackMessage(ResponseCode.ERR_UNKNOWN, PocketSphinxParameters.CLASS_POCKET_SPHINX, PocketSphinxParameters.METHOD_POCKET_SPHINX, message);
        
    }
    
    @Override
    public void onTimeout()
    {
        switchSearch(KWS_SEARCH);
    }
    
    
    @Override
    public void startListenAction()
    {
        runRecognizerSetup();
    }
    
    public void startListenAction(float threshold)
    {
        Logs.showTrace("startListenAction : isStart " + String.valueOf(isStart));
        if (isStart == false)
        {
            isStart = true;
            setKeyWordThreshold(threshold);
            runRecognizerSetup();
            
        }
        else
        {
            Logs.showError("stop it first!");
        }
        
    }
    
    @Override
    public void stopListenAction()
    {
        Logs.showTrace("stopListenAction : isStart " + String.valueOf(isStart));
        if (isStart == true)
        {
            if (null != recognizer)
            {
                isStart = false;
                recognizer.stop();
                recognizer.cancel();
                recognizer.shutdown();
            }
        }
        else
        {
            Logs.showError("start it first");
        }
    }
}
