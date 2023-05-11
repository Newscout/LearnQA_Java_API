import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class HelloWorldTest {

    @Test
    public void testHelloWorld() {
        Response response =RestAssured
                .get("https://playground.learnqa.ru/api/get_text")
                .andReturn();
        response.prettyPrint();
    }
    @Test
    public void testEx5ParsingJson() {
        JsonPath response = RestAssured
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();
        String answer = response.get("messages[1].message");
        System.out.println(answer);
    }
    @Test
    public void testEx6Redirect() {
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();
        String locationHeader = response.getHeader("location");
        System.out.println(locationHeader);
    }
    @Test
    public void testEx7LongRedirect() {

        int statusCode = 100;
        int countRedirects = 0;
        String url = "https://playground.learnqa.ru/api/long_redirect";
        while(statusCode != 200) {
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(url)
                    .andReturn();
            statusCode = response.getStatusCode();
            if(statusCode != 200) {
                url = response.getHeader("location");
                countRedirects++;
                System.out.println(statusCode + "  ---  " + url);
            }
        }
        System.out.println("The number of redirects is " + countRedirects + ".");
    }
    @Test
    public  void testEx8Tokens() throws InterruptedException {
        String url = "https://playground.learnqa.ru/ajax/api/longtime_job";
        System.out.println("Getting 'token' and 'seconds' for create a job...");
        JsonPath responseForGetTokenAndSeconds  = RestAssured
                .get(url)
                .jsonPath();
        String tokenForJob = responseForGetTokenAndSeconds.get("token");
        int secondsForWait = responseForGetTokenAndSeconds.get("seconds");
        int millisecondsForWait = secondsForWait * 1000;
        responseForGetTokenAndSeconds.prettyPrint();
        System.out.println();

        System.out.println("Checking errors with not correct token...");
        JsonPath responseWithNotCorrectToken  = RestAssured
                .given()
                .queryParam("token", "notCorrectToken")
                .get(url)
                .jsonPath();
        String errorWithNotCorrectToken = responseWithNotCorrectToken.get("error");
        responseWithNotCorrectToken.prettyPrint();
        if(errorWithNotCorrectToken.equals("No job linked to this token")) {
            System.out.println("Error message is correct. It is: " + errorWithNotCorrectToken);
        }else {
            System.out.println("Error message is NOT correct. It is: " + errorWithNotCorrectToken);
        }
        System.out.println();

        System.out.println("Checking job status without waiting ...");
        JsonPath responseWithoutWaiting  = RestAssured
                .given()
                .queryParam("token", tokenForJob)
                .get(url)
                .jsonPath();
        String statusWithoutWait = responseWithoutWaiting.get("status");
        responseWithoutWaiting.prettyPrint();
        if (statusWithoutWait.equals("Job is NOT ready")) {
            System.out.println("Jobs status without waiting is: '" + statusWithoutWait + "'. It is correct!!!");
        }else {
            System.out.println("Jobs status without waiting is: '" + statusWithoutWait + "'. It is NOT correct!!!");
        }
        System.out.println();

        System.out.println("Waiting " + secondsForWait + " seconds to create a job ...");
        Thread.sleep(millisecondsForWait);

        System.out.println("Checking job status after waiting ...");
        JsonPath responseWithWaiting  = RestAssured
                .given()
                .queryParam("token", tokenForJob)
                .get(url)
                .jsonPath();
        responseWithWaiting.prettyPrint();
        String statusAfterWait = responseWithWaiting.get("status");
        String resultAfterWait = responseWithWaiting.get("result");
        if (statusAfterWait.equals("Job is ready")) {
            System.out.println("Jobs status after waiting is: '" + statusAfterWait + "'. It is correct!!!");
        }else {
            System.out.println("Jobs status after waiting is: '" + statusAfterWait + "'. It is NOT correct!!!");
        }
        if (resultAfterWait != null) {
            System.out.println("Result is not empty. It is: " + resultAfterWait);
        }else {
            System.out.println("There is no result");
        }
    }
    @Test
    public void testEx9Password() {
        String [] passwords = {"password", "123456", "123456789", "12345678", "12345", "qwerty", "abc123",
                "football", "1234567", "monkey", "111111", "letmein", "1234", "1234567890", "dragon", "baseball",
                "sunshine", "iloveyou", "trustno1", "princess", "adobe123", "123123", "welcome",
                "login", "admin", "qwerty123", "solo", "1q2w3e4r", "master", "666666", "photoshop", "1qaz2wsx",
                "qwertyuiop", "ashley", "mustang", "121212", "starwars", "654321", "bailey", "access", "flower",
                "555555", "passw0rd", "shadow", "lovely", "7777777", "michael", "!@#$%^&*", "jesus", "password1",
                "superman", "hello", "charlie", "888888", "696969", "hottie", "freedom", "aa123456", "qazwsx",
                "ninja", "azerty", "loveme", "whatever", "donald", "batman", "zaq1zaq1", "Football", "000000", "123qwe"};
        String password = "";

        for(int i = 0; i<= passwords.length; i++){
            Map<String, String> loginPassword = new HashMap<>();
            loginPassword.put("password", passwords[i]);
            loginPassword.put("login", "super_admin");
            Response responseGetAuthCookie = RestAssured
                    .given()
                    .body(loginPassword)
                    .when()
                    .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                    .andReturn();
//            System.out.println(i + "  -  " + responseGetAuthCookie.getCookie("auth_cookie"));
            String responseAuthCookie = responseGetAuthCookie.getCookie("auth_cookie");
            Map<String, String> cookie = new HashMap<>();
            cookie.put("auth_cookie", responseAuthCookie);
            Response responseCheckAuth = RestAssured
                    .given()
                    .cookies(cookie)
                    .when()
                    .post("https://playground.learnqa.ru/ajax/api/check_auth_cookie")
                    .andReturn();
            String body = responseCheckAuth.getBody().asString();
//            System.out.println(body);
            if(body.equals("You are authorized")) {
                password = passwords[i];
                System.out.println(body + ". The password is '" + password + "'.");
                break;
            }
            System.out.println(body + ". The password '" + passwords[i] + "' is not correct.");
        }

    }
}
