package com.iii.more.main.secret;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * 立志做一個可以查詢常數數值的類別
 *
 * @author yubunu <yubunu@gmail.com>
 */
public class ClassConstantValueLookup
{
    private final Map<String, Integer> nameValueMap = new HashMap<>();

    public ClassConstantValueLookup(Class<?> clazz) {
        createValueNameMap(clazz);
    }

    /**
     * 取得 name 所對應的常數數值
     *
     * @return 若 value 存在於本類別，傳回其名稱，否則傳回 "undefined"。
     */
    public int getValue(String name) throws NoSuchElementException {
        Integer v = nameValueMap.get(name);
        if (v == null) {
            throw new NoSuchElementException("Specified name " + name + " not found");
        }

        return v;
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
                    Integer value = (Integer) field.get(null);
                    String name = field.getName();
                    nameValueMap.put(name, value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private ClassConstantValueLookup() {
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
