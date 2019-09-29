package tests;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static tests.CDPClient.getDynamicID;

public class MessageBuilder {
    protected String method;
    int id;
    protected Map<String, Object> params;

    MessageBuilder(String method) {
        this.id = getDynamicID();
        this.method = method;
    }

    public void addParam(String key, Object value) {
        if (Objects.isNull(params)) {
            params = new HashMap<>();
        }
        params.put(key, value);
    }

}


