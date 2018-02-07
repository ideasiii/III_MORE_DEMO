package com.iii.more.interrupt.logic;

/**
 * Created by joe on 2017/9/22.
 */

public abstract class InterruptLogicParameters
{
    public static final int CLASS_INTERRUPT_LOGIC = 5468;
    public static final int METHOD_LOGIC_RESPONSE = 0;
    public static final int METHOD_EMOTION_LOGIC_RESPONSE = 1;

    public static final String DEFAULT_LOGIC_BEHAVIOR_DATA = "";

    public static final String DEFAULT_SIMULATOR_EVENT_DATA ="f1[0],f2[0],C[0],D[0],H[255],FSR1[0],FSR2[0],X[0],Y[0],Z[0]";
    public static final String PATTERN_EVENT_DATA = "f1\\[([01])\\],f2\\[([01])\\],C\\[([01])\\],D\\[([01])\\],H\\[([0123456789]{1,3})\\],FSR1\\[(\\d+)\\],FSR2\\[(\\d+)\\],X\\[([\\d\\.-]+)\\],Y\\[([\\d\\.-]+)\\],Z\\[([\\d\\.-]+)\\]";
    public static final String PATTERN_EVENT_RFID_DATA ="RFID\\[(\\d+)\\]";

    public static final String STRING_RFID = "RFID";
    public static final String STRING_F1 = "f1";
    public static final String STRING_F2 = "f2";
    public static final String STRING_C = "C";
    public static final String STRING_D = "D";
    public static final String STRING_H = "H";
    public static final String STRING_FSR1 = "FSR1";
    public static final String STRING_FSR2 = "FSR2";

    public static final int INT_F1 = 1;
    public static final int INT_F2 = 2;
    public static final int INT_C = 3;
    public static final int INT_D = 4;
    public static final int INT_H = 5;
    public static final int INT_FSR1 = 6;
    public static final int INT_FSR2 = 7;

    public static final int TRIGGER_RULE_TRUE_ONE = 1;
    public static final int TRIGGER_RULE_TRUE_TWO = 2;

    public static final String JSON_STRING_SENSORS = "sensors";
    public static final String JSON_STRING_TRIGGER_RULE = "trigger_rule";
    public static final String JSON_STRING_ACTION_PRIORITY = "action";
    public static final String JSON_STRING_TAG = "tag";
    public static final String JSON_STRING_TRIGGER_RESULT = "trigger";
    public static final String JSON_STRING_VALUE = "value";
    public static final String JSON_STRING_DESCRIPTION = "desc";



    public static final float LOW_BOUND_EMOTION_VALUE = 20.0f;

    /** 臉頰的 sensor 判定為遭受按壓的最小閾值 */
    public static final double SENSOR_CHEEK_TRIGGER_THRESHOLD = 30.0;
    /** 光感判定為有開燈的最小閾值 */
    public static final double SENSOR_AMBIENT_LIGHT_TRIGGER_THRESHOLD = 255.0;
    /** 觸手 sensor 判定為有動作的的最大閾值 */
    public static final double SENSOR_HAND_TRIGGER_THRESHOLD = 20000;
}
