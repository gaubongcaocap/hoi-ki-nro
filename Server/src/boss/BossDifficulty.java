package boss;

import map.Zone;
import player.Player;
import services.Service;
import utils.Util;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class BossDifficulty {
    private static final BossDifficulty I = new BossDifficulty();

    public static BossDifficulty gI() {
        return I;
    }

    // ===== CẤU HÌNH CHUNG =====
    private static final int MIN_PLAYERS = 1; // mốc tối thiểu
    private static final double PER_PLAYER = 0.1; // +10% mỗi người thêm
    private static final double MAX_SCALE = 2.0; // trần hệ số
    private static final boolean FILL_HP_ON_SCALE = false; // true nếu muốn hồi full máu khi scale

    // Tắt dynamic cho vài boss mini/sự kiện (bạn bổ sung tên ở đây)
    private static final Set<String> NAME_BLACKLIST = Set.of(
            "Sói hẹc quyn", "Ăn Trộm", "Ăn trộm TV",
            "Ông già Noel", "Lân con", "Ma trơi", "Dơi", "Bí ma");

    private static final class BaseStats {
        long baseHp;
        long baseDmg;
    }

    private static final class Key {
        final long bossId;
        final int level;

        Key(long id, int lv) {
            this.bossId = id;
            this.level = lv;
        }

        @Override
        public int hashCode() {
            return (int) (bossId ^ (bossId >>> 32)) * 31 + level;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Key k))
                return false;
            return k.bossId == bossId && k.level == level;
        }
    }

    // Lưu “gốc” theo (bossId, currentLevel) để không nhân chồng
    private final Map<Key, BaseStats> baseByKey = new ConcurrentHashMap<>();
    // Nhớ số người lần trước để khỏi scale liên tục
    private final Map<Long, Integer> lastAppliedCount = new ConcurrentHashMap<>();

    /** Gọi 1 lần khi boss vừa join map (sau initBase) */
    public void applyOnJoin(Boss b, Zone z) {
        reapply(b, z, true);
    }

    /** Gọi định kỳ trong Boss.update() mỗi 2s (xem bước 2) */
    public void tick(Boss b) {
        if (b == null || b.zone == null)
            return;
        if (b.currentLevel < 0)
            return; // tránh lỗi khi chưa respawn
        if (!isScalingEnabled(b))
            return;

        int now = countRealPlayers(b.zone);
        Integer last = lastAppliedCount.get(b.id);
        if (last == null || last != now) {
            reapply(b, b.zone, false);
        }
    }

    // ========================= Core =========================
    private void reapply(Boss b, Zone z, boolean force) {
        if (b == null || z == null)
            return;
        if (b.currentLevel < 0)
            return;
        if (!isScalingEnabled(b))
            return;

        int players = countRealPlayers(z);
        lastAppliedCount.put(b.id, players);

        // Lấy snapshot “gốc” theo (bossId, level)
        Key key = new Key(b.id, b.currentLevel);
        BaseStats bs = baseByKey.computeIfAbsent(key, k -> {
            BaseStats s = new BaseStats();
            s.baseHp = Math.max(1, b.nPoint.hpg);
            s.baseDmg = Math.max(1, b.nPoint.dameg);
            return s;
        });

        double factor = 1.0 + Math.max(0, players - MIN_PLAYERS) * PER_PLAYER;
        if (factor > MAX_SCALE)
            factor = MAX_SCALE;

        long newHp = Math.max(1, (long) Math.round(bs.baseHp * factor));
        long newDmg = Math.max(1, (long) Math.round(bs.baseDmg * factor));

        b.nPoint.hpg = newHp;
        b.nPoint.dameg = newDmg;
        if (FILL_HP_ON_SCALE)
            b.nPoint.hp = newHp; // hoặc giữ nguyên máu hiện tại
        b.nPoint.calPoint();
        Service.gI().point(b);

        // Debug (bật khi cần)
        // utils.Logger.error("[Diff] " + safeName(b) + " players=" + players + "
        // factor=" + factor + " hp=" + newHp + " dmg=" + newDmg);
    }

    private boolean isScalingEnabled(Boss b) {
        if (b == null)
            return false;
        if (b.currentLevel < 0)
            return false; // tránh crash khi chưa initBase

        String tn = safeName(b);
        if (tn != null && NAME_BLACKLIST.contains(tn))
            return false;

        // (tuỳ chọn) tắt ở map đặc biệt:
        // if (b.zone != null && MapService.gI().isMapOffline(b.zone.map.mapId)) return
        // false;

        return true;
    }

    private String safeName(Boss b) {
        try {
            // Boss.name chỉ có sau initBase; nếu null, đọc từ data currentLevel (cùng
            // package boss nên truy cập được)
            if (b.name != null)
                return b.name;
            return b.data[b.currentLevel].getName();
        } catch (Throwable ignore) {
            return b.name;
        }
    }

    private int countRealPlayers(Zone z) {
        int c = 0;
        for (Player p : z.getNotBosses()) {
            if (p == null)
                continue;
            if (p.isBot)
                continue;
            if (p.isPet)
                continue;
            if (p.isBoss)
                continue;
            c++;
        }
        return c;
    }

    public void resetBaseForLevel(Boss b) {
        if (b == null || b.currentLevel < 0)
            return;
        baseByKey.remove(new Key(b.id, b.currentLevel));
        lastAppliedCount.remove(b.id);
    }
}
