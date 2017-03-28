package pt.andred.cmu1617;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by andre on 05/11/16.
 */
public class Joiner {
    private final String separator;

    private Joiner(String separator) {
        this.separator = separator;
    }

    public static Joiner on(String separator) {
        return new Joiner(separator);
    }

    public static String getEncodedQueryParams(Map<String, String> queryParams) {
        List<String> params = new ArrayList<String>();
        for (String queryParam : queryParams.keySet()) {
            params.add(Joiner.on("=").join(queryParam, queryParams.get(queryParam)));
        }
        String result = Joiner.on("&").join(params);
        return result;
    }

    public String getSeparator() {
        return separator;
    }

    public String join(String... entries) {
        return join(Arrays.asList(entries));
    }

    public String join(Iterable<String> entries) {
        StringBuilder stringBuilder = new StringBuilder();
        String entry = null;
        Iterator<String> iterator = entries.iterator();
        while (iterator.hasNext()) {
            entry = iterator.next();
            stringBuilder.append(entry);
            if (iterator.hasNext()) {
                stringBuilder.append(separator);
            }
        }
        return stringBuilder.toString();
    }
}
