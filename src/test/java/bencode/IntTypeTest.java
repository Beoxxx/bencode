package bencode;

import bencode.type.BencodeType;
import bencode.type.IntType;
import junit.framework.TestCase;

import java.io.IOException;

public class IntTypeTest extends TestCase {

    public void testEncode() throws IOException {
        assertEquals(new IntType(77).encode(), "i77e");

        assertEquals(new IntType(-123).encode(), "i-123e");

        assertEquals(new IntType(0).encode(), "i0e");
    }

    public void testDecode() throws IOException {
        BencodeType b = BencodeType.decode("i333e");

        assertTrue(b instanceof IntType);
        assertEquals(((IntType) b).getValue(), 333);

        b = BencodeType.decode("i-333e");

        assertTrue(b instanceof IntType);
        assertEquals(((IntType) b).getValue(), -333);
    }

    public void testTransit() throws IOException {
        assertEquals(BencodeType.decode("i1335e").encode(), "i1335e");

        assertEquals(BencodeType.decode("i-26e").encode(), "i-26e");

        assertEquals(((IntType) BencodeType.decode(new IntType(12).encode())).getValue(), 12);

        assertEquals(((IntType) BencodeType.decode(new IntType(-44).encode())).getValue(), -44);
    }
}
