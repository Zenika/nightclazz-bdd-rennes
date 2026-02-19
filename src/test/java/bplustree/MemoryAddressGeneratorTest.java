package bplustree;

import bplustree.memory.MemoryAddress;
import bplustree.memory.MemoryAddressGenerator;
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
    void address_displays_in_hex_format() {
        MemoryAddress addr = new MemoryAddress(0x00A3, 0x0F2B);

        assertEquals("0x00A3:0x0F2B", addr.toString());
    }

}
