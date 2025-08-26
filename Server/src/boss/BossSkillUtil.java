package boss;

import utils.Util;

public final class BossSkillUtil {
    private BossSkillUtil() {
    }

    // Định ước sentinel cho các khoảng random cooldown
    public static final int CD_RAND_5K_10K = -5000; // nghĩa: random 5.000..10.000
    public static final int CD_RAND_1K_2K = -1001; // ví dụ khác nếu cần
    // ... thêm các sentinel khác nếu bạn cần

    public static int materializeCooldown(int cd) {
        switch (cd) {
            case CD_RAND_5K_10K:
                return Util.nextInt(5_000, 10_000);
            case CD_RAND_1K_2K:
                return Util.nextInt(1_000, 2_000);
            default:
                return cd;
        }
    }

    // Gọi hàm này khi clone/khởi tạo skill cho boss từ mảng int[][]
    public static int[][] materializeAll(int[][] skills) {
        if (skills == null)
            return null;
        int[][] out = new int[skills.length][];
        for (int i = 0; i < skills.length; i++) {
            int[] s = skills[i];
            if (s == null || s.length < 3) {
                out[i] = s;
                continue;
            }
            out[i] = new int[] { s[0], s[1], materializeCooldown(s[2]) };
        }
        return out;
    }
}
