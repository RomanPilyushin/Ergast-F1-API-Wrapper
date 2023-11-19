package ergast.objects;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinishingStatus {
    private int statusId;
    private int count;
    private String status;

}
