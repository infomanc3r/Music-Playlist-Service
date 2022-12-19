package com.amazon.ata.music.playlist.service.exceptions;

public class AttributeRelatedException extends RuntimeException {
    /**
     * Exception with no message or cause.
     */
    public AttributeRelatedException() {
        super();
    }

    /**
     * Exception with a message, but no cause.
     * @param message A descriptive message for this exception.
     */
    public AttributeRelatedException(String message) {
        super(message);
    }

    /**
     * Exception with no message, but with a cause.
     * @param cause The original throwable resulting in this exception.
     */
    public AttributeRelatedException(Throwable cause) {
        super(cause);
    }

    /**
     * Exception with message and cause.
     * @param message A descriptive message for this exception.
     * @param cause The original throwable resulting in this exception.
     */
    public AttributeRelatedException(String message, Throwable cause) {
        super(message, cause);
    }

}

