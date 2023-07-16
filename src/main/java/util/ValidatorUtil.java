package util;

import model.InterfacePersist;
import util.messagesSystem.MensagemSistema;

import java.util.Collection;
import java.util.Map;

public class ValidatorUtil {

    public static final boolean isEmpty(Object o) {
        if (o == null)
            return true;
        if (o instanceof String)
            return isEmpty( (String) o);
        if (o instanceof Number i) {
            return (i.doubleValue() == 0);
        }
        if (o instanceof InterfacePersist)
            return ((InterfacePersist) o).getId() == 0;
        if (o instanceof Object[])
            return ((Object[]) o).length == 0;
        if (o instanceof int[])
            return ((int[]) o).length == 0;
        if (o instanceof Collection<?>)
            return ((Collection<?>) o).size() == 0;
        if (o instanceof Map<?, ?>)
            return ((Map<?, ?>) o).size() == 0;
        if (o instanceof MensagemSistema)
            return ((MensagemSistema) o).isEmpty();
        return false;
    }

    public static final boolean isEmpty(String s) {
        return (s == null || s.trim().length() == 0);
    }

    public static final boolean isNotEmpty(Object o) { return !isEmpty(o); }

}
