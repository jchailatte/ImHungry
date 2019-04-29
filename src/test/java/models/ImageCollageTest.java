package models;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import common.Constants;

import static org.junit.jupiter.api.Assertions.*;

class ImageCollageTest {
    @Test
    void testNumImages() {
        assertEquals("10", ImageCollage.numImages);
        assertEquals("10", ImageCollage.numImages);
    }

    @Test
    void testSearchType() {
        assertEquals("image", ImageCollage.searchType);
        assertEquals("image", ImageCollage.searchType);
    }

    @Test
    void testSearch() throws IOException {
        String query1 = "Apple Cinnamon Oatmeal";
        String query2 = "Banana Split Ice Cream";
        ArrayList<String> images1 = ImageCollage.search(query1);
        ArrayList<String> images2 = ImageCollage.search(query2);

        assertNotNull(images1);
        if (images1.size() == 10) {
            assertNotNull(images1.get(0));
            assertNotNull(images1.get(1));
            assertNotNull(images1.get(2));
            assertNotNull(images1.get(3));
            assertNotNull(images1.get(4));
            assertNotNull(images1.get(5));
            assertNotNull(images1.get(6));
            assertNotNull(images1.get(7));
            assertNotNull(images1.get(8));
            assertNotNull(images1.get(9));
        }

        assertNotNull(images2);
        if (images2.size() == 10) {
            assertNotNull(images2.get(0));
            assertNotNull(images2.get(1));
            assertNotNull(images2.get(2));
            assertNotNull(images2.get(3));
            assertNotNull(images2.get(4));
            assertNotNull(images2.get(5));
            assertNotNull(images2.get(6));
            assertNotNull(images2.get(7));
            assertNotNull(images2.get(8));
            assertNotNull(images2.get(9));
        }
    }
}
