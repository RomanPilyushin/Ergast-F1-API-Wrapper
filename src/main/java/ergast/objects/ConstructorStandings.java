package ergast.objects;

import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConstructorStandings {
    private int position;
    private String positionText;
    private int points;
    private int wins;
    private Constructor constructor;
}
