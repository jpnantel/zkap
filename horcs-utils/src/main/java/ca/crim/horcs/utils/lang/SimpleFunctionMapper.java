package ca.crim.horcs.utils.lang;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.el.FunctionMapper;

public class SimpleFunctionMapper implements FunctionMapper {

    private Map<String, Method> map = new HashMap<String, Method>();

    /**
     * Add a function to the mapper
     * 
     * @param prefix
     * @param localName
     * @param method
     */
    public void addFunction(String prefix, String localName, Method method) {
        map.put(prefix + ":" + localName, method);
    }

    /**
     * Add all methods from a class using reflection
     * 
     * @param prefix
     * @param clazz
     */
    public void addReflect(String prefix, Class<?> clazz) {
        for (Method m : clazz.getMethods()) {
            this.addFunction(prefix, m.getName(), m);
        }
    }

    public Method resolveFunction(String prefix, String localName) {
        return (Method) map.get(prefix + ":" + localName);
    }

}
