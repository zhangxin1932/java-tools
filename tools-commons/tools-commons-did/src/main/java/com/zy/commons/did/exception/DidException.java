package com.zy.commons.did.exception;

public class DidException extends RuntimeException {
    private static final long serialVersionUID = -184892146399473848L;

    public DidException() {
    }

    public DidException(String message) {
        super(message);
    }

    public DidException(String message, Throwable cause) {
        super(message, cause);
    }

    public DidException(Throwable cause) {
        super(cause);
    }

}
