package ca.crim.horcs.utils.collection;

import java.util.Map;

import javax.servlet.jsp.el.ELException;
import javax.servlet.jsp.el.VariableResolver;

public class MapVariableResolver implements VariableResolver {

    private Map<?, ?> map;

    public MapVariableResolver(Map<?, ?> map) {
        this.map = map;
    }

    public Object resolveVariable(String var) throws ELException {
        if (!map.containsKey(var)) {
            System.out.println("WARNING : Variable not resolved : " + var);
        }
        return map.get(var);
    }
}
