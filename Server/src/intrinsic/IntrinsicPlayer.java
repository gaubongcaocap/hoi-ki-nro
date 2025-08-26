package intrinsic;

/*
 *
 *
 * @author NTD
 */

import services.IntrinsicService;

public class IntrinsicPlayer {

    public byte countOpen;

    public Intrinsic intrinsic;

    public IntrinsicPlayer() {
        this.intrinsic = IntrinsicService.gI().getIntrinsicById(0);
    }

    public void dispose() {
        this.intrinsic = null;
    }
}
