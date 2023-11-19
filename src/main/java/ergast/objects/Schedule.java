package ergast.objects;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    private int season;
    private int round;
    private String url;
    private String raceName;
    private Circuit circuit;
    private String date;
    private String time;

    public Schedule(String season, String round, String url, String raceName, Circuit circuit, String date, String time) {
        this(Integer.valueOf(season), Integer.valueOf(round), url, raceName, circuit, date, time);
    }

}
