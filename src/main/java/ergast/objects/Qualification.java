package ergast.objects;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class Qualification {
    private int number;
    private int position;
    private Driver driver;
    private Constructor constructor;
    private String q1;
    private String q2;
    private String q3;

}
