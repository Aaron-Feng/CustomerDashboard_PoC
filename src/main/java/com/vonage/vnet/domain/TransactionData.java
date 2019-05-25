package com.vonage.vnet.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component("transactionData")
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TransactionData implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, Future<String>> transactionMap = new HashMap<String, Future<String>>();

    public Map<String, Future<String>> getTransactionMap() {
        return transactionMap;
    }

    public void setTransactionMap(Map<String, Future<String>> transactionMap) {
        this.transactionMap = transactionMap;
    }

}
