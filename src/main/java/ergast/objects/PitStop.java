package ergast.objects;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class PitStop {
    private String driverId;
    private int stop;
    private int lap;
    private String time;
    private String duration;

}
