package com.gruppo2.musicplaylistmanager.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per l'enum {@link Tag} (US-15): presenza dei tre tag visivi e
 * relative etichette leggibili.
 */
class TagTest {

    @Test
    void hasThreeVisualTags() {
        assertEquals(3, Tag.values().length);
    }

    @Test
    void everyTagHasNonEmptyLabel() {
        for (Tag tag : Tag.values()) {
            assertNotNull(tag.getLabel());
            assertFalse(tag.getLabel().isBlank(), "label vuota per " + tag);
        }
    }

    @Test
    void labelsAreDistinct() {
        assertEquals(Tag.values().length,
                java.util.Arrays.stream(Tag.values()).map(Tag::getLabel).distinct().count());
    }
}
