package ergast.objects;

import lombok.Data;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
public class Lap {
    private int number;
    private List<Timing> timings;

}
