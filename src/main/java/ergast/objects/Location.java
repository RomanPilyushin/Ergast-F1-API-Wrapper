package ergast.objects;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class Location {
    private float lat;
    private float lng;
    private String locality;
    private String country;

}
