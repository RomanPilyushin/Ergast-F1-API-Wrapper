package ergast;

import ergast.objects.RacePitStops;
import java.io.IOException;
import java.util.List;

public class Start {
    public static void main(String[] args) {
        try {
            Ergast ergast = new Ergast(2016, 100, 2);
            List<RacePitStops> results = ergast.getRacePitStops(21);

            results.forEach(result -> System.out.println(result));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
