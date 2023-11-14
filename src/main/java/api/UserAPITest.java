import io.restassured.RestAssured;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import io.restassured.response.Response;

public class UserAPITest {

    @Test
    public void testCreateUser() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";

        // Создаем пользователя и получаем его ID
        String userId = createUser();

        // Проверяем, что пользователь был успешно создан (пример)
        System.out.println("User created with ID: " + userId);
    }

    // Метод для создания пользователя
    private String createUser() {
        return given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "  \"id\": 0,\n" +
                        "  \"username\": \"string\",\n" +
                        "  \"firstName\": \"string\",\n" +
                        "  \"lastName\": \"string\",\n" +
                        "  \"email\": \"string\",\n" +
                        "  \"password\": \"string\",\n" +
                        "  \"phone\": \"string\",\n" +
                        "  \"userStatus\": 0\n" +
                        "}")
                .when()
                .post("/user")
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("type", equalTo("unknown"))
                .extract()
                .path("message");
    }
    @Test
    public void testCreateAndFindUser() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";

        // Создаем пользователя и получаем его ID
        String userId = createUser();

        // Проверяем, что пользователь был успешно создан (пример)
        System.out.println("User created with ID: " + userId);

        // Находим пользователя по его имени с помощью метода GET
        findUserByUsername("Петос");
    }

    // Метод для создания пользователя
    private String createUserForSerch() {
        return given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "  \"id\": 222,\n" +
                        "  \"username\": \"Петос\",\n" +
                        "  \"firstName\": \"string\",\n" +
                        "  \"lastName\": \"string\",\n" +
                        "  \"email\": \"string\",\n" +
                        "  \"password\": \"string\",\n" +
                        "  \"phone\": \"string\",\n" +
                        "  \"userStatus\": 0\n" +
                        "}")
                .when()
                .post("/user")
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("type", equalTo("unknown"))
                .extract()
                .path("message");
    }

    // Метод для поиска пользователя по его имени
    private void findUserByUsername(String username) {
        given()
                .pathParam("username", username)
                .when()
                .get("/user/{username}")
                .then()
                .statusCode(200)
                .body("id", equalTo(222))
                .body("username", equalTo("Петос"))
                .body("firstName", equalTo("string"))
                .body("lastName", equalTo("string"))
                .body("email", equalTo("string"))
                .body("password", equalTo("string"))
                .body("phone", equalTo("string"))
                .body("userStatus", equalTo(0));
    }

    @Test
    public void testCreateAndDeleteUser() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";

        // Создаем пользователя и получаем его ID
        String username = "Петос";
        createUser(username);

        // Проверяем, что пользователь был успешно создан
        System.out.println("User created with username: " + username);

        // Удаляем пользователя по его имени
        deleteUserByUsername(username);

        // Повторно удаляем пользователя по его имени, ожидаем код 404
        deleteUserByUsernameExpecting404(username);
    }

    // Метод для создания пользователя
    private void createUser(String username) {
        given()
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "  \"id\": 222,\n" +
                        "  \"username\": \"" + username + "\",\n" +
                        "  \"firstName\": \"string\",\n" +
                        "  \"lastName\": \"string\",\n" +
                        "  \"email\": \"string\",\n" +
                        "  \"password\": \"string\",\n" +
                        "  \"phone\": \"string\",\n" +
                        "  \"userStatus\": 0\n" +
                        "}")
                .when()
                .post("/user")
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("type", equalTo("unknown"))
                .extract()
                .path("message");
    }

    // Метод для удаления пользователя по его имени
    private void deleteUserByUsername(String username) {
        given()
                .pathParam("username", username)
                .when()
                .delete("/user/{username}")
                .then()
                .statusCode(200);
        System.out.println("User with username " + username + " deleted successfully.");
    }

    // Метод для удаления пользователя по его имени с ожиданием кода 404
    private void deleteUserByUsernameExpecting404(String username) {
        given()
                .pathParam("username", username)
                .when()
                .delete("/user/{username}")
                .then()
                .statusCode(404);
        System.out.println("User with username " + username + " not found after deletion.");
    }
}