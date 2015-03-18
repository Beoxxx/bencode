package bencode.type;

import bencode.exception.InconsistentInputException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * <p>A list of values is encoded as l[contents]e . The contents consist of the bencoded elements of the list, in order, concatenated. A list consisting of the string "spam" and the number 42 would be encoded as: l4:spami42ee. Note the absence of separators between elements.</p>
 *
 * Backed by ArrayList in purpose to be respective to the original elements order
 */
public class ListType implements BencodeType {
    private final ArrayList<BencodeType> list;

    public ListType(ArrayList<BencodeType> list) {
        this.list = list;
    }

    public ListType(BencodeType... list) {
        this.list = new ArrayList<>(Arrays.asList(list));
    }

    public ArrayList<BencodeType> getList() {
        return list;
    }

    /**
     * @return String human-readable representation
     */
    @Override
    public String toString() {
        return "{" + list.stream().map(Object::toString).collect(Collectors.joining(", ")) + "}";
    }

    /**
     * Encodes current ListType into the given OutputStream
     * @param outputStream OutputStream
     * @throws IOException
     */
    @Override
    public void encode(OutputStream outputStream) throws IOException {
        outputStream.write("l".getBytes());
        list.stream().forEach(v -> {
            try {
                v.encode(outputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        outputStream.write("e".getBytes());

    }

    /**
     * Decodes current InputStream into ListType
     * All included types are decoding recursively
     * @param inputStream InputStream
     * @return ListType
     * @throws IOException
     */
    public static ListType decode(InputStream inputStream) throws IOException {
        return decode(inputStream, (char) inputStream.read());
    }

    public static ListType decode(InputStream inputStream, char firstChar) throws IOException {

        if (firstChar != START_LITERAL_LIST_TYPE)
            throw new InconsistentInputException("Wrong start literal: '" + firstChar + "'");

        ArrayList<BencodeType> list = new ArrayList<>();

        BencodeType b;

        while (true) {
            b = BencodeType.decodeInternal(inputStream);

            if (b == null)
                break;

            list.add(b);
        }

        return new ListType(list);
    }
}
