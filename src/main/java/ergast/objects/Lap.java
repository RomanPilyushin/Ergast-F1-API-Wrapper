package ergast.objects;

import java.util.List;

public class Lap {
    private int number;
    private List<Timing> timings;

    public Lap(int number, List<Timing> timings) {
        this.number = number;
        this.timings = timings;
    }

    public int getNumber() {
        return number;
    }

    public List<Timing> getTimings() {
        return timings;
    }

    @Override
    public String toString() {
        return "Lap{" +
                "number=" + number +
                ", timings=" + timings +
                '}';
    }
}
