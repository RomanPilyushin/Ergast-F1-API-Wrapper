package ergast.objects;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverStandings {
    private int position;
    private String positionText;
    private int points;
    private int wins;
    private Driver driver;
    private List<Constructor> constructors;

}
