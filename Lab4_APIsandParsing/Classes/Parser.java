package Classes;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.util.Set;

public class Parser {
    public static void parseJson(String jsonResponse) {
        //System.out.println(jsonResponse); // checking
        try {
            // Check is it array or object (single)
            JsonReader reader = Json.createReader(new StringReader(jsonResponse));
            if (jsonResponse.trim().startsWith("[")) { // it's an array
                JsonArray jsonArray = reader.readArray();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject jsonObj = jsonArray.getJsonObject(i);
                    printJson(jsonObj);
                }
            } else { // it's an object
                JsonObject jsonObj = reader.readObject();
                printJson(jsonObj);
            }
            System.out.println("**************************************************************************");
            //System.out.print("\n");
        } catch (Exception e) {
            System.out.println("Ошибка при разборе JSON: " + e.getMessage());
        }
    }

    private static void printJson(JsonObject jsonObj) {
        Set<String> keys = jsonObj.keySet();
        int elemID = -1; // -1 because id[0..n] -- checking for errors
        StringBuilder obj_string = new StringBuilder();
        for (String key : keys) {
            if (key.equals("id")) {
                elemID = jsonObj.getInt(key);
            } else {
                // for delete ""
                Object value = jsonObj.get(key);
                if (value instanceof javax.json.JsonString) {
                    value = ((javax.json.JsonString) value).getString();
                }

                obj_string.append(key).append(": ").append(value).append("\n");
            }
        }
        // nice printing
        System.out.println("----------------------" + elemID + "----------------------");
        System.out.println(obj_string);

    }
}
