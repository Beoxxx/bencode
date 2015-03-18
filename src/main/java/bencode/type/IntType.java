package bencode.type;


import bencode.exception.InconsistentInputException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * <p>An integer is encoded as i[integer encoded in base ten ASCII]e. Leading zeros are not allowed (although the number zero is still represented as "0"). Negative values are encoded by prefixing the number with a minus sign. The number 42 would thus be encoded as i42e, 0 as i0e, and -42 as i-42e. Negative zero is not permitted.</p>
 */
public class IntType implements BencodeType {
    private final int value;

    public IntType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * @return String human-readable representation
     */
    @Override
    public String toString() {
        return String.valueOf(value);
    }

    /**
     * Encodes current IntType to the given OutputStream
     *
     * @param outputStream OutputStream
     * @throws IOException
     */
    @Override
    public void encode(OutputStream outputStream) throws IOException {
        outputStream.write("i".getBytes());
        outputStream.write(String.valueOf(value).getBytes());
        outputStream.write("e".getBytes());
    }

    /**
     * Decodes current InputStream into IntType
     *
     * @param inputStream InputStream
     * @return IntType
     * @throws IOException
     */
    public static IntType decode(InputStream inputStream) throws IOException {
        return decode(inputStream, (char) inputStream.read());
    }

    public static IntType decode(InputStream inputStream, char firstChar) throws IOException {

        if (firstChar != START_LITERAL_INT_TYPE)
            throw new InconsistentInputException("Wrong start literal: '" + firstChar + "'");

        char c;
        StringBuilder sb = new StringBuilder();

        while (true) {
            c = (char) inputStream.read();

            if (c == END_LITERAL)
                return new IntType(Integer.parseInt(sb.toString()));

            if (!Character.isDigit(c) && c != '-')
                throw new InconsistentInputException("Wrong digit literal in integer type: '" + c + "'");

            sb.append(c);

        }
    }

}
