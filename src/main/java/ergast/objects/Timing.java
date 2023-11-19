package ergast.objects;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class Timing {
    private String driverId;
    private int position;
    private String time;

}
