package power;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author NTD
 */

@Getter
@Setter
@AllArgsConstructor
@Builder
public class Caption {

    private int id;
    private String earth, saiya, namek;
    private long power;

    public String getCaption(int planet) {
        String caption = earth;
        if (planet == 1) {
            caption = namek;
        } else if (planet == 2) {
            caption = saiya;
        }
        return caption;
    }
    public long getPower() {
        return power;
    }

    public void setPower(long power) {
        this.power = power;
    }
}
