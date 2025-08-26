package pet;

public enum PetType {
    // Thường
    NORMAL(0, "Đệ tử", 2000, "Xin hãy thu nhận làm đệ tử", Kind.NORMAL, 6, 3, false, null),
    MABU(1, "Mabư", 1500000, "Oa oa oa...", Kind.MABU, 10, 3, false, null),
    BEERUS(2, "Beerus Hắc Ám", 1500000, "Thần hủy diệt hiện thân tất cả quỳ xuống...", Kind.BEERUS, 10, 4, false, null),
    PIC(3, "Pic", 1500000, "Sư Phụ SooMe hiện thân tụi m quỳ xuống...", Kind.PIC, 10, 4, false, null),
    BLACK(4, "Black Hắc Ám", 1500000, "Ta sẽ cho người biết sức mạnh của một vị thần là như thế nào !", Kind.BLACK, 10,
            4, false, null),

    // Nhí/Special
    FIDE_NHI(5, "Fide Nhí", 1500000, "Con đây sư phụ ơi!!!", Kind.NHI, 10, 4, true, null),
    CELL_NHI(6, "Cell Nhí", 1500000, "Con đây sư phụ ơi!!!", Kind.NHI, 10, 4, true, null),
    BUU_NHI(7, "Bưu Nhí", 1500000, "Con đây sư phụ ơi!!!", Kind.NHI, 10, 4, true, null),
    ADR_BEACH(8, "Adr Bãi biển", 1500000, "Con đây sư phụ ơi!!!", Kind.NHI, 10, 4, true, null),
    BERRUS_NHI(9, "Berrus nhí", 1500000, "Con đây sư phụ ơi!!!", Kind.NHI, 10, 4, true, null),
    MABU_GAY(10, "Mabu gầy", 1500000, "Con đây sư phụ ơi!!!", Kind.NHI, 10, 4, true, null),

    // SUPER (Broly & Đệ tử super)
    SUPER_DE_TU(0, "Đệ tử", 1500000, "Xin hãy thu nhận làm đệ tử", Kind.SUPER, 10, 4, false, null),
    BROLY_MABU(1, "[Broly]Mabư", 1500000, "Xin hãy thu nhận làm đệ tử", Kind.SUPER, 10, 4, false, 1),
    BROLY_BEERUS(2, "[Broly]Beerus", 1500000, "Xin hãy thu nhận làm đệ tử", Kind.SUPER, 10, 4, false, 2),
    BROLY_PIC(3, "[Broly]Pic", 1500000, "Xin hãy thu nhận làm đệ tử", Kind.SUPER, 10, 4, false, 3),
    BROLY_BLACK(4, "[Broly]Black", 1500000, "Xin hãy thu nhận làm đệ tử", Kind.SUPER, 10, 4, false, 4);

    public enum Kind {
        NORMAL, MABU, BEERUS, PIC, BLACK, NHI, SUPER
    }

    public final int id; // typePet mặc định (nếu không override)
    public final String displayName;
    public final long defaultPower;
    public final String chatOnCreate;
    public final Kind kind;
    public final int bodySlots;
    public final int emptySkillSlots;
    public final boolean isNhiVariant;
    public final Integer overrideTypePet; // cho SUPER Broly map về 1..4

    PetType(int id, String displayName, long defaultPower, String chatOnCreate,
            Kind kind, int bodySlots, int emptySkillSlots, boolean isNhiVariant, Integer overrideTypePet) {
        this.id = id;
        this.displayName = displayName;
        this.defaultPower = defaultPower;
        this.chatOnCreate = chatOnCreate;
        this.kind = kind;
        this.bodySlots = bodySlots;
        this.emptySkillSlots = emptySkillSlots;
        this.isNhiVariant = isNhiVariant;
        this.overrideTypePet = overrideTypePet;
    }

    public static PetType byId(int id) {
        for (PetType t : values())
            if (t.id == id)
                return t;
        return NORMAL;
    }
}
