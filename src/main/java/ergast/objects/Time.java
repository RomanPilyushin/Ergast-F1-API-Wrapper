package ergast.objects;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class Time {
    private int millis;
    private String time;

}
