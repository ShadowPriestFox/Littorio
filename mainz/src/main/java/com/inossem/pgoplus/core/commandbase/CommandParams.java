package com.inossem.pgoplus.core.commandbase;

import io.vavr.control.Try;

import java.util.HashMap;
import java.util.Map;

public class CommandParams {
    private Map<String, String> mapParams = new HashMap<>();

    public Try<String> getValue(String key) {
        if (mapParams.containsKey(key)) {
            return Try.success(mapParams.get(key));
        } else {
            return Try.failure(new Exception(key + "not found!"));
        }
    }

    public static CommandParams create(Map<String, String> params) {
        var p = new CommandParams();
        if (params != null) {
            p.mapParams.putAll(params);
        }
        return p;
    }

}
