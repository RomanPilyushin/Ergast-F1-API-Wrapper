package ergast.objects;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FastestLap {
    private int rank;
    private int lap;
    private Time time;
    private AverageSpeed averageSpeed;

}
