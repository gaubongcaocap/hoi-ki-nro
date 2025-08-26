package npc.npc_manifest;

/**
 * @author NTD
 */
import boss.BossID;
import consts.ConstNpc;
import item.Item;

import java.io.IOException;

import models.Combine.manifest.CheTaoTrangBiThienSu;
import network.Message;
import npc.Npc;
import player.Player;
import services.InventoryService;
import services.Service;
import services.SkillService;
import services.func.ChangeMapService;
import models.Combine.CombineService;
import models.Training.TrainingService;
import services.NpcService;
import services.func.TopService;
import shop.ShopService;
import skill.Skill;
import utils.SkillUtil;
import utils.Util;

public class Whis extends Npc {

    private static final int COST_HD = 50000000;

    public Whis(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            switch (this.mapId) {
                case 48 -> {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Mày muốn nâng cấp Set KH VIP sao, gặp bà già Hạt mít để nâng cấp nhé",
                                "Hướng\ndẫn\nthêm", "Từ chối");
                }
                case 154 -> {
                    Item BiKiepTuyetKy = InventoryService.gI().findItem(player.inventory.itemsBag, 1229);
                    if (BiKiepTuyetKy != null) {
                        createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Thử đánh với ta xem nào.\nNgươi còn 1 lượt nữa cơ mà.",
                                "Nâng cấp\nTrang bị\nThiên sứ",
                                "Học\ntuyệt kỹ"
                                // ,"Top 100",
                                // "[LV:" + (player.traning.getTop() + 1) + "]"
                        );
                    } else {
                        createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Thử đánh với ta xem nào.\nNgươi còn 1 lượt nữa cơ mà.",
                                "Nâng cấp\nTrang bị\nThiên sứ"
                                // ,"Top 100",
                                // "[LV:" + (player.traning.getTop() + 1) + "]"
                        );
                    }
                }
                default -> {
                    super.openBaseMenu(player);
                }
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (player.iDMark.isBaseMenu() && (this.mapId == 154 || this.mapId == 169)) {
                Item BiKiepTuyetKy = InventoryService.gI().findItem(player.inventory.itemsBag, 1229);
                switch (select) {
                    case 0 -> {
                        if (this.mapId == 154) {
                            if (!player.setClothes.checkSetDes()) {
                                this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        "Ngươi hãy trang bị đủ 5 món trang bị Hủy Diệt rồi ta nói chuyện tiếp.",
                                        "OK");
                                return;
                            }
                            this.createOtherMenu(player, 5,
                                    "Ta sẽ giúp ngươi chế tạo trang bị thiên sứ",
                                    "Chế tạo",
                                    "Từ chối");
                        } else if (this.mapId == 164) {
                            ChangeMapService.gI().changeMapInYard(player, 154, -1, 758);
                        }
                    }
                    case 1 -> {
                        if (BiKiepTuyetKy != null) {
                            int idskill = Skill.MA_PHONG_BA;
                            if (player.gender == 0) {
                                idskill = Skill.SUPER_KAME;
                            } else if (player.gender == 2) {
                                idskill = Skill.LIEN_HOAN_CHUONG;
                            }
                            Skill curSkill = SkillUtil.getSkillbyId(player, idskill);
                            boolean checkskill = false;
                            if (curSkill == null) {
                                checkskill = true;

                            } else if (curSkill.point == 0) {
                                checkskill = true;
                            }
                            boolean duSachTuyetKy = false;
                            boolean duVang = false;
                            boolean duNgoc = false;
                            String nameSkill = player.gender == 0 ? "Super kamejoko" : player.gender == 1 ? "Ma phong ba" : "Ca đíc liên hoàn chưởng";
                            if (BiKiepTuyetKy.quantity >= 9999) {
                                duSachTuyetKy = true;
                            }
                            if (player.inventory.gold >= 1_000_000_000) {
                                duVang = true;
                            }
                            if (player.inventory.gem >= 990) {
                                duNgoc = true;
                            }
                            if (checkskill) {
                                if (duSachTuyetKy && duVang && duNgoc) {
                                    this.createOtherMenu(player, 6,
                                            "|1|Ta sẽ dạy ngươi tuyệt kỹ " + nameSkill + " 1"
                                            + "\n|2|Bí kiếp tuyệt kỹ " + BiKiepTuyetKy.quantity + "/999\n"
                                            + "|2|Giá vàng: 1 tỷ\n"
                                            + "|2|Giá ngọc: 999",
                                            "Đồng ý", "Từ chối");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                            "|1|Ta sẽ dạy ngươi tuyệt kỹ " + nameSkill + " 1"
                                            + "\n" + (duSachTuyetKy ? "|2|" : "|7|") + "Bí kiếp tuyệt kỹ " + BiKiepTuyetKy.quantity + "/999"
                                            + "\n" + (duVang ? "|2|" : "|7|") + "Giá vàng: 1 tỷ"
                                            + "\n" + (duNgoc ? "|2|" : "|7|") + "Giá ngọc: 999", "Từ chối");
                                }
                            } else {
                                if (duSachTuyetKy && duVang && duNgoc) {
                                    this.createOtherMenu(player, 6,
                                            "|1|Ta sẽ dạy ngươi tuyệt kỹ " + nameSkill + " " + (curSkill.point + 1)
                                            + "\n|2|Bí kiếp tuyệt kỹ " + BiKiepTuyetKy.quantity + "/999\n"
                                            + "|2|Giá vàng: 1 tỷ\n"
                                            + "|2|Giá ngọc: 999",
                                            "Đồng ý", "Từ chối");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                            "|1|Ta sẽ dạy ngươi tuyệt kỹ " + nameSkill + " " + (curSkill.point + 1)
                                            + "\n" + (duSachTuyetKy ? "|2|" : "|7|") + "Bí kiếp tuyệt kỹ " + BiKiepTuyetKy.quantity + "/999"
                                            + "\n" + (duVang ? "|2|" : "|7|") + "Giá vàng: 1 tỷ"
                                            + "\n" + (duNgoc ? "|2|" : "|7|") + "Giá ngọc: 999", "Từ chối");
                                }
                            }
                        } else {
                            TopService.showListTop(player, 3);
                        }
                    }
                    case 2 -> {
                        TrainingService.gI().callBoss(player, BossID.WHIS, false);
                    }
                }
            } else if (player.iDMark.getIndexMenu() == 5) {
                switch (select) {
                    case 0 ->
                        CombineService.gI().openTabCombine(player, CombineService.CHE_TAO_TRANG_BI_THIEN_SU);
                }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                switch (player.combine.typeCombine) {
                    case CombineService.CHE_TAO_TRANG_BI_THIEN_SU: {
                        CombineService.gI().startCombine(player);
                    }
                }
            } else if (player.iDMark.getIndexMenu() == 6) {
                switch (select) {
                    case 0 -> {
                        Item sach = InventoryService.gI().findItemBag(player, 2055);
                        if (sach != null && player.inventory.gold >= 1000000000 && player.inventory.gem > 999 && player.nPoint.power >= 60000000000L) {
                            int idskill = Skill.MA_PHONG_BA;
                            int iconSkill = 11194;
                            if (player.gender == 0) {
                                idskill = Skill.SUPER_KAME;
                                iconSkill = 11162;
                            } else if (player.gender == 2) {
                                idskill = Skill.LIEN_HOAN_CHUONG;
                                iconSkill = 11193;
                            }
                            Skill curSkill = SkillUtil.getSkillbyId(player, idskill);
                            boolean checkskill = false;
                            if (curSkill == null) {
                                checkskill = true;
                            } else if (curSkill.point == 0) {
                                checkskill = true;
                            }
                            if (checkskill) {
                                if (sach.quantity >= 9999) {
                                    try {
                                        int trubk = 99;
                                        String msg = "Tư chất kém!";
                                        String msg2 = "Ngu dốt!";
                                        if (Util.isTrue(15, 15)) {
                                            trubk = 9999;
                                            msg = "Học skill thành công!";
                                            msg2 = "Chúc mừng con nhé!";
                                            switch (player.gender) {
                                                case 0 ->
                                                    SkillService.gI().learSkillSpecial(player, Skill.SUPER_KAME);
                                                case 2 ->
                                                    SkillService.gI().learSkillSpecial(player, Skill.LIEN_HOAN_CHUONG);
                                                default ->
                                                    SkillService.gI().learSkillSpecial(player, Skill.MA_PHONG_BA);
                                            }
                                        } else {
                                            iconSkill = 15313;
                                        }
                                        Message msgg;
                                        msgg = new Message(-81);
                                        msgg.writer().writeByte(0);
                                        msgg.writer().writeUTF("Skill 9");
                                        msgg.writer().writeUTF("NRO");
                                        msgg.writer().writeShort(tempId);
                                        player.sendMessage(msgg);
                                        msgg.cleanup();
                                        msgg = new Message(-81);
                                        msgg.writer().writeByte(1);
                                        msgg.writer().writeByte(1);
                                        msgg.writer().writeByte(InventoryService.gI().getIndexItemBag(player, sach));
                                        player.sendMessage(msgg);
                                        msgg.cleanup();
                                        msgg = new Message(-81);
                                        msgg.writer().writeByte(trubk == 99 ? 8 : 7);
                                        msgg.writer().writeShort(iconSkill);
                                        player.sendMessage(msgg);
                                        msgg.cleanup();
                                        this.npcChat(player, msg2);
                                        Service.gI().sendThongBao(player, msg);
                                        InventoryService.gI().subQuantityItemsBag(player, sach, trubk);
                                        player.inventory.gold -= 1000000000;
                                        player.inventory.gem -= 999;
                                        InventoryService.gI().sendItemBag(player);
                                        Service.gI().sendThongBao(player, msg);
                                    } catch (IOException e) {
                                    }
                                } else {
                                    int sosach = 9999 - sach.quantity;
                                    Service.gI().sendThongBao(player, "Ngươi còn thiếu " + sosach + " bí kíp nữa.\nHãy tìm đủ rồi đến gặp ta.");
                                }
                            } else {
                                if (sach.quantity >= 999) {
                                    if (curSkill.currLevel < 1000) {
                                        npcChat(player, "Ngươi chưa luyện skill đến mức thành thạo. Luyện thêm đi.");
                                    } else if (curSkill.point >= 9) {
                                        npcChat(player, "Skill của ngươi đã đến cấp độ tối đa");
                                    } else {
                                        try {
                                            int trubk = 99;
                                            String msg = "Tư chất kém!";
                                            String msg2 = "Ngu dốt!";
                                            if (Util.isTrue(1, 30)) {
                                                trubk = 999;
                                                msg = "Nâng skill thành công!";
                                                msg2 = "Chúc mừng con nhé!";
                                                curSkill.point++;
                                                curSkill.currLevel = 0;
                                                SkillService.gI().sendCurrLevelSpecial(player, curSkill);
                                            } else {
                                                iconSkill = 15313;
                                            }
                                            Message msgg;
                                            msgg = new Message(-81);
                                            msgg.writer().writeByte(0);
                                            msgg.writer().writeUTF("Skill 9");
                                            msgg.writer().writeUTF("NroVoz");
                                            msgg.writer().writeShort(tempId);
                                            player.sendMessage(msgg);
                                            msgg.cleanup();
                                            msgg = new Message(-81);
                                            msgg.writer().writeByte(1);
                                            msgg.writer().writeByte(1);
                                            msgg.writer().writeByte(InventoryService.gI().getIndexItemBag(player, sach));
                                            player.sendMessage(msgg);
                                            msgg.cleanup();
                                            msgg = new Message(-81);
                                            msgg.writer().writeByte(trubk == 99 ? 8 : 7);
                                            msgg.writer().writeShort(iconSkill);
                                            player.sendMessage(msgg);
                                            msgg.cleanup();
                                            this.npcChat(player, msg2);
                                            Service.gI().sendThongBao(player, msg);
                                            InventoryService.gI().subQuantityItemsBag(player, sach, trubk);
                                            player.inventory.gold -= 1000000000;
                                            player.inventory.gem -= 99;
                                            InventoryService.gI().sendItemBag(player);
                                            Service.gI().sendThongBao(player, msg);
                                        } catch (IOException e) {
                                        }
                                    }
                                } else {
                                    int sosach = 999 - sach.quantity;
                                    Service.gI().sendThongBao(player, "Ngươi còn thiếu " + sosach + " bí kíp nữa.\nHãy tìm đủ rồi đến gặp ta.");
                                }
                            }
                        } else if (player.nPoint.power < 60000000000L) {
                            Service.gI().sendThongBao(player, "Ngươi không đủ sức mạnh để học tuyệt kỹ");
                        } else if (player.inventory.gold <= 1000000000) {
                            Service.gI().sendThongBao(player, "Hãy có đủ vàng thì quay lại gặp ta.");
                        } else if (player.inventory.gem <= 999) {
                            Service.gI().sendThongBao(player, "Hãy có đủ ngọc xanh thì quay lại gặp ta.");
                        }
                    }
                }

            } else if (player.iDMark.isBaseMenu() && this.mapId == 169) {
                if (select == 0) {
                    ChangeMapService.gI().changeMapBySpaceShip(player, 154, -1, 450);
                }
            } else if (player.iDMark.isBaseMenu() && this.mapId == 48) {
                switch (select) {
                    case 0:
                        NpcService.gI().createTutorial(player, tempId, this.avartar, ConstNpc.HUONG_DAN_DOI_SKH_VIP);
                        break;
                    default:
                        break;
                }

            } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_NANG_DOI_SKH_VIP) {
                if (select == 0) {
                    CombineService.gI().startCombine(player);
                }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_CHE_TAO_CHAN_MENH) {
                if (select == 0) {
                    CombineService.gI().startCombine(player);
                }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_NANG_CAP_CHAN_MENH) {
                if (select == 0) {
                    CombineService.gI().startCombine(player);
                }
            }
        }
    }

    private void vaoMapHD(Player player) {
        if (player.getSession().player.nPoint.power >= 80000000000L && player.inventory.gold > COST_HD) {
            player.inventory.gold -= COST_HD;
            Service.gI().sendMoney(player);
            ChangeMapService.gI().changeMapBySpaceShip(player, 169, -1, 168);
        } else {
            this.npcChat(player, "Bạn chưa đủ điều kiện để vào");
            Service.gI().sendThongBao(player, "Yêu cầu sm lớn hơn 80 Tỷ và 50 Tr vàng.");
        }
    }

}
