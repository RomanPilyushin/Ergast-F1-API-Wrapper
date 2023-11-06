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
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

/**
 * Ergast API Wrapper Class.
 */
public class Ergast {

    private static final Logger LOG = Logger.getLogger(Ergast.class.getName());
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String BASE_URL_TEMPLATE = "http://ergast.com/api/f1/{SEASON}/{ROUND}/{REQUEST}.json?limit={LIMIT}&offset={OFFSET}";

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

    private int season;
    private int limit;
    private int offset;

    public static final int NO_SEASON = -1;
    public static final int DEFAULT_LIMIT = 30;
    public static final int DEFAULT_OFFSET = 0;
    public static final int NO_ROUND = -1;

    public Ergast(int season, int limit, int offset) {
        setLimit(limit);
        setOffset(offset);
        this.season = season;
    }

    public Ergast() {
        this(NO_SEASON, DEFAULT_LIMIT, DEFAULT_OFFSET);
    }

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

    private <T> List<T> parseResponse(String request, int round, Class<T> type, String... jsonPath) throws IOException {
        String url = buildUrl(request, round);
        String jsonResponse = getJson(url);
        Parser<T> parser = new Parser<>(jsonResponse, jsonPath, type); // Instantiating with required arguments
        return parser.parse(jsonResponse, type, jsonPath);
    }


    private String getJson(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = connection.getResponseCode();

        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("HTTP request not successful. Response Code: " + responseCode);
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            return response.toString();
        }
    }

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

    // Add any additional methods or logic as needed.
}
