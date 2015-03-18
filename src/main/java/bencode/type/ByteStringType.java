package bencode.type;


import bencode.exception.InconsistentInputException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * <p>A byte string (a sequence of bytes, not necessarily characters) is encoded as [length]:[contents]. The length is encoded in base 10, like integers, but must be non-negative (zero is allowed); the contents are just the bytes that make up the string. The string "spam" would be encoded as 4:spam. The specification does not deal with encoding of characters outside the ASCII set; to mitigate this, some BitTorrent applications explicitly communicate the encoding (most commonly UTF-8) in various non-standard ways. This is identical to how netstrings work, except that netstrings additionally append a comma suffix after the byte sequence.</p>
 */
public class ByteStringType implements BencodeType {
    private final byte[] value;

    public ByteStringType(byte[] value) {
        this.value = value;
    }

    public byte[] getValue() {
        return value;
    }

    /**
     * @return String human-readable representation
     */
    @Override
    public String toString() {
        return new String(value);
    }

    /**
     * Encodes current ByteStringType to the given OutputStream
     *
     * @param outputStream OutputStream
     * @throws IOException
     */
    @Override
    public void encode(OutputStream outputStream) throws IOException {
        outputStream.write(String.valueOf(value.length).getBytes());//writeBytes(String.valueOf(value.length));
        outputStream.write(":".getBytes());//writeBytes(":");
        outputStream.write(value);
    }


    /**
     * Decodes currrent InputStream to ByteStringType
     * Processing in two steps
     * 1. Read number of bytes following by
     * 2. Read bytes themselves
     *
     * @param inputStream InputStream
     * @return ByteStringType
     * @throws IOException
     * @throws InconsistentInputException
     * @throws java.lang.NumberFormatException
     */
    public static ByteStringType decode(InputStream inputStream) throws IOException {
        return decode(inputStream, (char) inputStream.read());
    }

    public static ByteStringType decode(InputStream inputStream, char firstChar) throws IOException {
        char c;
        int byteCnt;
        StringBuilder sb = new StringBuilder();

        if (!Character.isDigit(firstChar))
            throw new InconsistentInputException("Wrong digit literal in byte string size: '" + firstChar + "'");

        sb.append(firstChar);

        // read number of following bytes
        while (true) {
            c = (char) inputStream.read();

            if (c == DELIMITER_LITERAL_BYTE_STRING_TYPE) {
                byteCnt = Integer.parseInt(sb.toString());
                break;
            }

            if (!Character.isDigit(c))
                throw new InconsistentInputException("Wrong digit literal in byte string size: '" + c + "'");

            sb.append(c);
        }

        // read bytes
        byte[] bytes = new byte[byteCnt];

        for (int i = 0; i < byteCnt; i++)
            bytes[i] = (byte) inputStream.read();


        return new ByteStringType(bytes);
    }
}
