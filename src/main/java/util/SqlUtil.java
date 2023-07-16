package util;

import model.InterfacePersist;

import java.time.LocalDate;
import java.util.Collection;

public class SqlUtil {

    public static String gerarStringInSQL(Collection<?> values) {
        StringBuilder sb = new StringBuilder();

        sb.append("(");
        boolean firstValue = true;
        for (Object value : values) {
            if (!firstValue) {
                sb.append(",");
            }
            sb.append(formatValueForInClause(value));
            firstValue = false;
        }
        sb.append(")");

        return sb.toString();
    }

    private static String formatValueForInClause(Object value) {
        if (value instanceof String || value instanceof Character || value instanceof LocalDate) {
            return "'" + escapeSingleQuote(value.toString()) + "'";
        } else if (value instanceof InterfacePersist) {
            return formatValueForInClause(((InterfacePersist) value).getId());
        }

        return value.toString();
    }

    // Trata casos em que possa haver String ou afins com aspas simples
    private static String escapeSingleQuote(String value) {
        return value.replace("'", "''");
    }

}
