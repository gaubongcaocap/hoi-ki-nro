package power;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author NTD
 */

@Setter
@Getter
@AllArgsConstructor
@Builder
public class PowerLimit {

    private int id;
    private long power;
    private long hp;
    private long mp;
    private long damage;
    private int defense;
    private int critical;
}
