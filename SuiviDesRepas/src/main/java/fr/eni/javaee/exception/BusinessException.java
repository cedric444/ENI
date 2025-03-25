package fr.eni.javaee.exception;

import java.util.ArrayList;
import java.util.List;

public class BusinessException extends Exception {

    private List<Integer> listeCodes;

    public BusinessException() {
        super();
        this.listeCodes = new ArrayList<>();
    }

    public void addError(int code) {
        if(!listeCodes.contains(code)) {
            listeCodes.add(code);
        }
    }

    public boolean hasError() {
        return !this.listeCodes.isEmpty();
    }

    public List<Integer> getListeCodes() {
        return this.listeCodes;
    }
}

