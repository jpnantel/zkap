package ca.crim.horcs.utils.lang;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Collection;

public class ReflectionUtils {

    public static Method getSetterMethod(Object object, String attribute) {
        String methodName = "set" + attribute.substring(0, 1).toUpperCase() + attribute.substring(1);
        return searchMethod(object, methodName, new Object[] { new Object() });
    }

    public static Object getAttribute(Object object, String attribute) {
        Method getter = getGetterMethod(object, attribute);
        if (getter == null) {
            throw new RuntimeException("Getter " + attribute + " not found for object " + object);
        }
        Object result = null;
        try {
            result = getter.invoke(object, new Object[] {});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Method getGetterMethod(Object object, String attribute) {
        String methodName;
        if (attribute.equals("toString")) {
            methodName = attribute;
        } else {
            methodName = "get" + attribute.substring(0, 1).toUpperCase() + attribute.substring(1);
        }
        Method method = searchMethod(object, methodName, new Object[] {});
        if (method == null) {
            methodName = "is" + attribute.substring(0, 1).toUpperCase() + attribute.substring(1);
            method = searchMethod(object, methodName, new Object[] {});
        }
        return method;
    }

    public static Method searchMethod(Object object, String methodName, Object[] argsValues) {
        if (object == null) {
            throw new InvalidParameterException("Invalid parameter for method 'searchMethod'. object=" + object
                    + ",methodName=" + methodName);
        }

        // Compile args types
        Class<?>[] argsClazz = null;
        if (argsValues != null) {
            argsClazz = new Class[argsValues.length];
            for (int i = 0; i < argsValues.length; i++) {
                if (argsValues[i] != null) {
                    argsClazz[i] = argsValues[i].getClass();
                }
            }
        }
        // Find a matching method
        // - Same parameters types
        // - OR Same parameters count
        Method foundMethod = null;
        int foundMatch = 0;
        for (Method m : object.getClass().getMethods()) {
            if (m.getName().equals(methodName)) {
                Class<?>[] parameterTypes = m.getParameterTypes();
                int match = arrayContentsMatch(parameterTypes, argsClazz);
                if (argsClazz == null && parameterTypes.length == 0) {
                    foundMethod = m;
                } else if (parameterTypes.length == argsClazz.length) {
                    if (foundMethod == null || match > foundMatch) {
                        foundMatch = match;
                        foundMethod = m;
                    }
                }
            }
        }
        return foundMethod;
    }

    private static boolean isObjectSubTypeOfClazz(Object object, Class<?> clazz) {
        Class<?> objectClazz = object.getClass();
        for (Class<?> clazzItem : objectClazz.getClasses()) {
            if (clazz.equals(clazzItem)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Object convertObject(Object value, Class clazz) {
        Object result;
        if (value == null || clazz == null) {
            result = null;
        } else if (clazz.equals(value.getClass()) || isObjectSubTypeOfClazz(value, clazz)) {
            result = value;
        } else if (clazz.isArray() && value instanceof Object[]) {
            result = Arrays.copyOf((Object[]) value, ((Object[]) value).length, clazz);
        } else if (clazz.isArray() && value instanceof Collection) {
            result = ((Collection) value).toArray((Object[]) Array.newInstance(clazz.getComponentType(), 0));
        } else {
            String strValue = value.toString();
            if (clazz.equals(Integer.class) || clazz.equals(int.class)) {
                result = new Integer(strValue);
            } else if (clazz.equals(Double.class) || clazz.equals(double.class)) {
                result = new Double(strValue);
            } else if (clazz.equals(Float.class) || clazz.equals(float.class)) {
                result = new Float(strValue);
            } else if (clazz.equals(Long.class) || clazz.equals(long.class)) {
                result = new Long(strValue);
            } else if (clazz.equals(Boolean.class) || clazz.equals(boolean.class)) {
                result = new Boolean(strValue);
            } else if (clazz.equals(Short.class) || clazz.equals(short.class)) {
                result = new Short(strValue);
            } else if (clazz.equals(Character.class) || clazz.equals(char.class)) {
                result = strValue.charAt(0);
            } else if (clazz.equals(String.class)) {
                result = strValue;
            } else {
                result = value;
            }
        }
        return result;
    }

    private static int arrayContentsMatch(Object[] a1, Object[] a2) {
        if (a1 == null) {
            if (a2 == null || a2.length == 0) {
                return 1;
            }
        }
        if (a2 == null) {
            if (a1.length == 0) {
                return 1;
            }
        }
        if (a1.length != a2.length) {
            return 0;
        }
        int match = 0;
        for (int i = 0; i < a1.length; i++) {
            if (a1[i] == a2[i]) {
                match++;
            }
        }
        return match;
    }

}
