package org.code.protocol.serialization;

/**
 * The type Serialization exception.
 */
public class SerializationException extends RuntimeException {

    private static final long serialVersionUID = -1;

    /**
     * Instantiates a new Serialization exception.
     */
    public SerializationException() {
        super();
    }

    /**
     * Instantiates a new Serialization exception.
     * @param msg the msg
     */
    public SerializationException(String msg) {
        super(msg);
    }

    /**
     * Instantiates a new Serialization exception.
     * @param msg   the msg
     * @param cause the cause
     */
    public SerializationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * Instantiates a new Serialization exception.
     * @param cause the cause
     */
    public SerializationException(Throwable cause) {
        super(cause);
    }
}
