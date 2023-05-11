import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class Lesson3Tests {
    @Test
    public void testEx10ShortPhrase() {
        String phrase = "Hello, World!";
        assertTrue(phrase.length() > 15, "This phrase is too short!!!");
    }
    @Test
    public void testEx11Cookies() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();
        Map<String, String> cookies = response.getCookies();
        assertFalse(cookies.isEmpty(), "There is no cookies.");
        assertTrue(cookies.containsKey("HomeWork"),"There is no cookie with this key.");
        assertTrue(cookies.containsValue("hw_value"),"There is no cookie with this value.");
    }
}
