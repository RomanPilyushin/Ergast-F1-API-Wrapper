package ergast.parser;

import com.google.gson.*;
import ergast.objects.ConstructorStandings;
import ergast.objects.DriverStandings;
import ergast.objects.Qualification;
import ergast.objects.RaceResult;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Parser<T> {
    private static final Gson GSON = new Gson(); // Reuse the GSON instance
    private static final Pattern FIX_JSON_PATTERN = Pattern.compile(
            "\"(Location|Circuit|Constructor|Driver|Time|AverageSpeed|FastestLap|Q1|Q2|Q3|Constructors|Laps|Timings|PitStops)\"",
            Pattern.CASE_INSENSITIVE);

    private String json;
    private String[] jsonObjects;
    private Class<T> type;

    public Parser(String json, String[] jsonObjects, Class<T> type) {
        this.json = json;
        this.jsonObjects = jsonObjects;
        this.type = type;
    }

    public List<T> parse() { // Removed redundant parameters
        fixJson();

        JsonArray jarray = getJsonArray();
        List<T> entities = new ArrayList<>();

        for (JsonElement jelement : jarray) {
            entities.add(GSON.fromJson(jelement, type));
        }

        return entities;
    }

    private JsonArray getJsonArray() {
        JsonElement jelement = JsonParser.parseString(json);
        JsonObject jobject = jelement.getAsJsonObject().getAsJsonObject("MRData");

        for (int i = 0; i < jsonObjects.length - 1; i++) {
            jelement = jobject.get(jsonObjects[i]);
            if (jelement != null && !jelement.isJsonNull()) {
                jobject = jelement.getAsJsonObject();
            } else {
                throw new JsonParseException("Missing object in JSON path: " + jsonObjects[i]);
            }
        }

        JsonElement jsonArrayElement = jobject.get(jsonObjects[jsonObjects.length - 1]);
        if (jsonArrayElement != null && jsonArrayElement.isJsonArray()) {
            return jsonArrayElement.getAsJsonArray();
        } else {
            throw new JsonParseException("Missing array in JSON path: " + jsonObjects[jsonObjects.length - 1]);
        }
    }

    private void fixJson() {
        json = FIX_JSON_PATTERN.matcher(json).replaceAll(matchResult ->
                "\"" + matchResult.group(1).toLowerCase() + "\""
        );
    }
}
