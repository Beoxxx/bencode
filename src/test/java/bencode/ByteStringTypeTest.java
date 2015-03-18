package bencode;

import bencode.type.BencodeType;
import bencode.type.ByteStringType;
import junit.framework.TestCase;

import java.io.IOException;

public class ByteStringTypeTest extends TestCase {

    public void testEncode() throws IOException {

        assertEquals(new ByteStringType("this is test".getBytes()).encode(), "12:this is test");

        assertEquals(new ByteStringType("шахта".getBytes()).encode(), "10:шахта");

        assertEquals(new ByteStringType("78яхтаz".getBytes()).encode(), "11:78яхтаz");
    }

    public void testDecode() throws IOException {
        BencodeType bencodeType = BencodeType.decode("25:зацени тестик");

        assertTrue(bencodeType instanceof ByteStringType);
        assertEquals(bencodeType.toString(), "зацени тестик");

        bencodeType = BencodeType.decode("4:zero");

        assertTrue(bencodeType instanceof ByteStringType);
        assertEquals(bencodeType.toString(), "zero");


        // byte to byte compare
        String testString = "это mix";
        byte[] testStringBytes = testString.getBytes();

        bencodeType = BencodeType.decode(testStringBytes.length + ":" + testString);

        assertTrue(bencodeType instanceof ByteStringType);

        ByteStringType byteStringType = (ByteStringType) bencodeType;

        for(int i = 0; i < testStringBytes.length; i++)
            assertEquals(testStringBytes[i], byteStringType.getValue()[i]);
    }

    public void testTransit() throws IOException {
        assertEquals(BencodeType.decode(new ByteStringType("блаблаblah".getBytes()).encode()).toString(), "блаблаblah");

        assertEquals(BencodeType.decode("12:рукаface").encode(), "12:рукаface");
    }
}
