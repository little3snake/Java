import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class FetchData {
    public static String fetchDataFromApi(String apiUrl) {
        StringBuilder result = new StringBuilder();
        try {
            // Create a connection with API
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Checking the answer
            if (connection.getResponseCode() == 200) { // OK
                // Reading data from request
                InputStream in = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(in);
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    result.append(line);
                }
                br.close();
            }
            else {
                System.out.println("Ошибка: код ответа " + connection.getResponseCode());
            }
            connection.disconnect();

        } catch (MalformedURLException e) {
            System.out.println("Ошибка при запросе к API по URL: " + e.getMessage());
        } catch (ProtocolException e) {
            System.out.println("Ошибка в протоколе при выполнении запроса: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Ошибка при выполнении запроса: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Неизвестная ошибка при выполненни запроса: " + e.getMessage());
        }
        return result.toString();
    }
}
