package ergast.objects;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class Constructor {
    private final String constructorId;
    private final String url;
    private final String name;
    private final String nationality;
}
