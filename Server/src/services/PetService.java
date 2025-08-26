package services;

/*
 *
 *
 * @author NTD
 */
import consts.ConstPlayer;
import pet.PetType;
import player.NewPet;
import player.Pet;
import player.Player;
import services.func.ChangeMapService;
import utils.SkillUtil;
import utils.Util;

public class PetService {

    private static PetService instance;

    public static PetService gI() {
        if (instance == null)
            instance = new PetService();
        return instance;
    }

    // ===================== CORE FACTORY =====================
    private void createPet(Player player, PetType type,
            boolean isChange,
            Byte genderOpt, // null => random
            Byte limitPowerOpt, // null => giữ mặc định
            boolean forcePlayerGender,
            Byte superTypeOverride // chỉ dùng cho SUPER_DE_TU để set typePet = type tham số
    ) {
        // >>> CÁCH 1: nếu đã có đệ mà vẫn gọi create, tự chuyển thành change
        final boolean hasPet = (player != null && player.pet != null);
        if (!isChange && hasPet) {
            isChange = true;
        }

        byte limitPower;
        if (isChange && hasPet) {
            // giữ lại limitPower đệ cũ
            limitPower = player.pet.nPoint.limitPower;

            // dọn trạng thái đệ cũ
            if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
                player.pet.unFusion();
            }
            ChangeMapService.gI().exitMap(player.pet);
            player.pet.dispose();
            player.pet = null;
        } else {
            // tạo mới lần đầu: thiết lập limitPower như logic cũ
            limitPower = (type.isNhiVariant) ? (byte) 1
                    : (limitPowerOpt != null ? limitPowerOpt : (byte) 1);
        }

        Thread.startVirtualThread(() -> {
            try {
                Pet pet = new Pet(player);
                pet.name = "$" + type.displayName;

                // gender
                if (forcePlayerGender) {
                    pet.gender = player.gender;
                } else {
                    pet.gender = (genderOpt != null) ? genderOpt : (byte) Util.nextInt(0, 2);
                }

                pet.id = player.isPl() ? -player.id : -Math.abs(player.id) - 100000;

                // power & typePet
                pet.nPoint.power = type.defaultPower;
                int typePet = (type.overrideTypePet != null) ? type.overrideTypePet : type.id;
                if (type == PetType.SUPER_DE_TU && superTypeOverride != null) {
                    typePet = superTypeOverride; // đúng hành vi createNewPetSuper cũ
                }
                pet.typePet = (byte) typePet;

                // stamina
                pet.nPoint.stamina = 1000;
                pet.nPoint.maxStamina = 1000;

                // stats
                if (type.kind == PetType.Kind.NHI) {
                    fillNhiStatsByType(pet, type);
                } else {
                    int[] data = getDataForKind(type.kind);
                    pet.nPoint.hpg = data[0];
                    pet.nPoint.mpg = data[1];
                    pet.nPoint.hpMax = data[0];
                    pet.nPoint.mpMax = data[1];
                    pet.nPoint.dameg = data[2];
                    pet.nPoint.defg = data[3];
                    pet.nPoint.critg = data[4];
                }

                // body slots
                for (int i = 0; i < type.bodySlots; i++) {
                    pet.inventory.itemsBody.add(ItemService.gI().createItemNull());
                }
                // skills
                pet.playerSkill.skills.add(SkillUtil.createSkill(Util.nextInt(0, 2) * 2, 1));
                for (int i = 0; i < type.emptySkillSlots; i++) {
                    pet.playerSkill.skills.add(SkillUtil.createEmptySkill());
                }

                pet.nPoint.setFullHpMp();
                player.pet = pet;
                player.pet.nPoint.limitPower = limitPower;

                // fusion bonus theo loại
                applyFusionBonusIfAny(player, type);

                Thread.sleep(1000);
                Service.gI().chatJustForMe(player, player.pet, type.chatOnCreate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // ===================== DATA HELPERS =====================
    private int[] getDataForKind(PetType.Kind kind) {
        switch (kind) {
            case MABU:
                return getDataPetMabu();
            case PIC:
                return getDataPetPic();
            case BEERUS:
                return getDataPetBerrus();
            case BLACK:
                return getDataPetBlack();
            case NORMAL:
            case SUPER:
            default:
                return getDataPetNormal();
        }
    }

    private int[] getDataPetBerrus() { 
        int[] d = new int[5];
        d[0] = Util.nextInt(5000, 7500);//hp
        d[1] = Util.nextInt(5000, 7500);// mp
        d[2] = Util.nextInt(130, 250); // dame
        d[3] = Util.nextInt(100, 200); // def
        d[4] = Util.nextInt(0, 40); // crit
        return d;
    }

    private int[] getDataPetBlack() { 
        int[] d = new int[5];
        d[0] = Util.nextInt(5000, 7500);//hp
        d[1] = Util.nextInt(5000, 7500);// mp
        d[2] = Util.nextInt(130, 250); // dame
        d[3] = Util.nextInt(100, 200); // def
        d[4] = Util.nextInt(0, 40); // crit
        return d;
    }

    private int[] getDataPetNormal() {
        int[] d = new int[5];
        d[0] = Util.nextInt(1000, 3000) ; // hp
        d[1] = Util.nextInt(1000, 3000); // mp
        d[2] = Util.nextInt(20, 60); // dame
        d[3] = Util.nextInt(8, 50); // def
        d[4] = Util.nextInt(0, 10); // crit
        return d;
    }

    private int[] getDataPetMabu() {
        int[] d = new int[5];
        d[0] = Util.nextInt(4000, 5500); // hp
        d[1] = Util.nextInt(4000, 5500); // mp
        d[2] = Util.nextInt(70, 150); // dame
        d[3] = Util.nextInt(100, 150); // def
        d[4] = Util.nextInt(0, 10); // crit
        return d;
    }

    private int[] getDataPetPic() {
        int[] d = new int[5];
        d[0] = Util.nextInt(4000, 6000); // hp
        d[1] = Util.nextInt(4000, 6000); // mp
        d[2] = Util.nextInt(70, 150); // dame
        d[3] = Util.nextInt(100, 150); // def
        d[4] = Util.nextInt(0, 10); // crit
        return d;
    }

    private void fillNhiStatsByType(Pet pet, PetType type) {
        pet.nPoint.hpg = Util.nextInt(4500, 6000);
        pet.nPoint.mpg = Util.nextInt(4500, 6000);
        pet.nPoint.hpMax = pet.nPoint.hpg;
        pet.nPoint.mpMax = pet.nPoint.mpg;
        pet.nPoint.dameg = Util.nextInt(100, 210);
        pet.nPoint.defg = Util.nextInt(50, 200);
        pet.nPoint.critg = (type == PetType.FIDE_NHI || type == PetType.CELL_NHI) ? 5 : 15;
    }

    private void applyFusionBonusIfAny(Player player, PetType type) {
        switch (type) {
            case BROLY_MABU:
            case BROLY_PIC:
            case BROLY_BLACK:
            case BROLY_BEERUS:
                player.pointfusion.setHpFusion(Util.nextInt(5, 5));
                player.pointfusion.setMpFusion(Util.nextInt(5, 5));
                player.pointfusion.setDameFusion(Util.nextInt(5, 5));
                break;
            case BUU_NHI:
                player.pointfusion.setHpFusion(Util.nextInt(1, 5));
                player.pointfusion.setMpFusion(Util.nextInt(1, 5));
                player.pointfusion.setDameFusion(Util.nextInt(1, 5));
                break;
            case FIDE_NHI:
                player.pointfusion.setHpFusion(Util.nextInt(5, 10));
                player.pointfusion.setMpFusion(Util.nextInt(5, 10));
                player.pointfusion.setDameFusion(Util.nextInt(5, 10));
                break;
            case CELL_NHI:
                player.pointfusion.setHpFusion(Util.nextInt(10, 15));
                player.pointfusion.setMpFusion(Util.nextInt(10, 15));
                player.pointfusion.setDameFusion(Util.nextInt(10, 15));
                break;
            case ADR_BEACH:
                player.pointfusion.setHpFusion(Util.nextInt(15, 20));
                player.pointfusion.setMpFusion(Util.nextInt(15, 20));
                player.pointfusion.setDameFusion(Util.nextInt(15, 20));
                break;
            case MABU_GAY:
                player.pointfusion.setHpFusion(Util.nextInt(20, 25));
                player.pointfusion.setMpFusion(Util.nextInt(20, 25));
                player.pointfusion.setDameFusion(Util.nextInt(20, 25));
                break;
            case BERRUS_NHI:
                player.pointfusion.setHpFusion(Util.nextInt(25, 30));
                player.pointfusion.setMpFusion(Util.nextInt(25, 30));
                player.pointfusion.setDameFusion(Util.nextInt(25, 30));
                break;
            case BLACK:
                player.pointfusion.setHpFusion(Util.nextInt(30, 35));
                player.pointfusion.setMpFusion(Util.nextInt(30, 35));
                player.pointfusion.setDameFusion(Util.nextInt(30, 35));
                break;
            case BEERUS:
                player.pointfusion.setHpFusion(Util.nextInt(35, 45));
                player.pointfusion.setMpFusion(Util.nextInt(35, 45));
                player.pointfusion.setDameFusion(Util.nextInt(35, 45));
                break;
            default:
                player.pointfusion.setHpFusion(0);
                player.pointfusion.setMpFusion(0);
                player.pointfusion.setDameFusion(0);
        }
    }

    // ===================== PUBLIC API (giữ hàm cũ) =====================
    // Normal
    public void createNormalPet(Player p, int gender, byte... limit) {
        createPet(p, PetType.NORMAL, false, (byte) gender, one(limit), false, null);
    }

    public void createNormalPet(Player p, byte... limit) {
        createPet(p, PetType.NORMAL, false, null, one(limit), false, null);
    }

    public void changeNormalPet(Player p, int gender) {
        createPet(p, PetType.NORMAL, true, (byte) gender, null, false, null);
    }

    public void changeNormalPet(Player p) {
        createPet(p, PetType.NORMAL, true, null, null, false, null);
    }

    // Mabu
    public void createMabuPet(Player p, byte... limit) {
        createPet(p, PetType.MABU, false, null, one(limit), false, null);
    }

    public void createMabuPet(Player p, int gender, byte... limit) {
        createPet(p, PetType.MABU, false, (byte) gender, one(limit), false, null);
    }

    public void changeMabuPet(Player p) {
        createPet(p, PetType.MABU, true, null, null, false, null);
    }

    public void changeMabuPet(Player p, int gender) {
        createPet(p, PetType.MABU, true, (byte) gender, null, false, null);
    }

    // Beerus
    public void createBeerusPet(Player p, byte... limit) {
        createPet(p, PetType.BEERUS, false, null, one(limit), false, null);
    }

    public void createBeerusPet(Player p, int gender, byte... limit) {
        createPet(p, PetType.BEERUS, false, (byte) gender, one(limit), false, null);
    }

    public void createBeerusPet(Player p, boolean isChange, int gender) {
        createPet(p, PetType.BEERUS, isChange, (byte) gender, null, false, null);
    }

    public void changeBeerusPet(Player p) {
        createPet(p, PetType.BEERUS, true, null, null, false, null);
    }

    public void changeBeerusPet(Player p, int gender) {
        createPet(p, PetType.BEERUS, true, (byte) gender, null, false, null);
    }

    // Pic
    public void createPicPet(Player p, byte... limit) {
        createPet(p, PetType.PIC, false, null, one(limit), false, null);
    }

    public void createPicPet(Player p, int gender, byte... limit) {
        createPet(p, PetType.PIC, false, (byte) gender, one(limit), false, null);
    }

    public void changePicPet(Player p) {
        createPet(p, PetType.PIC, true, null, null, false, null);
    }

    public void changePicPet(Player p, int gender) {
        createPet(p, PetType.PIC, true, (byte) gender, null, false, null);
    }

    // Black
    public void createBlackPet(Player p, byte... limit) {
        createPet(p, PetType.BLACK, false, null, one(limit), false, null);
    }

    public void createBlackPet(Player p, int gender, byte... limit) {
        createPet(p, PetType.BLACK, false, (byte) gender, one(limit), false, null);
    }

    public void createBlackPet(Player p, boolean isChange, int gender) {
        createPet(p, PetType.BLACK, isChange, (byte) gender, null, false, null);
    }

    public void changeBlackPet(Player p) {
        createPet(p, PetType.BLACK, true, null, null, false, null);
    }

    public void changeBlackPet(Player p, int gender) {
        createPet(p, PetType.BLACK, true, (byte) gender, null, false, null);
    }

    public void changeBlackPet(Player p, boolean isChange, int gender) {
        createPet(p, PetType.BLACK, isChange, (byte) gender, null, false, null);
    }

    // Nhí/Special
    public void createPetFideNhi(Player p, boolean isChange, byte gender) {
        createPet(p, PetType.FIDE_NHI, isChange, gender, null, false, null);
    }

    public void createPetCellNhi(Player p, boolean isChange, byte gender) {
        createPet(p, PetType.CELL_NHI, isChange, gender, null, false, null);
    }

    public void createPetBuuNhi(Player p, boolean isChange, byte gender) {
        createPet(p, PetType.BUU_NHI, isChange, gender, null, false, null);
    }

    public void createPetAdrBeach(Player p, boolean isChange, byte gender) {
        createPet(p, PetType.ADR_BEACH, isChange, gender, null, false, null);
    }

    public void createPetBerrusNhi(Player p, boolean isChange, byte gender) {
        createPet(p, PetType.BERRUS_NHI, isChange, gender, null, false, null);
    }

    public void createPetMabuGay(Player p, boolean isChange, byte gender) {
        createPet(p, PetType.MABU_GAY, isChange, gender, null, false, null);
    }

    // SUPER: map đúng hành vi cũ
    public void createNormalPetSuperGender(Player p, int gender, byte type) {
        createPet(p, mapBrolyType(type), false, null, null, true, null); // dùng gender của player
    }

    public void createNormalPetSuper(Player p, int gender, byte type) {
        // tên "Đệ tử", typePet = type (1..4), gender random/param (gốc code random –
        // mình giữ random để y nguyên)
        createPet(p, PetType.SUPER_DE_TU, false, null, null, false, type);
    }

    // ===================== UTILS =====================
    private static Byte one(byte[] arr) {
        return (arr != null && arr.length == 1) ? arr[0] : null;
    }

    private PetType mapBrolyType(byte type) {
        switch (type) {
            case 1:
                return PetType.BROLY_MABU;
            case 2:
                return PetType.BROLY_BEERUS;
            case 3:
                return PetType.BROLY_PIC;
            case 4:
                return PetType.BROLY_BLACK;
            default:
                return PetType.SUPER_DE_TU; // fallback
        }
    }

    // ===== NewPet tùy biến ngoại hình & delete & rename (giữ nguyên) =====
    public static void Pet2(Player pl, int h, int b, int l) {
        if (pl.newPet != null) {
            pl.newPet.dispose();
        }
        pl.newPet = new NewPet(pl, (short) h, (short) b, (short) l);
        pl.newPet.name = "$";
        pl.newPet.gender = pl.gender;
        pl.newPet.nPoint.tiemNang = 1;
        pl.newPet.nPoint.power = 1;
        pl.newPet.nPoint.limitPower = 1;
        pl.newPet.nPoint.hpg = 500000000;
        pl.newPet.nPoint.mpg = 500000000;
        pl.newPet.nPoint.hp = 500000000;
        pl.newPet.nPoint.mp = 500000000;
        pl.newPet.nPoint.dameg = 1;
        pl.newPet.nPoint.defg = 1;
        pl.newPet.nPoint.critg = 1;
        pl.newPet.nPoint.stamina = 1;
        pl.newPet.nPoint.setBasePoint();
        pl.newPet.nPoint.setFullHpMp();
    }

    public void deletePet(Player player) {
        Pet pet = player.pet;
        if (pet != null) {
            if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
                pet.unFusion();
            }
            ChangeMapService.gI().exitMap(pet);
            pet.dispose();
            player.pet = null;
        }
    }

    public void reapplyFusion(Player p) {
        if (p == null || p.pet == null)
            return;
        applyFusionBonusIfAny(p, PetType.byId(p.pet.typePet));
    }

    public void changeNamePet(Player player, String name) {
        try {
            if (!InventoryService.gI().isExistItemBag(player, 400)) {
                Service.gI().sendThongBao(player, "Bạn cần thẻ đặt tên đệ tử, mua tại Santa");
                return;
            } else if (Util.haveSpecialCharacter(name)) {
                Service.gI().sendThongBao(player, "Tên không được chứa ký tự đặc biệt");
                return;
            } else if (name.length() > 10) {
                Service.gI().sendThongBao(player, "Tên quá dài");
                return;
            }
            ChangeMapService.gI().exitMap(player.pet);
            player.pet.name = "$" + name.toLowerCase().trim();
            InventoryService.gI().subQuantityItemsBag(player, InventoryService.gI().findItemBag(player, 400), 1);
            Thread.startVirtualThread(() -> {
                try {
                    Thread.sleep(1000);
                    Service.gI().chatJustForMe(player, player.pet, "Cảm ơn sư phụ đã đặt cho con tên " + name);
                } catch (Exception e) {
                }
            });
        } catch (Exception ex) {

        }
    }
}
