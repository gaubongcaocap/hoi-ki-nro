package models.Combine;

/*
 *
 *
 * @author NTD
 */

import item.Item;
import java.util.ArrayList;
import java.util.List;

public class Combine {

    public long lastTimeCombine;

    public List<Item> itemsCombine;
    public int typeCombine;

    public int goldCombine;
    public int gemCombine;
    public float ratioCombine;
    public int countDaNangCap;
    public int countDaQuy;
    public short countDaBaoVe;
     public int DiemNangcap;
    public int DaNangcap;
    public float TileNangcap;

    public Combine() {
        this.itemsCombine = new ArrayList<>();
    }

    public void setTypeCombine(int type) {
        this.typeCombine = type;
    }

    public void clearItemCombine() {
        this.itemsCombine.clear();
    }

    public void clearParamCombine() {
        this.goldCombine = 0;
        this.gemCombine = 0;
        this.ratioCombine = 0;
        this.countDaNangCap = 0;
        this.countDaQuy = 0;
        this.countDaBaoVe = 0;
          this.DiemNangcap = 0;
        this.DaNangcap = 0;
        this.TileNangcap = 0;

    }

    public void dispose() {
        this.itemsCombine = null;
    }
}
