package bencode.type;


import bencode.exception.InconsistentInputException;

import java.io.*;


/**
 * <p>Main interface of beoncode types</p>
 * <p>
 * <p>Bencode uses ASCII characters as delimiters and digits.</p>
 * <p>An integer is encoded as i[integer encoded in base ten ASCII]e. Leading zeros are not allowed (although the number zero is still represented as "0"). Negative values are encoded by prefixing the number with a minus sign. The number 42 would thus be encoded as i42e, 0 as i0e, and -42 as i-42e. Negative zero is not permitted.</p>
 * <p>A byte string (a sequence of bytes, not necessarily characters) is encoded as [length]:[contents]. The length is encoded in base 10, like integers, but must be non-negative (zero is allowed); the contents are just the bytes that make up the string. The string "spam" would be encoded as 4:spam. The specification does not deal with encoding of characters outside the ASCII set; to mitigate this, some BitTorrent applications explicitly communicate the encoding (most commonly UTF-8) in various non-standard ways. This is identical to how netstrings work, except that netstrings additionally append a comma suffix after the byte sequence.</p>
 * <p>A list of values is encoded as l[contents]e . The contents consist of the bencoded elements of the list, in order, concatenated. A list consisting of the string "spam" and the number 42 would be encoded as: l4:spami42ee. Note the absence of separators between elements.</p>
 * <p>A dictionary is encoded as d[contents]e. The elements of the dictionary are encoded each key immediately followed by its value. All keys must be byte strings and must appear in lexicographical order. A dictionary that associates the values 42 and "spam" with the keys "foo" and "bar", respectively (in other words, {"bar": "spam", "foo": 42}), would be encoded as follows: d3:bar4:spam3:fooi42ee. (This might be easier to read by inserting some spaces: d 3:bar 4:spam 3:foo i42e e.)</p>
 * <p>There are no restrictions on what kind of values may be stored in lists and dictionaries; they may (and usually do) contain other lists and dictionaries. This allows for arbitrarily complex data structures to be encoded.</p>
 */
public interface BencodeType {

    public static final char START_LITERAL_INT_TYPE = 'i';
    public static final char START_LITERAL_DICTIONARY_TYPE = 'd';
    public static final char START_LITERAL_LIST_TYPE = 'l';
    public static final char END_LITERAL = 'e';
    public static final char DELIMITER_LITERAL_BYTE_STRING_TYPE = ':';

    public static void main(String[] args) throws IOException {
        FileOutputStream os = new FileOutputStream("D:/text.txt");
        BencodeType b = new ByteStringType("Это тест".getBytes());
        b.encode(os);

        FileInputStream fs = new FileInputStream("D:/text.txt");
        System.out.println(BencodeType.decode(new DataInputStream(fs)));
        System.out.println(BencodeType.decode(System.in));
    }

    /**
     * Encodes current element to string representation
     *
     * @return String with encoded element
     * @throws IOException
     */
    public default String encode() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        encode(os);
        return new String(os.toByteArray());
    }

    /**
     * Encodes current element to the given OutputStream
     * Complex types (DictionaryType, ListType) are encoding recursively
     *
     * @throws IOException
     */
    public void encode(OutputStream outputStream) throws IOException;

    /**
     * Decodes input String in to one of a bencode types
     *
     * @param input String with bencoded data
     * @return BencodeType instance with decoded data
     * @throws IOException
     * @throws bencode.exception.InconsistentInputException
     * @throws java.lang.NumberFormatException
     */
    public static BencodeType decode(String input) throws IOException {
        return decode(new ByteArrayInputStream(input.getBytes()));
    }

    /**
     * Decodes InputStream in to one of a bencode types
     * The main goal is to check first literal of the sequence and then select needed decode algorithm, complex types (DictionaryType, ListType) are decoding recursively
     *
     * @param inputStream InputStream with bencoded data
     * @return BencodeType instance with decoded data
     * @throws IOException
     * @throws bencode.exception.InconsistentInputException
     * @throws java.lang.NumberFormatException
     */
    static BencodeType decodeInternal(InputStream inputStream) throws IOException {

        char c = (char) inputStream.read();

        if (c == START_LITERAL_INT_TYPE)
            return IntType.decode(inputStream, c);
        else if (Character.isDigit(c))
            return ByteStringType.decode(inputStream, c);
        else if (c == START_LITERAL_LIST_TYPE)
            return ListType.decode(inputStream, c);
        else if (c == START_LITERAL_DICTIONARY_TYPE)
            return DictionaryType.decode(inputStream, c);
        else if (c == END_LITERAL)
            return null;
        else
            throw new InconsistentInputException("Wrong start literal: '" + c + "'");

    }

    /**
     * This is wrapper over decodeInternal method to handle start 'e' literal exception
     *
     * @param inputStream InputStream with bencoded data
     * @return BencodeType instance with decoded data
     * @throws IOException
     * @throws bencode.exception.InconsistentInputException
     * @throws java.lang.NumberFormatException
     */
    public static BencodeType decode(InputStream inputStream) throws IOException {
        BencodeType bencodeType = decodeInternal(inputStream);
        if (bencodeType == null)
            throw new InconsistentInputException("Wrong start literal: 'e'");
        return bencodeType;
    }
}


