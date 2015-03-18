package bencode.type;

import bencode.exception.InconsistentInputException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

/**
 * <p>A dictionary is encoded as d[contents]e. The elements of the dictionary are encoded each key immediately followed by its value. All keys must be byte strings and must appear in lexicographical order. A dictionary that associates the values 42 and "spam" with the keys "foo" and "bar", respectively (in other words, {"bar": "spam", "foo": 42}), would be encoded as follows: d3:bar4:spam3:fooi42ee. (This might be easier to read by inserting some spaces: d 3:bar 4:spam 3:foo i42e e.)</p>
 * <p>
 * A dictionary type is backed by LinkedHashMap in purpose to be respective to the original elements order
 */
public class DictionaryType implements BencodeType {
    private final LinkedHashMap<BencodeType, BencodeType> map;

    public DictionaryType(LinkedHashMap<BencodeType, BencodeType> map) {
        this.map = map;
    }

    public DictionaryType() {
        this.map = new LinkedHashMap<>();
    }

    public void put(BencodeType key, BencodeType value) {
        map.put(key, value);
    }

    public LinkedHashMap<BencodeType, BencodeType> getMap() {
        return map;
    }

    /**
     * @return String human-readable representation
     */
    @Override
    public String toString() {
        return "{" + map.entrySet().stream().map(v -> "[" + v.getKey() + " : " + v.getValue() + "]").collect(Collectors.joining(", ")) + "}";
    }

    /**
     * Encoding current DictionaryType into OutputStream
     *
     * @param outputStream OutputStream
     * @throws IOException
     */
    @Override
    public void encode(OutputStream outputStream) throws IOException {
        outputStream.write("d".getBytes());

        map.entrySet().stream().forEach(v -> {
            try {
                v.getKey().encode(outputStream);
                v.getValue().encode(outputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        outputStream.write("e".getBytes());

    }

    /**
     * Decoding current InputStream into DictionaryType. All included types are decoding recursively.
     *
     * @param inputStream InputStream
     * @return DictionaryType
     * @throws IOException
     */
    public static DictionaryType decode(InputStream inputStream) throws IOException {
        return decode(inputStream, (char) inputStream.read());
    }

    public static DictionaryType decode(InputStream inputStream, char firstChar) throws IOException {

        if (firstChar != START_LITERAL_DICTIONARY_TYPE)
            throw new InconsistentInputException("Wrong start literal: '" + firstChar + "'");

        LinkedHashMap<BencodeType, BencodeType> map = new LinkedHashMap<>();

        while (true) {
            BencodeType key = BencodeType.decodeInternal(inputStream);

            if (key == null)
                break;

            BencodeType value = BencodeType.decodeInternal(inputStream);

            if (value == null)
                throw new InconsistentInputException("Dictionary entry value is null.");


            map.put(key, value);
        }

        return new DictionaryType(map);
    }
}
