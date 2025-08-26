package models.Combine.manifest;

import consts.ConstFont;
import consts.ConstNpc;
import item.Item;
import models.Template;
import models.Combine.CombineService;
import player.Player;
import services.InventoryService;
import services.Service;
import services.SkillService;
import skill.Skill;
import utils.SkillUtil;
import utils.Util;

public class HocTuyetKy {

    public static void showInfoCombine(Player player) {
        Item biKipTuyetKy = InventoryService.gI().findItem(player.inventory.itemsBag, 1229);
        int quantityBiKipTuyetKy = biKipTuyetKy != null ? biKipTuyetKy.quantity : 0;
        int gem = player.inventory.getGemAndRuby();
        long gold = player.inventory.gold;
        int skillId = player.gender == 0 ? Skill.SUPER_KAME : player.gender == 1 ? Skill.MA_PHONG_BA : Skill.LIEN_HOAN_CHUONG;
        Skill curSkill = SkillUtil.getSkillbyId(player, skillId);
        boolean isHaveSkill = curSkill != null && curSkill.point > 0;
        int nextPoint = (isHaveSkill ? curSkill.point : 0) + 1;
        if (nextPoint > 7) {
            Service.gI().sendServerMessage(player, "Kỹ năng đã đạt tối đa!");
            return;
        }
        Template.SkillTemplate skillTemplate = SkillUtil.findSkillTemplate(skillId);
        StringBuilder text = new StringBuilder();
        text.append(ConstFont.BOLD_GREEN).append("Qua sẽ dạy ngươi tuyệt kỹ ").append(skillTemplate.name).append(" ").append(nextPoint).append("\n");
        if (nextPoint == 1) {
            text.append(quantityBiKipTuyetKy < 9999 ? ConstFont.BOLD_RED : ConstFont.BOLD_BLUE).append("Bí kíp tuyệt kỹ ").append(quantityBiKipTuyetKy).append("/9999\n");
            text.append(gold < 10_000_000 ? ConstFont.BOLD_RED : ConstFont.BOLD_BLUE).append("Giá vàng: 10.000.000\n");
            text.append(gem < 99 ? ConstFont.BOLD_RED : ConstFont.BOLD_BLUE).append("Giá ngọc: 99");
            if (quantityBiKipTuyetKy < 9999 || gold < 10_000_000 || gem < 99) {
                CombineService.gI().whis.createOtherMenu(player, ConstNpc.IGNORE_MENU, text.toString(), "Từ chối");
                return;
            }
        } else {
            text.append(quantityBiKipTuyetKy < 999 ? ConstFont.BOLD_RED : ConstFont.BOLD_BLUE).append("Bí kíp tuyệt kỹ ").append(quantityBiKipTuyetKy).append("/999\n");
            text.append(gold < 10_000_000 ? ConstFont.BOLD_RED : ConstFont.BOLD_BLUE).append("Giá vàng: 10.000.000");
            if (quantityBiKipTuyetKy < 999 || gold < 10_000_000) {
                CombineService.gI().whis.createOtherMenu(player, ConstNpc.IGNORE_MENU, text.toString(), "Từ chối");
                return;
            }
        }
        CombineService.gI().whis.createOtherMenu(player, ConstNpc.HOC_TUYET_KY, text.toString(), "Đồng ý", "Từ chối");
    }

    public static void hocTuyetKy(Player player) {
        Item biKipTuyetKy = InventoryService.gI().findItem(player.inventory.itemsBag, 1229);
        int quantityBiKipTuyetKy = biKipTuyetKy != null ? biKipTuyetKy.quantity : 0;
        int gem = player.inventory.getGemAndRuby();
        long gold = player.inventory.gold;
        int skillId = player.gender == 0 ? Skill.SUPER_KAME : player.gender == 1 ? Skill.MA_PHONG_BA : Skill.LIEN_HOAN_CHUONG;
        Skill curSkill = SkillUtil.getSkillbyId(player, skillId);
        boolean isHaveSkill = curSkill != null && curSkill.point > 0;
        int nextPoint = (isHaveSkill ? curSkill.point : 0) + 1;
        if (nextPoint > 7) {
            return;
        }
        if (curSkill != null && 9999 < 1000) {
            Service.gI().sendServerMessage(player, "Bạn cần đạt thông thạo tối đa.");
            return;
        }
        Template.SkillTemplate skillTemplate = SkillUtil.findSkillTemplate(skillId);
        int goldSub;
        int gemSub;
        int biKipSub;
        Skill nextSkill = SkillUtil.createSkill(skillTemplate.id, nextPoint);
        if (nextPoint == 1) {
            if (quantityBiKipTuyetKy < 9999 || gold < 10_000_000 || gem < 99) {
                return;
            }
            goldSub = 10_000_000;
            gemSub = 99;
            biKipSub = 9999;
        } else {
            if (quantityBiKipTuyetKy < 999 || gold < 10_000_000) {
                return;
            }
            goldSub = 10_000_000;
            gemSub = 0;
            biKipSub = 999;
        }
        SkillUtil.setSkill(player, nextSkill);
        CombineService.gI().sendAddItemCombine(player, ConstNpc.WHIS, biKipTuyetKy);
        player.inventory.subGemAndRuby(gemSub);
        player.inventory.gold -= goldSub;
        InventoryService.gI().subQuantityItemsBag(player, biKipTuyetKy, biKipSub);
        CombineService.gI().whis.npcChat(player, "Bư cô lô, ba cô la, bư ra bư zô...");
        CombineService.gI().sendEffSuccessVip(player, skillTemplate.iconId);
        InventoryService.gI().sendItemBag(player);
    }
}
