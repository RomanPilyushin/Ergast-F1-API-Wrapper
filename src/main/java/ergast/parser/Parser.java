package ergast.parser;

import com.google.gson.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
* The Parser class is a generic JSON parser that is designed to parse a JSON string
* into a list of Java objects of a specified type. It is configured to navigate through
* a complex JSON structure by specifying a path to the target JSON array. It also contains
* a method to "fix" the JSON string by ensuring certain keys are in lowercase, which may be
* important for consistency or matching the fields of the Java objects.
 */

public class Parser<T> {
    // A reusable Gson instance for JSON operations to improve performance instead of creating a new one each time.
    private static final Gson GSON = new Gson();

    // A compiled regex pattern that will be used to fix the JSON string by targeting specific object keys.
    // It makes the targeted keys lowercase, which is likely intended to normalize the keys before parsing.
    private static final Pattern FIX_JSON_PATTERN = Pattern.compile(
            "\"(Location|Circuit|Constructor|Driver|Time|AverageSpeed|FastestLap|Q1|Q2|Q3|Constructors|Laps|Timings|PitStops)\"",
            Pattern.CASE_INSENSITIVE);

    private String json; // The raw JSON string to be parsed.
    private final String[] jsonObjects; // An array representing the hierarchy of JSON objects to reach the target data.
    private final Class<T> type; // The class type of the objects to be created from the JSON.

    public Parser(String json, String[] jsonObjects, Class<T> type) {
        this.json = json;
        this.jsonObjects = jsonObjects;
        this.type = type;
    }

    public List<T> parse() { // This method parses the JSON into a list of objects of type T.
        fixJson(); // First, fixes the JSON string based on the pattern defined above.

        JsonArray jarray = getJsonArray(); // Gets the JSON array that contains the data to be parsed.
        List<T> entities = new ArrayList<>(); // Prepares a list to store the parsed objects.

        for (JsonElement jelement : jarray) {
            entities.add(GSON.fromJson(jelement, type)); // Parses each JSON element into an object of type T and adds it to the list.
        }

        return entities; // Returns the list of parsed objects.
    }

    private JsonArray getJsonArray() { // Helper method to navigate through the JSON hierarchy and get the desired JSON array.
        JsonElement jelement = JsonParser.parseString(json);
        JsonObject jobject = jelement.getAsJsonObject().getAsJsonObject("MRData");

        // Loops through the array of jsonObjects to dive into the JSON hierarchy.
        for (int i = 0; i < jsonObjects.length - 1; i++) {
            jelement = jobject.get(jsonObjects[i]);
            if (jelement != null && !jelement.isJsonNull()) {
                jobject = jelement.getAsJsonObject(); // Navigates one level deeper in the JSON object hierarchy.
            } else {
                // Throws an exception if an expected JSON object in the hierarchy is missing.
                throw new JsonParseException("Missing object in JSON path: " + jsonObjects[i]);
            }
        }

        // Obtains the last JSON element which is expected to be a JSON array.
        JsonElement jsonArrayElement = jobject.get(jsonObjects[jsonObjects.length - 1]);
        if (jsonArrayElement != null && jsonArrayElement.isJsonArray()) {
            return jsonArrayElement.getAsJsonArray(); // Returns the JSON array if it exists.
        } else {
            // Throws an exception if the final element is not a JSON array as expected.
            throw new JsonParseException("Missing array in JSON path: " + jsonObjects[jsonObjects.length - 1]);
        }
    }

    private void fixJson() { // Method to normalize certain keys in the JSON string to lowercase.
        json = FIX_JSON_PATTERN.matcher(json).replaceAll(matchResult ->
                "\"" + matchResult.group(1).toLowerCase() + "\"" // Replaces the matched keys with their lowercase equivalents.
        );
    }
}
