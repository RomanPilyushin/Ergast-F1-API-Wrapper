package ergast;

import ergast.objects.RacePitStops;
import ergast.objects.Schedule;
import ergast.objects.Season;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Start {
    public static void main(String[] args) {
        try {
            Ergast ergast = new Ergast(2016, 100, 2);
            List<RacePitStops> racePitStopsList = ergast.getRacePitStops(21);
            writeListToCSV(racePitStopsList);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static <T> void writeListToCSV(List<T> list) throws IOException {
        if (list == null || list.isEmpty()) {
            System.out.println("List is empty or null.");
            return;
        }

        String className = list.get(0).getClass().getSimpleName();
        String fileName = className + ".csv";

        try (
                FileWriter writer = new FileWriter(fileName);
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("Data"))
        ) {
            for (T item : list) {
                csvPrinter.printRecord(item.toString());
            }
            csvPrinter.flush();
        }
    }
}
