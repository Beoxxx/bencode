package bencode;

import bencode.type.*;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.Map;

public class DictionaryTypeTest extends TestCase {

    public void testEncode() throws IOException {
        DictionaryType dictionaryType = new DictionaryType();
        dictionaryType.put(new IntType(1), new ListType(new ByteStringType("test".getBytes()), new IntType(2)));
        dictionaryType.put(new IntType(3), new IntType(4));

        assertEquals(dictionaryType.encode(), "di1el4:testi2eei3ei4ee");
    }

    public void testDecode() throws IOException {
        BencodeType bencodeType = BencodeType.decode("ddi1ei2eei3ee");

        assertTrue(bencodeType instanceof DictionaryType);

        DictionaryType dictionaryType = (DictionaryType) bencodeType;

        for(Map.Entry<BencodeType, BencodeType> entry : dictionaryType.getMap().entrySet()) {
            assertTrue(entry.getKey() instanceof DictionaryType);

            for(Map.Entry<BencodeType, BencodeType> subEntry : ((DictionaryType)entry.getKey()).getMap().entrySet()) {
                assertTrue(subEntry.getKey() instanceof IntType);
                assertEquals(((IntType)subEntry.getKey()).getValue(), 1);

                assertTrue(subEntry.getValue() instanceof IntType);
                assertEquals(((IntType)subEntry.getValue()).getValue(), 2);
            }

            assertTrue(entry.getValue() instanceof IntType);
            assertEquals(((IntType)entry.getValue()).getValue(), 3);
        }
    }

    public void testTransit() throws IOException {
        assertEquals(BencodeType.decode("dl3:hubi-3ee4:testi-1ei2ee").encode(), "dl3:hubi-3ee4:testi-1ei2ee");
    }
}
