package info.kgeorgiy.java.advanced.implementor;

/**
 * Thrown by {@link info.kgeorgiy.java.advanced.implementor.Impler} when an error occurred.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 * @version $$Id$$
 */
public class ImplerException extends Exception {
    public ImplerException() {
    }

    public ImplerException(String message) {
        super(message);
    }

    public ImplerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImplerException(Throwable cause) {
        super(cause);
    }
}
