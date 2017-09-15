package com.iii.more.dmp;

/**
 * Created by joe on 2017/8/22.
 */

public class DMPParameters
{
    public static final int CLASS_DMP = 9452;
    public static final int METHOD_INIT = 0;
    public static final int METHOD_BIND = 1;
    public static final int METHOD_EXPRESSION = 2;
    public static final int METHOD_REBIND = 3;
    
    
    public static final String JSON_STRING_DEVICE_ID = "device_id";
    public static final String JSON_STRING_UUID = "imei";
    public static final String JSON_STRING_MESSAGE = "message";
    public static final String JSON_STRING_VERSION = "version";
            
            
    public static class DMP_HEADER
    {
        public int command_length;
        public int command_id;
        public int command_status;
    }
    
    public static class DMP_PACKET
    {
        public DMP_HEADER dmpHeader = new DMP_HEADER();
        public String dmpBody;
    }
    
    
    public static final int REQUEST_BIND = 0x00000001;
    public static final int RESPONSE_BIND = 0x00000002;
    public static final int REQUEST_EXPRESSION_PUSH = 0x00000003;
    public static final int RESPONSE_EXPRESSION_PUSH = 0x00000004;
    
    public static final int REQUEST_ENQUIRE_LINK = 0x00000005;
    public static final int RESPONSE_ENQUIRE_LINK = 0x00000006;
    
    
    public static final int DMP_HEADER_SIZE = 12;
    public static final String BODY_CODE_TYPE = "UTF-8";
    
    public static final int STATUS_SUCCESS = 0x00000001;
    public static final int STATUS_FAIL = 0x00000000;
    public static final int STATUS_ERR_IO_EXCEPTION = -1;
    public static final int STATUS_ERR_SOCKET_INVALID = -2;
    public static final int STATUS_ERR_PACKET_CONVERT = -3;
    public static final int STATUS_ERR_PACKET_LENGTH = -4;
    public static final int STATUS_ERR_EXCEPTION = -5;
    public static final int STATUS_ERR_INVALID_PARAM = -6;
    
}
