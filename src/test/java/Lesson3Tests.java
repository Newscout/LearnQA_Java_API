import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Lesson3Tests {
    @Test
    public void testEx10ShortPhrase() {
        String phrase = "Hello, World!";
        assertTrue(phrase.length() > 15, "This phrase is too short!!!");
    }
}
