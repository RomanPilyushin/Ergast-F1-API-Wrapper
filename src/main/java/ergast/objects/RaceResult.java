package ergast.objects;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class RaceResult {
    private int number;
    private int position;
    private String positionText;
    private int points;
    private Driver driver;
    private Constructor constructor;
    private int grid;
    private int laps;
    private String status;
    private Time time;
    private FastestLap fastestLap;

}
