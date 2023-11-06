package ergast;

import ergast.exceptions.QueryLimitException;
import ergast.exceptions.QueryOffsetException;
import ergast.exceptions.SeasonException;
import ergast.objects.*;
import ergast.parser.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.logging.Logger;

/**
 * Ergast API Wrapper Class.
 * This class provides methods to access and parse data from the Ergast API for different categories
 * like drivers, circuits, and race results.
 */

public class Ergast {

    // Logger to log information, warnings, or errors.
    private static final Logger LOG = Logger.getLogger(Ergast.class.getName());

    // User-Agent header value to be used in HTTP requests to simulate a web browser request.
    private static final String USER_AGENT = "Mozilla/5.0";

    // Template for the base URL used to construct Ergast API endpoint URLs.
    private static final String BASE_URL_TEMPLATE = "http://ergast.com/api/f1/{SEASON}/{ROUND}/{REQUEST}.json?limit={LIMIT}&offset={OFFSET}";

    // Constant strings representing different parts of the API endpoints.
    private static final String DRIVERS = "drivers";
    private static final String CIRCUITS = "circuits";
    private static final String CONSTRUCTORS = "constructors";
    private static final String SEASONS = "seasons";
    private static final String RESULTS = "results";
    private static final String QUALIFYING = "qualifying";
    private static final String DRIVER_STANDINGS = "driverStandings";
    private static final String CONSTRUCTOR_STANDINGS = "constructorStandings";
    private static final String FINISHING_STATUS = "status";
    private static final String LAP_TIMES = "laps";
    private static final String PIT_STOPS = "pitstops";


    // Parameters for the API request.
    private final int season; // The season year to query data for.
    private int limit; // The limit on the number of records to fetch.
    private int offset; // The offset for pagination of the results.


    // Constants for default or undefined values.
    public static final int NO_SEASON = -1; // Sentinel value for no specific season.
    public static final int DEFAULT_LIMIT = 30; // Default limit for API responses.
    public static final int DEFAULT_OFFSET = 0; // Default offset for API responses.
    public static final int NO_ROUND = -1; // Sentinel value for no specific round.


    // Constructors to initialize the Ergast API wrapper instance.
    public Ergast(int season, int limit, int offset) {
        setLimit(limit); // Validate and set the limit.
        setOffset(offset); // Validate and set the offset.
        this.season = season; // Set the season for the instance.
    }

    // Default constructor with default values.
    public Ergast() {
        this(NO_SEASON, DEFAULT_LIMIT, DEFAULT_OFFSET);
    }

    // Methods to retrieve data from the API, parsing JSON responses into lists of objects.
    public List<Driver> getDrivers() throws IOException {
        return parseResponse(DRIVERS, NO_ROUND, Driver.class, new String[]{"DriverTable", "Drivers"});
    }

    public List<Circuit> getCircuits() throws IOException {
        return parseResponse(CIRCUITS, NO_ROUND, Circuit.class, new String[]{"CircuitTable", "Circuits"});
    }

    public List<Season> getSeasons() throws IOException {
        return parseResponse(SEASONS, NO_ROUND, Season.class, new String[]{"SeasonTable", "Seasons"});
    }

    public List<Constructor> getConstructors() throws IOException {
        return parseResponse(CONSTRUCTORS, NO_ROUND, Constructor.class, new String[]{"ConstructorTable", "constructors"});
    }

    public List<RaceResult> getRaceResults(int round) throws IOException {
        requireSeason();
        return parseResponse(RESULTS, round, RaceResult.class, new String[]{"RaceTable", "Races", "Results"});
    }

    public List<Qualification> getQualificationResults(int round) throws IOException {
        requireSeason();
        return parseResponse(QUALIFYING, round, Qualification.class, new String[]{"RaceTable", "Races", "QualifyingResults"});
    }

    public List<DriverStandings> getDriverStandings(int round) throws IOException {
        requireSeason();
        return parseResponse(DRIVER_STANDINGS, round, DriverStandings.class, new String[]{"StandingsTable", "StandingsLists", "DriverStandings"});
    }

    public List<ConstructorStandings> getConstructorStandings(int round) throws IOException {
        requireSeason();
        return parseResponse(CONSTRUCTOR_STANDINGS, round, ConstructorStandings.class, new String[]{"StandingsTable", "StandingsLists", "ConstructorStandings"});
    }

    public List<FinishingStatus> getFinishingstatuses(int round) throws IOException {
        if (season == NO_SEASON && round != NO_ROUND) {
            throw new SeasonException("Season must be specified if round is specified.");
        }
        return parseResponse(FINISHING_STATUS, round, FinishingStatus.class, new String[]{"StatusTable", "Status"});
    }

    public List<LapTimes> getLapTimes(int round) throws IOException {
        requireSeasonAndRound(round);
        return parseResponse(LAP_TIMES, round, LapTimes.class, new String[]{"RaceTable", "Races"});
    }

    public List<RacePitStops> getRacePitStops(int round) throws IOException {
        requireSeasonAndRound(round);
        return parseResponse(PIT_STOPS, round, RacePitStops.class, new String[]{"RaceTable", "Races"});
    }

    // Helper method to construct the API URL.
    private String buildUrl(String request, int round) {
        String seasonStr = season == NO_SEASON ? "current" : Integer.toString(season);
        String roundStr = round == NO_ROUND ? "" : Integer.toString(round);
        return BASE_URL_TEMPLATE
                .replace("{SEASON}", seasonStr)
                .replace("{ROUND}", roundStr)
                .replace("{REQUEST}", request)
                .replace("{LIMIT}", Integer.toString(limit))
                .replace("{OFFSET}", Integer.toString(offset));
    }


    /**
     * Generic method to parse the JSON response from the API request.
     *
     * @param request  The specific API request endpoint.
     * @param round    The round number within a season.
     * @param type     The class type of the expected response objects.
     * @param jsonPath The JSON path to extract the desired array from the response.
     * @return A list of parsed objects of the specified type.
     * @throws IOException If an I/O exception occurs.
     */

    private <T> List<T> parseResponse(String request, int round, Class<T> type, String... jsonPath) throws IOException {
        String url = buildUrl(request, round);
        String jsonResponse = null;
        try {
            jsonResponse = getJson(url);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Parser<T> parser = new Parser<>(jsonResponse, jsonPath, type); // Instantiating with required arguments
        return parser.parse();
    }

    // Helper method to perform the actual HTTP request and retrieve the JSON string.
    private String getJson(String urlStr) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlStr))
                .header("User-Agent", USER_AGENT)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("HTTP request not successful. Response Code: " + response.statusCode());
        }

        return response.body();
    }

    // Helper methods to ensure required parameters like season and round are set before making certain API requests.
    private void requireSeason() {
        if (season == NO_SEASON) {
            throw new SeasonException("Season must be specified for this request.");
        }
    }

    private void requireSeasonAndRound(int round) {
        requireSeason();
        if (round == NO_ROUND) {
            throw new IllegalArgumentException("Round must be specified for this request.");
        }
    }

    // Methods to set the limit and offset for API requests with validations.
    public void setLimit(int limit) {
        if (limit < 1 || limit > 1000) {
            throw new QueryLimitException("Limit must be between 1 and 1000.");
        }
        this.limit = limit;
    }

    public void setOffset(int offset) {
        if (offset < 0) {
            throw new QueryOffsetException("Offset must be a non-negative integer.");
        }
        this.offset = offset;
    }
}
