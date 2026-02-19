package bplustree;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemoryAddressGeneratorTest {

    private MemoryAddressGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new MemoryAddressGenerator();
    }

    @Test
    void testGenerateWithSpecificValues() {
        MemoryAddress address = generator.generate(0x00A3, 0x0F2B);

        assertEquals(0x00A3, address.sector());
        assertEquals(0x0F2B, address.offset());
        assertEquals("0x00A3:0x0F2B", address.toString());
    }

    @Test
    void testGenerateRandomProducesValidAddresses() {
        MemoryAddress address = generator.generateRandom();

        assertNotNull(address);
        assertTrue(address.sector() >= 0 && address.sector() < 0xFFFF);
        assertTrue(address.offset() >= 0 && address.offset() < 0xFFFF);
    }

    @Test
    void testGenerateRandomWithBounds() {
        int maxSector = 100;
        int maxOffset = 200;

        MemoryAddress address = generator.generateRandom(maxSector, maxOffset);

        assertNotNull(address);
        assertTrue(address.sector() >= 0 && address.sector() <= maxSector);
        assertTrue(address.offset() >= 0 && address.offset() <= maxOffset);
    }

    @Test
    void testGenerateRandomWithSeedProducesSameSequence() {
        long seed = 12345L;
        MemoryAddressGenerator gen1 = new MemoryAddressGenerator(seed);
        MemoryAddressGenerator gen2 = new MemoryAddressGenerator(seed);

        MemoryAddress addr1 = gen1.generateRandom();
        MemoryAddress addr2 = gen2.generateRandom();

        assertEquals(addr1, addr2);
    }

    @Test
    void testGenerateSequential() {
        generator.resetSequential();

        MemoryAddress addr1 = generator.generateSequential();
        MemoryAddress addr2 = generator.generateSequential();
        MemoryAddress addr3 = generator.generateSequential();

        assertEquals(new MemoryAddress(0, 0), addr1);
        assertEquals(new MemoryAddress(0, 1), addr2);
        assertEquals(new MemoryAddress(0, 2), addr3);
    }

    @Test
    void testSequentialIncrementsSectorWhenOffsetOverflows() {
        generator.resetSequential();

        // Générer suffisamment d'adresses pour dépasser 0xFFFF
        MemoryAddress lastAddr = null;
        for (int i = 0; i <= 0x10000; i++) {
            lastAddr = generator.generateSequential();
        }

        // Après avoir dépassé 0xFFFF offsets, on doit être sur le secteur 1
        assertNotNull(lastAddr);
        assertEquals(1, lastAddr.sector());
    }

    @Test
    void testResetSequential() {
        generator.generateSequential();
        generator.generateSequential();
        generator.generateSequential();

        generator.resetSequential();

        MemoryAddress addr = generator.generateSequential();
        assertEquals(new MemoryAddress(0, 0), addr);
    }

    @Test
    void testFromValue() {
        // Valeur: 0x00A30F2B
        // Secteur: 0x00A3 (bits 16-31)
        // Offset: 0x0F2B (bits 0-15)
        int value = 0x00A30F2B;

        MemoryAddress address = generator.fromValue(value);

        assertEquals(0x00A3, address.sector());
        assertEquals(0x0F2B, address.offset());
    }

    @Test
    void testFromValueSimple() {
        // Valeur simple: secteur 1, offset 5
        int value = (1 << 16) | 5;

        MemoryAddress address = generator.fromValue(value);

        assertEquals(1, address.sector());
        assertEquals(5, address.offset());
    }

    @Test
    void testMemoryAddressComparison() {
        MemoryAddress addr1 = generator.generate(10, 100);
        MemoryAddress addr2 = generator.generate(10, 200);
        MemoryAddress addr3 = generator.generate(20, 50);

        assertTrue(addr1.compareTo(addr2) < 0, "addr1 devrait être inférieur à addr2 (même secteur, offset plus petit)");
        assertTrue(addr2.compareTo(addr1) > 0, "addr2 devrait être supérieur à addr1");
        assertTrue(addr1.compareTo(addr3) < 0, "addr1 devrait être inférieur à addr3 (secteur plus petit)");
        assertEquals(0, addr1.compareTo(addr1), "Une adresse devrait être égale à elle-même");
    }

    @Test
    void testMemoryAddressParse() {
        String addressString = "0x00A3:0x0F2B";

        MemoryAddress parsed = MemoryAddress.parse(addressString);

        assertEquals(0x00A3, parsed.sector());
        assertEquals(0x0F2B, parsed.offset());
    }

    @Test
    void testMemoryAddressParseInvalidFormat() {
        assertThrows(IllegalArgumentException.class, () -> {
            MemoryAddress.parse("invalid");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            MemoryAddress.parse("0x00A3");
        });
    }

    @Test
    void testMemoryAddressInvalidNegativeValues() {
        assertThrows(IllegalArgumentException.class, () -> {
            new MemoryAddress(-1, 0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new MemoryAddress(0, -1);
        });
    }

    @Test
    void testMemoryAddressToString() {
        MemoryAddress addr = new MemoryAddress(163, 3883); // 0x00A3, 0x0F2B en décimal

        assertEquals("0x00A3:0x0F2B", addr.toString());
    }

    @Test
    void testMultipleRandomAddressesAreDifferent() {
        MemoryAddress addr1 = generator.generateRandom();
        MemoryAddress addr2 = generator.generateRandom();
        MemoryAddress addr3 = generator.generateRandom();

        // Il est très peu probable que trois adresses aléatoires soient identiques
        assertFalse(addr1.equals(addr2) && addr2.equals(addr3),
                "Les adresses aléatoires ne devraient probablement pas toutes être identiques");
    }
}
