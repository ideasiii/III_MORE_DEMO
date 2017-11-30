package com.iii.more.main.secret;

import android.util.SparseArray;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
/**
 * 立志做一個可以反向查詢常數名稱的類別
 *
 * @author yubunu <yubunu@gmail.com>
 */
public class ClassConstantValueReverseLookup
{
    private final SparseArray<String> valueNameLookup = new SparseArray<>();

    public ClassConstantValueReverseLookup(Class<?> clazz) {
        createValueNameMap(clazz);
    }

    /**
     * 取得 value 所對應的常數名稱
     *
     * @return 若 value 存在於本類別，傳回其名稱，否則傳回 "undefined"。
     */
    public String getName(int value) {
        return valueNameLookup.get(value, "undefined");
    }

    /** 建立常數數值與其名稱的對應表 */
    private void createValueNameMap(Class<?> clazz) throws IllegalStateException {
        //System.out.println("createValueNameMap w/ class " + clazz.toString());
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getType().getName() != "int") {
                continue;
            }

            int modifier = field.getModifiers();
            if (Modifier.isStatic(modifier) && Modifier.isPublic(modifier) && Modifier.isFinal(modifier)) {
                try {
                    Integer code = (Integer) field.get(null);
                    String name = field.getName();

                    if (codeValueExistsInMap(code)) {
                        throw new IllegalStateException(
                                "Value of constant '" + name + "' (" + code + ") occured more than once in "
                                        + clazz.toString() + ", assign a unique value for each constant");
                    }

                    valueNameLookup.put(code, name);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean codeValueExistsInMap(int code) {
        return valueNameLookup.get(code, null) != null;
    }

    private ClassConstantValueReverseLookup() {
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
