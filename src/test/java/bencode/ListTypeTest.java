package bencode;

import bencode.type.BencodeType;
import bencode.type.ByteStringType;
import bencode.type.IntType;
import bencode.type.ListType;
import junit.framework.TestCase;

import java.io.IOException;

public class ListTypeTest extends TestCase {

    public void testEncode() throws IOException {
        assertEquals(new ListType(new IntType(-2), new ByteStringType("oneдва".getBytes())).encode(), "li-2e9:oneдваe");

        assertEquals(new ListType(new IntType(-3), new ByteStringType("1:3".getBytes())).encode(), "li-3e3:1:3e");

        assertEquals(new ListType().encode(), "le");
    }

    public void testDecode() throws IOException {
        BencodeType bencodeType = BencodeType.decode("li33e5:ZZtopi-12ee");

        assertTrue(bencodeType instanceof ListType);

        ListType listType = (ListType) bencodeType;

        assertTrue(listType.getList().get(0) instanceof IntType);

        assertTrue(listType.getList().get(1) instanceof ByteStringType);

        assertTrue(listType.getList().get(2) instanceof IntType);

        assertEquals(((IntType)listType.getList().get(0)).getValue(), 33);

        assertEquals((listType.getList().get(1)).toString(), "ZZtop");

        assertEquals(((IntType)listType.getList().get(2)).getValue(), -12);

    }

    public void testTransit() throws IOException {
        assertEquals(BencodeType.decode("l6:Glitchi-1e4:хаe").encode(), "l6:Glitchi-1e4:хаe");

        BencodeType b = BencodeType.decode(new ListType(new IntType(1), new ByteStringType("tst".getBytes())).encode());

        assertTrue(b instanceof ListType);

        ListType listType = (ListType) b;

        assertTrue(listType.getList().get(0) instanceof IntType);

        assertEquals(((IntType) listType.getList().get(0)).getValue(), 1);

        assertTrue(listType.getList().get(1) instanceof ByteStringType);

        assertEquals(listType.getList().get(1).toString(), "tst");
    }
}
