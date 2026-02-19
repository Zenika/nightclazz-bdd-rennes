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
    void generated_address_has_non_negative_sector_and_offset() {
        MemoryAddress addr = generator.generateRandom();

        assertTrue(addr.sector() >= 0);
        assertTrue(addr.offset() >= 0);
    }

    @Test
    void two_generators_with_same_seed_produce_same_address() {
        MemoryAddressGenerator gen1 = new MemoryAddressGenerator(42L);
        MemoryAddressGenerator gen2 = new MemoryAddressGenerator(42L);

        assertEquals(gen1.generateRandom(), gen2.generateRandom());
    }

    @Test
    void address_displays_in_hex_format() {
        MemoryAddress addr = new MemoryAddress(0x00A3, 0x0F2B);

        assertEquals("0x00A3:0x0F2B", addr.toString());
    }

    @Test
    void address_with_negative_sector_is_rejected() {
        assertThrows(IllegalArgumentException.class, () -> new MemoryAddress(-1, 0));
    }

    @Test
    void address_with_negative_offset_is_rejected() {
        assertThrows(IllegalArgumentException.class, () -> new MemoryAddress(0, -1));
    }
}
