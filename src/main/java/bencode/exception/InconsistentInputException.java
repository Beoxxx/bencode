package bencode.exception;

import java.io.IOException;

public class InconsistentInputException extends IOException {
    public InconsistentInputException(String message) {
        super(message);
    }
}
