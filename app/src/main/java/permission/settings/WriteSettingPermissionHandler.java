package permission.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;

import java.util.HashMap;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.ResponseCode;

/**
 * Created by joe on 2017/7/20.
 */

public class WriteSettingPermissionHandler extends BaseHandler
{
    
    public WriteSettingPermissionHandler(@NonNull Context context)
    {
        super(context);
    }
    
    
    public boolean check()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            return Settings.System.canWrite(mContext);
        }
        return true;
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == WriteSettingPermissionParameters.REQUEST_CODE)
        {
            HashMap<String, String> message = new HashMap<>();
            if (check())
            {
                message.put("message", String.valueOf(WriteSettingPermissionParameters.PERMISSION_PASS));
                callBackMessage(ResponseCode.ERR_SUCCESS, WriteSettingPermissionParameters.CLASS_WRITE_SETTING, WriteSettingPermissionParameters.METHOD_CHECK_PERMISSION, message);
            }
            else
            {
                message.put("message", String.valueOf(WriteSettingPermissionParameters.PERMISSION_REJECT));
                callBackMessage(ResponseCode.ERR_NO_SPECIFY_USE_PERMISSION, WriteSettingPermissionParameters.CLASS_WRITE_SETTING, WriteSettingPermissionParameters.METHOD_CHECK_PERMISSION, message);
            }
        }
    }
    
    public void getPermission()
    {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                Uri.parse("package:" + mContext.getPackageName()));
        ((Activity) mContext).startActivityForResult(intent, WriteSettingPermissionParameters.REQUEST_CODE);
    }
}
