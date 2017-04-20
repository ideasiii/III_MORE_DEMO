
package com.iii.more.pocketshinx;

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
    /* Named searches allow to quickly reconfigure the decoder */
    private static final String KWS_SEARCH = "wakeup";
    
    /* Keyword we are looking for to activate menu */
    
    
    private SpeechRecognizer recognizer = null;
    
    
    public PocketSphinxHandler(Context context)
    {
        super(context);
    }
    // private static boolean isListening = false;
    
    public void setKeyWord(String keyWord)
    {
        PocketSphinxParameters.KEY_PHRASE = keyWord;
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
    }
    
    private void runRecognizerSetup()
    {
        // if(isListening == false)
        {
            // Recognizer initialization is a time-consuming and it involves IO,
            // so we execute it in async task
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
            //   isListening = true;
        }
    }
    
    
    /**
     * In partial result we get quick updates about current hypothesis. In
     * keyword spotting mode we can react here, in other modes we need to wait
     * for final result in onResult.
     */
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
            Logs.showTrace("Sphinx get****" + PocketSphinxParameters.KEY_PHRASE + "****");
            recognizer.stop();
            recognizer.shutdown();
        }
    }
    
    /**
     * This callback is called when we stop the recognizer.
     */
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
    
    /**
     * We stop recognizer here to get a final result
     */
    @Override
    public void onEndOfSpeech()
    {
        if (!recognizer.getSearchName().equals(KWS_SEARCH))
        {
            // switchSearch(KWS_SEARCH);
            
        }
    }
    
    private void switchSearch(String searchName)
    {
        recognizer.stop();
        
        // If we are not spotting, start listening with timeout (10000 ms or 10 seconds).
        if (searchName.equals(KWS_SEARCH))
        {
            recognizer.startListening(searchName);
        }
    }
    
    private void setupRecognizer(File assetsDir) throws IOException
    {
        
        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                .setRawLogDir(assetsDir) // To disable logging of raw audio comment out this call (takes a lot of space on the device)
                .setKeywordThreshold(PocketSphinxParameters.KEY_PHRASE_THRESHOLD)
                .getRecognizer();
        recognizer.addListener(this);
        // Create keyword-activation search.
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
    
    @Override
    public void stopListenAction()
    {
        if (null != recognizer)
        {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }
}
