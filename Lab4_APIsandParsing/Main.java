

public class Main {

    public static void main(String[] args) {
        // URLs
        String usersApi = "https://fake-json-api.mock.beeceptor.com/users";
        String companiesApi = "https://fake-json-api.mock.beeceptor.com/companies";

        System.out.println("Получаем данные пользователей:");
        String usersResponse = FetchData.fetchDataFromApi(usersApi);
        if (!usersResponse.isEmpty()) {
            Parser.parseJson(usersResponse);
        }

        System.out.println("\nПолучаем данные по компаниям:");
        String companiesResponse = FetchData.fetchDataFromApi(companiesApi);
        if (!companiesResponse.isEmpty()) {
            Parser.parseJson(companiesResponse);
        }
    }

}
