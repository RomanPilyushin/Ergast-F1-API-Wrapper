package ergast.objects;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class Circuit {
    private final String circuitId;
    private final String url;
    private final String circuitName;
    private final Location location;
}
