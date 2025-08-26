package player;

/*
 *
 *
 * @author NTD
 */
import item.Item;

public class SetClothes {

    private Player player;

    public SetClothes(Player player) {
        this.player = player;
    }

    public byte songoku;
    public byte thienXinHang;
    public byte kirin;
    public byte kaioken;
    public byte thanVuTruKaio;

    public byte ocTieu;
    public byte pikkoroDaimao;
    public byte picolo;
    public byte lienHoan;
    public byte nail;

    public byte kakarot;
    public byte cadic;
    public byte nappa;
    public byte giamSatThuong;
    public byte cadicM;
    public byte ctBatGioi;
    public byte worldcup;
    public byte setDHD;
    public byte HoatiemthuongPhonghoaluan;
    public boolean godClothes;
    public int ctHaiTac = -1;
    public int ctDietQuy = -1;
    public int ctBunmaTocMau = -1;
    public int ctTiecbaiBien = -1;
    public int thouhangnga;
    public  int zorokiem;
    public void setup() {
        setDefault();
        setupSKT();
        setupSKHNew();
        this.godClothes = true;
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                if (item.template.id > 567 || item.template.id < 555) {
                    this.godClothes = false;
                    break;
                }
            } else {
                this.godClothes = false;
                break;
            }
        }

         if (this.player.inventory.itemsBody.size() > 5) {
            Item ct = this.player.inventory.itemsBody.get(5);
            if (ct.isNotNullItem()) {
                switch (ct.template.id) {
                    case 618:
                    case 619:
                    case 620:
                    case 621:
                    case 622:
                    case 623:
                    case 624:
                    case 626:
                    case 627:
                        this.ctHaiTac = ct.template.id;
                        break;
                    case 1087:
                    case 1088:
                    case 1089:
                    case 1090:
                    case 1091:
                        this.ctDietQuy = ct.template.id;
                        break;
                    case 1208:
                    case 1209:
                    case 1210:
                        this.ctBunmaTocMau = ct.template.id;
                        break;
                    case 1234:
                    case 1235:
                    case 1236:
                        this.ctTiecbaiBien = ct.template.id;
                        break;

                }
            }

        }

        Item ctItem = null;
        Item gay = null;
        for (int i = 0; i < this.player.inventory.itemsBody.size(); i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                if (item.template.id == 1698) {
                    ctItem = item;
                }
                if (item.template.id == 1699) {
                    gay = item;
                }
            }
        }
        if (ctItem != null && gay != null) {
            this.ctBatGioi = 1;
        } else {
            this.ctBatGioi = 0;
        }
        // hỏa tiêm thương + phong hỏa luân
        Item Hoatiemthuong = null;
        Item Phonghoaluan = null;
        for (int i = 0; i < this.player.inventory.itemsBody.size(); i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                if (item.template.id == 1680) {
                    Hoatiemthuong = item;
                }
                if (item.template.id == 1676) {
                    Phonghoaluan = item;
                }
            }
        }
        if (Hoatiemthuong != null && Phonghoaluan != null) {
            this.HoatiemthuongPhonghoaluan = 1;
        } else {
            this.HoatiemthuongPhonghoaluan = 0;
        }
        // pet THỎ ú VÀ HẰNG NGA
        Item thou = null;
        Item hangnga = null;
        for (int i = 0; i < this.player.inventory.itemsBody.size(); i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                if (item.template.id == 1686) {
                    thou = item;
                }
                if (item.template.id == 1700) {
                    hangnga = item;
                }
            }
        }
        if (thou != null && hangnga != null) {
            this.thouhangnga = 1;
        } else {
            this.thouhangnga = 0;
        }
//kiếm yairobe zoro và kiếm
        Item kiem = null;
        Item zoro = null;
        for (int i = 0; i < this.player.inventory.itemsBody.size(); i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                if (item.template.id == 1456) {
                    kiem = item;
                }
                if (item.template.id == 1265) {
                    zoro = item;
                }
            }
        }
        if (kiem != null && zoro != null) {
            this.zorokiem = 1;
        } else {
            this.zorokiem = 0;
        }

    }

    private void setupSKT() {
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                boolean isActSet = false;
                for (Item.ItemOption io : item.itemOptions) {
                    switch (io.optionTemplate.id) {
                        case 129:
                        case 141:
                            isActSet = true;
                            songoku++;
                            break;
                        case 127:
                        case 139:
                            isActSet = true;
                            thienXinHang++;
                            break;
                        case 128:
                        case 140:
                            isActSet = true;
                            kirin++;
                            break;
                        case 131:
                        case 143:
                            isActSet = true;
                            ocTieu++;
                            break;
                        case 132:
                        case 144:
                            isActSet = true;
                            pikkoroDaimao++;
                            break;
                        case 130:
                        case 142:
                            isActSet = true;
                            picolo++;
                            break;
                        case 135:
                        case 138:
                            isActSet = true;
                            nappa++;
                            break;
                        case 133:
                        case 136:
                            isActSet = true;
                            kakarot++;
                            break;
                        case 134:
                        case 137:
                            isActSet = true;
                            cadic++;
                            break;
                        case 250:
                        case 253:
                            isActSet = true;
                            kaioken++;
                            break;
                        case 251:
                        case 254:
                            isActSet = true;
                            lienHoan++;
                            break;
                        case 252:
                        case 255:
                            isActSet = true;
                            giamSatThuong++;
                            break;

                        case 21:
                            if (io.param == 80) {
                                setDHD++;
                            }
                            break;
                    }

                    if (isActSet) {
                        break;
                    }
                }
            } else {
                break;
            }
        }
    }

    private void setupSKHNew() {
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                boolean isActSet = false;
                for (Item.ItemOption io : item.itemOptions) {
                    switch (io.optionTemplate.id) {
                        case 245:
                        case 246:
                        case 247:
                        case 248:
                            isActSet = true;
                            thanVuTruKaio++;
                            break;
                        case 237:
                        case 238:
                        case 239:
                        case 240:
                            isActSet = true;
                            nail++;
                            break;
                        case 241:
                        case 242:
                        case 243:
                        case 244:
                            isActSet = true;
                            cadicM++;
                            break;
                    }

                    if (isActSet) {
                        break;
                    }
                }
            } else {
                break;
            }
        }
    }

    private void setDefault() {
        this.songoku = 0;
        this.thienXinHang = 0;
        this.kirin = 0;
        this.kaioken = 0;
        this.ocTieu = 0;
        this.pikkoroDaimao = 0;
        this.picolo = 0;
        this.lienHoan = 0;
        this.kakarot = 0;
        this.cadic = 0;
        this.nappa = 0;
        this.giamSatThuong = 0;

        this.thanVuTruKaio = 0;

        this.nail = 0;

        this.cadicM = 0;

        this.setDHD = 0;
        this.worldcup = 0;
        this.godClothes = false;
        this.ctHaiTac = -1;
        this.ctDietQuy = -1;
        this.ctBunmaTocMau = -1;
        this.ctTiecbaiBien = -1;
    }


    public boolean checkSetGod() {
        if (this.player.isBot) {
            return false;
        }
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                if (item.template.id < 555 || item.template.id > 567) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public boolean checkSetDes() {
        if (this.player.isBot) {
            return false;
        }
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                if (item.template.id < 650 || item.template.id > 662) {

                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public void dispose() {
        this.player = null;
    }
}
