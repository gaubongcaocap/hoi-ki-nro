using System;
using System.Security.Cryptography;
using System.Text;
using Assets.Assets.Scripts.Assembly_CSharp;
using Assets.src.e;
using Assets.src.f;
using Assets.src.g;
using Mod.XMAP;
using UnityEngine;

public class Controller : IMessageHandler
{
    protected static Controller me;

    protected static Controller me2;

    public Message messWait;

    public static bool isLoadingData = false;

    public static bool isConnectOK;

    public static bool isConnectionFail;

    public static bool isDisconnected;

    public static bool isMain;

    private float demCount;

    private int move;

    private int total;

    public static bool isStopReadMessage;

    public static MyHashTable frameHT_NEWBOSS = new MyHashTable();

    public const sbyte PHUBAN_TYPE_CHIENTRUONGNAMEK = 0;

    public const sbyte PHUBAN_START = 0;

    public const sbyte PHUBAN_UPDATE_POINT = 1;

    public const sbyte PHUBAN_END = 2;

    public const sbyte PHUBAN_LIFE = 4;

    public const sbyte PHUBAN_INFO = 5;

    public static Controller gI()
    {
        if (me == null)
        {
            me = new Controller();
        }
        return me;
    }

    public static Controller gI2()
    {
        if (me2 == null)
        {
            me2 = new Controller();
        }
        return me2;
    }

    public void onConnectOK(bool isMain1)
    {
        isMain = isMain1;
        mSystem.onConnectOK();
    }

    public void onConnectionFail(bool isMain1)
    {
        isMain = isMain1;
        mSystem.onConnectionFail();
    }

    public void onDisconnected(bool isMain1)
    {
        isMain = isMain1;
        mSystem.onDisconnected();
    }

    public void requestItemPlayer(Message msg)
    {
        try
        {
            int num = msg.reader().readUnsignedByte();
            Item item = GameScr.currentCharViewInfo.arrItemBody[num];
            item.saleCoinLock = msg.reader().readInt();
            item.sys = msg.reader().readByte();
            item.options = new MyVector();
            try
            {
                while (true)
                {
                    item.options.addElement(new ItemOption(msg.reader().readUnsignedByte(), msg.reader().readUnsignedShort()));
                }
            }
            catch (Exception ex)
            {
                Cout.println("Loi tairequestItemPlayer 1" + ex.ToString());
            }
        }
        catch (Exception ex2)
        {
            Cout.println("Loi tairequestItemPlayer 2" + ex2.ToString());
        }
    }

   	public void onMessage(Message msg)
	{
		GameCanvas.debugSession.removeAllElements();
		GameCanvas.debug("SA1", 2);
		try
		{
			Char @char = null;
			Mob mob = null;
			MyVector myVector = new MyVector();
			int num = 0;
			GameCanvas.timeLoading = 15;
			Controller2.readMessage(msg);
			switch (msg.command)
			{
			case 70:
				QuayTamBao.receiveMsg(msg);
				break;
			case 71:
				QuaNapTuan.receiveMsg(msg);
				break;
			case -71:
				AdminPopup.gI().readInfoPopup(msg);
				break;
			case 0:
				readLogin(msg);
				break;
			case 24:
				read_opt(msg);
				break;
			case 20:
				phuban_Info(msg);
				break;
			case 66:
				readGetImgByName(msg);
				break;
			case 65:
			{
				sbyte b67 = msg.reader().readSByte();
				string text7 = msg.reader().readUTF();
				short num217 = msg.reader().readShort();
				if (ItemTime.isExistMessage(b67))
				{
					if (num217 != 0)
					{
						ItemTime.getMessageById(b67).initTimeText(b67, text7, num217);
					}
					else
					{
						GameScr.textTime.removeElement(ItemTime.getMessageById(b67));
					}
				}
				else
				{
					ItemTime itemTime = new ItemTime();
					itemTime.initTimeText(b67, text7, num217);
					GameScr.textTime.addElement(itemTime);
				}
				break;
			}
			case 112:
				switch (msg.reader().readByte())
				{
				case 0:
					Panel.spearcialImage = msg.reader().readShort();
					Panel.specialInfo = msg.reader().readUTF();
					ModFunc.GI().UpdateIntrinsicInfo(Panel.specialInfo);
					break;
				case 1:
				{
					sbyte tabSize = msg.reader().readByte();
					Char.myCharz().infoSpeacialSkill = new string[tabSize][];
					Char.myCharz().imgSpeacialSkill = new short[tabSize][];
					GameCanvas.panel.speacialTabName = new string[tabSize][];
					for (int num195 = 0; num195 < tabSize; num195++)
					{
						GameCanvas.panel.speacialTabName[num195] = new string[2];
						string[] array12 = Res.split(msg.reader().readUTF(), "\n", 0);
						if (array12.Length == 2)
						{
							GameCanvas.panel.speacialTabName[num195] = array12;
						}
						if (array12.Length == 1)
						{
							GameCanvas.panel.speacialTabName[num195][0] = array12[0];
							GameCanvas.panel.speacialTabName[num195][1] = string.Empty;
						}
						int size2 = msg.reader().readByte();
						Char.myCharz().infoSpeacialSkill[num195] = new string[size2];
						Char.myCharz().imgSpeacialSkill[num195] = new short[size2];
						for (int num196 = 0; num196 < size2; num196++)
						{
							Char.myCharz().imgSpeacialSkill[num195][num196] = msg.reader().readShort();
							Char.myCharz().infoSpeacialSkill[num195][num196] = msg.reader().readUTF();
						}
					}
					GameCanvas.panel.tabName[25] = GameCanvas.panel.speacialTabName;
					GameCanvas.panel.setTypeSpeacialSkill();
					GameCanvas.panel.show();
					break;
				}
				}
				break;
			case -98:
			{
				sbyte num19 = msg.reader().readByte();
				GameCanvas.menu.showMenu = false;
				if (num19 == 0)
				{
					GameCanvas.startYesNoDlg(msg.reader().readUTF(), new Command(mResources.YES, GameCanvas.instance, 888397, msg.reader().readUTF()), new Command(mResources.NO, GameCanvas.instance, 888396, null));
				}
				break;
			}
			case -97:
				Char.myCharz().cNangdong = msg.reader().readInt();
				break;
			 case -96:
                    {
                        sbyte typeTop = msg.reader().readByte();
                        GameCanvas.panel.vTop.removeAllElements();
                        string topName = msg.reader().readUTF();
                        sbyte b49 = msg.reader().readByte();
                        for (int num128 = 0; num128 < b49; num128++)
                        {
                            int rank = msg.reader().readInt();
                            int pId = msg.reader().readInt();
                            short headID = msg.reader().readShort();
                            short headICON = msg.reader().readShort();
                            short body = msg.reader().readShort();
                            short leg = msg.reader().readShort();
                            string name = msg.reader().readUTF();
                            string info3 = msg.reader().readUTF();
                            TopInfo topInfo = new TopInfo();
                            topInfo.rank = rank;
                            topInfo.headID = headID;
                            topInfo.headICON = headICON;
                            topInfo.body = body;
                            topInfo.leg = leg;
                            topInfo.name = name;
                            topInfo.info = info3;
                            topInfo.info2 = msg.reader().readUTF();
                            topInfo.pId = pId;
                            GameCanvas.panel.vTop.addElement(topInfo);
                        }
                        GameCanvas.panel.topName = topName;
                        GameCanvas.panel.setTypeTop(typeTop);
                        GameCanvas.panel.show();
                        break;
                    }
			case -94:
				while (msg.reader().available() > 0)
				{
					short num136 = msg.reader().readShort();
					int num137 = msg.reader().readInt();
					for (int num138 = 0; num138 < Char.myCharz().vSkill.size(); num138++)
					{
						Skill skill = (Skill)Char.myCharz().vSkill.elementAt(num138);
						if (skill != null && skill.skillId == num136 && num137 < skill.coolDown)
						{
							skill.lastTimeUseThisSkill = mSystem.currentTimeMillis() - (skill.coolDown - num137);
						}
					}
				}
				break;
			case -95:
			{
				sbyte type4 = msg.reader().readByte();
				if (type4 == 0)
				{
					int num244 = msg.reader().readInt();
					short templateId = msg.reader().readShort();
					double hp = msg.reader().readIntToLongDQT();
					SoundMn.gI().explode_1();
					if (num244 == Char.myCharz().charID)
					{
						Char.myCharz().mobMe = new Mob(num244, isDisable: false, isDontMove: false, isFire: false, isIce: false, isWind: false, templateId, 1, hp, 0, hp, (short)(Char.myCharz().cx + ((Char.myCharz().cdir != 1) ? (-40) : 40)), (short)Char.myCharz().cy, 4, 0)
						{
							isMobMe = true
						};
						EffecMn.addEff(new Effect(18, Char.myCharz().mobMe.x, Char.myCharz().mobMe.y, 2, 10, -1));
						Char.myCharz().tMobMeBorn = 30;
						GameScr.vMob.addElement(Char.myCharz().mobMe);
					}
					else
					{
						@char = GameScr.findCharInMap(num244);
						if (@char != null)
						{
							Mob mob7 = new Mob(num244, isDisable: false, isDontMove: false, isFire: false, isIce: false, isWind: false, templateId, 1, hp, 0, hp, (short)@char.cx, (short)@char.cy, 4, 0);
							mob7.isMobMe = true;
							@char.mobMe = mob7;
							GameScr.vMob.addElement(@char.mobMe);
						}
						else
						{
							Mob mob8 = GameScr.findMobInMap(num244);
							if (mob8 == null)
							{
								mob8 = new Mob(num244, isDisable: false, isDontMove: false, isFire: false, isIce: false, isWind: false, templateId, 1, hp, 0, hp, -100, -100, 4, 0)
								{
									isMobMe = true
								};
								GameScr.vMob.addElement(mob8);
							}
						}
					}
				}
				if (type4 == 1)
				{
					int num245 = msg.reader().readInt();
					int mobId = msg.reader().readByte();
					if (num245 == Char.myCharz().charID)
					{
						if (GameScr.findMobInMap(mobId) != null)
						{
							Char.myCharz().mobMe.attackOtherMob(GameScr.findMobInMap(mobId));
						}
					}
					else
					{
						@char = GameScr.findCharInMap(num245);
						if (@char != null && GameScr.findMobInMap(mobId) != null)
						{
							@char.mobMe.attackOtherMob(GameScr.findMobInMap(mobId));
						}
					}
				}
				if (type4 == 2)
				{
					int num246 = msg.reader().readInt();
					int num247 = msg.reader().readInt();
					double dameHit3 = msg.reader().readIntToLongDQT();
					double cHPNew = msg.reader().readIntToLongDQT();
					if (num246 == Char.myCharz().charID)
					{
						@char = GameScr.findCharInMap(num247);
						if (@char != null)
						{
							@char.cHPNew = cHPNew;
							if (Char.myCharz().mobMe.isBusyAttackSomeOne)
							{
								@char.doInjure(dameHit3, 0.0, isCrit: false, isMob: true);
							}
							else
							{
								Char.myCharz().mobMe.dame = dameHit3;
								Char.myCharz().mobMe.setAttack(@char);
							}
						}
					}
					else
					{
						mob = GameScr.findMobInMap(num246);
						if (mob != null)
						{
							if (num247 == Char.myCharz().charID)
							{
								Char.myCharz().cHPNew = cHPNew;
								if (mob.isBusyAttackSomeOne)
								{
									Char.myCharz().doInjure(dameHit3, 0.0, isCrit: false, isMob: true);
								}
								else
								{
									mob.dame = dameHit3;
									mob.setAttack(Char.myCharz());
								}
							}
							else
							{
								@char = GameScr.findCharInMap(num247);
								if (@char != null)
								{
									@char.cHPNew = cHPNew;
									if (mob.isBusyAttackSomeOne)
									{
										@char.doInjure(dameHit3, 0.0, isCrit: false, isMob: true);
									}
									else
									{
										mob.dame = dameHit3;
										mob.setAttack(@char);
									}
								}
							}
						}
					}
				}
				if (type4 == 3)
				{
					int num248 = msg.reader().readInt();
					int mobId2 = msg.reader().readInt();
					double hp2 = msg.reader().readIntToLongDQT();
					double dame = msg.reader().readIntToLongDQT();
					@char = null;
					@char = ((Char.myCharz().charID != num248) ? GameScr.findCharInMap(num248) : Char.myCharz());
					if (@char != null)
					{
						mob = GameScr.findMobInMap(mobId2);
						if (@char.mobMe != null)
						{
							@char.mobMe.attackOtherMob(mob);
						}
						if (mob != null)
						{
							mob.hp = hp2;
							mob.updateHp_bar();
							if (dame == 0.0)
							{
								mob.x = mob.xFirst;
								mob.y = mob.yFirst;
								GameScr.startFlyText(mResources.miss, mob.x, mob.y - mob.h, 0, -2, mFont.MISS);
							}
							else
							{
								GameScr.startFlyText("-" + dame, mob.x, mob.y - mob.h, 0, -2, mFont.ORANGE);
							}
						}
					}
				}
				_ = 4;
				if (type4 == 5)
				{
					int num249 = msg.reader().readInt();
					sbyte b75 = msg.reader().readByte();
					int mobId3 = msg.reader().readInt();
					double num250 = msg.reader().readIntToLongDQT();
					double hp3 = msg.reader().readIntToLongDQT();
					@char = null;
					@char = ((num249 != Char.myCharz().charID) ? GameScr.findCharInMap(num249) : Char.myCharz());
					if (@char == null)
					{
						return;
					}
					if ((TileMap.tileTypeAtPixel(@char.cx, @char.cy) & 2) == 2)
					{
						@char.setSkillPaint(GameScr.sks[b75], 0);
					}
					else
					{
						@char.setSkillPaint(GameScr.sks[b75], 1);
					}
					Mob mob9 = GameScr.findMobInMap(mobId3);
					if (@char.cx <= mob9.x)
					{
						@char.cdir = 1;
					}
					else
					{
						@char.cdir = -1;
					}
					@char.mobFocus = mob9;
					mob9.hp = hp3;
					mob9.updateHp_bar();
					if (num250 == 0.0)
					{
						mob9.x = mob9.xFirst;
						mob9.y = mob9.yFirst;
						GameScr.startFlyText(mResources.miss, mob9.x, mob9.y - mob9.h, 0, -2, mFont.MISS);
					}
					else
					{
						GameScr.startFlyText("-" + num250, mob9.x, mob9.y - mob9.h, 0, -2, mFont.ORANGE);
					}
				}
				if (type4 == 6)
				{
					int num251 = msg.reader().readInt();
					if (num251 == Char.myCharz().charID)
					{
						Char.myCharz().mobMe.startDie();
					}
					else
					{
						GameScr.findCharInMap(num251)?.mobMe.startDie();
					}
				}
				if (type4 != 7)
				{
					break;
				}
				int num252 = msg.reader().readInt();
				if (num252 == Char.myCharz().charID)
				{
					Char.myCharz().mobMe = null;
					for (int num253 = 0; num253 < GameScr.vMob.size(); num253++)
					{
						if (((Mob)GameScr.vMob.elementAt(num253)).mobId == num252)
						{
							GameScr.vMob.removeElementAt(num253);
						}
					}
					break;
				}
				@char = GameScr.findCharInMap(num252);
				for (int num254 = 0; num254 < GameScr.vMob.size(); num254++)
				{
					if (((Mob)GameScr.vMob.elementAt(num254)).mobId == num252)
					{
						GameScr.vMob.removeElementAt(num254);
					}
				}
				if (@char != null)
				{
					@char.mobMe = null;
				}
				break;
			}
			case -92:
				Main.typeClient = msg.reader().readByte();
				if (Rms.loadRMSString("ResVersion") == null)
				{
					Rms.clearAll();
				}
				Rms.saveRMSInt("clienttype", Main.typeClient);
				Rms.saveRMSInt("lastZoomlevel", mGraphics.zoomLevel);
				if (Rms.loadRMSString("ResVersion") == null)
				{
					GameCanvas.startOK(mResources.plsRestartGame, 8885, null);
				}
				break;
			case -91:
			{
				sbyte b46 = msg.reader().readByte();
				GameCanvas.panel.mapNames = new string[b46];
				GameCanvas.panel.planetNames = new string[b46];
				for (int num158 = 0; num158 < b46; num158++)
				{
					GameCanvas.panel.mapNames[num158] = msg.reader().readUTF();
					GameCanvas.panel.planetNames[num158] = msg.reader().readUTF();
				}
				AutoXmap.ShowPanelMapTrans();
				break;
			}
			case -90:
			{
				sbyte num255 = msg.reader().readByte();
				int num256 = msg.reader().readInt();
				@char = ((Char.myCharz().charID != num256) ? GameScr.findCharInMap(num256) : Char.myCharz());
				if (num255 != -1)
				{
					short num257 = msg.reader().readShort();
					short num258 = msg.reader().readShort();
					short num259 = msg.reader().readShort();
					sbyte isMonkey = msg.reader().readByte();
					if (@char != null)
					{
						if (@char.charID == num256)
						{
							@char.isMask = true;
							@char.isMonkey = isMonkey;
							if (@char.isMonkey != 0)
							{
								@char.isWaitMonkey = false;
								@char.isLockMove = false;
							}
						}
						else if (@char != null)
						{
							@char.isMask = true;
							@char.isMonkey = isMonkey;
						}
						if (num257 != -1)
						{
							@char.head = num257;
						}
						if (num258 != -1)
						{
							@char.body = num258;
						}
						if (num259 != -1)
						{
							@char.leg = num259;
						}
					}
				}
				if (num255 == -1 && @char != null)
				{
					@char.isMask = false;
					@char.isMonkey = 0;
				}
				if (@char != null)
				{
				}
				break;
			}
			case -88:
				GameCanvas.endDlg();
				GameCanvas.serverScreen.switchToMe();
				break;
			case -87:
			{
				msg.reader().mark(100000);
				createData(msg.reader(), isSaveRMS: true);
				msg.reader().reset();
				sbyte[] data3 = new sbyte[msg.reader().available()];
				msg.reader().readFully(ref data3);
				sbyte[] data4 = new sbyte[1] { GameScr.vcData };
				Rms.saveRMS("NRdataVersion", data4);
				LoginScr.isUpdateData = false;
				if (GameScr.vsData == GameScr.vcData && GameScr.vsMap == GameScr.vcMap && GameScr.vsSkill == GameScr.vcSkill && GameScr.vsItem == GameScr.vcItem)
				{
					GameScr.gI().readDart();
					GameScr.gI().readEfect();
					GameScr.gI().readArrow();
					GameScr.gI().readSkill();
					Service.gI().clientOk();
					return;
				}
				break;
			}
			case -86:
			{
				sbyte b42 = msg.reader().readByte();
				if (b42 == 0)
				{
					int playerID = msg.reader().readInt();
					GameScr.gI().giaodich(playerID);
				}
				if (b42 == 1)
				{
					int num139 = msg.reader().readInt();
					Char char7 = GameScr.findCharInMap(num139);
					if (char7 == null)
					{
						return;
					}
					GameCanvas.panel.setTypeGiaoDich(char7);
					GameCanvas.panel.show();
					Service.gI().getPlayerMenu(num139);
				}
				if (b42 == 2)
				{
					sbyte b43 = msg.reader().readByte();
					for (int num140 = 0; num140 < GameCanvas.panel.vMyGD.size(); num140++)
					{
						Item item2 = (Item)GameCanvas.panel.vMyGD.elementAt(num140);
						if (item2.indexUI == b43)
						{
							GameCanvas.panel.vMyGD.removeElement(item2);
							break;
						}
					}
				}
				if (b42 == 6)
				{
					GameCanvas.panel.isFriendLock = true;
					if (GameCanvas.panel2 != null)
					{
						GameCanvas.panel2.isFriendLock = true;
					}
					GameCanvas.panel.vFriendGD.removeAllElements();
					if (GameCanvas.panel2 != null)
					{
						GameCanvas.panel2.vFriendGD.removeAllElements();
					}
					int friendMoneyGD = msg.reader().readInt();
					sbyte b44 = msg.reader().readByte();
					for (int num141 = 0; num141 < b44; num141++)
					{
						Item item3 = new Item();
						item3.template = ItemTemplates.get(msg.reader().readShort());
						item3.quantity = msg.reader().readInt();
						int num142 = msg.reader().readUnsignedByte();
						if (num142 != 0)
						{
							item3.itemOption = new ItemOption[num142];
							for (int num143 = 0; num143 < item3.itemOption.Length; num143++)
							{
								int num144 = msg.reader().readShortOptionTemp();
								int param5 = msg.reader().ReadParamDQT();
								if (num144 != -1)
								{
									item3.itemOption[num143] = new ItemOption(num144, param5);
									item3.compare = GameCanvas.panel.getCompare(item3);
								}
							}
						}
						if (GameCanvas.panel2 != null)
						{
							GameCanvas.panel2.vFriendGD.addElement(item3);
						}
						else
						{
							GameCanvas.panel.vFriendGD.addElement(item3);
						}
					}
					if (GameCanvas.panel2 != null)
					{
						GameCanvas.panel2.setTabGiaoDich(isMe: false);
						GameCanvas.panel2.friendMoneyGD = friendMoneyGD;
					}
					else
					{
						GameCanvas.panel.friendMoneyGD = friendMoneyGD;
						if (GameCanvas.panel.currentTabIndex == 2)
						{
							GameCanvas.panel.setTabGiaoDich(isMe: false);
						}
					}
				}
				if (b42 == 7)
				{
					InfoDlg.hide();
					if (GameCanvas.panel.isShow)
					{
						GameCanvas.panel.hide();
					}
				}
				break;
			}
			case -85:
			{
				sbyte num211 = msg.reader().readByte();
				if (num211 == 0)
				{
					int num212 = msg.reader().readUnsignedShort();
					sbyte[] data5 = new sbyte[num212];
					msg.reader().read(ref data5, 0, num212);
					GameScr.imgCapcha = Image.createImage(data5, 0, num212);
					GameScr.gI().keyInput = "-----";
					GameScr.gI().strCapcha = msg.reader().readUTF();
					GameScr.gI().keyCapcha = new int[GameScr.gI().strCapcha.Length];
					GameScr.gI().mobCapcha = new Mob();
					GameScr.gI().right = null;
				}
				if (num211 == 1)
				{
					MobCapcha.isAttack = true;
				}
				if (num211 == 2)
				{
					MobCapcha.explode = true;
					GameScr.gI().right = GameScr.gI().cmdFocus;
				}
				break;
			}
			case -112:
			{
				sbyte num170 = msg.reader().readByte();
				if (num170 == 0)
				{
					GameScr.findMobInMap(msg.reader().readByte()).clearBody();
				}
				if (num170 == 1)
				{
					GameScr.findMobInMap(msg.reader().readByte()).setBody(msg.reader().readShort());
				}
				break;
			}
			case -84:
			{
				int index2 = msg.reader().readUnsignedByte();
				Mob mob6 = null;
				try
				{
					mob6 = (Mob)GameScr.vMob.elementAt(index2);
				}
				catch (Exception)
				{
				}
				if (mob6 != null)
				{
					mob6.maxHp = msg.reader().readInt();
				}
				break;
			}
			case -83:
			{
				sbyte num189 = msg.reader().readByte();
				if (num189 == 0)
				{
					int num190 = msg.reader().readShort();
					int bgRID = msg.reader().readShort();
					int num191 = msg.reader().readUnsignedByte();
					int num192 = msg.reader().readInt();
					msg.reader().readUTF();
					int num193 = msg.reader().readShort();
					int num194 = msg.reader().readShort();
					if (msg.reader().readByte() == 1)
					{
						GameScr.gI().isRongNamek = true;
					}
					else
					{
						GameScr.gI().isRongNamek = false;
					}
					GameScr.gI().xR = num193;
					GameScr.gI().yR = num194;
					if (Char.myCharz().charID == num192)
					{
						GameCanvas.panel.hideNow();
						GameScr.gI().activeRongThanEff(isMe: true);
					}
					else if (TileMap.mapID == num190 && TileMap.zoneID == num191)
					{
						GameScr.gI().activeRongThanEff(isMe: false);
					}
					else if (mGraphics.zoomLevel > 1)
					{
						GameScr.gI().doiMauTroi();
					}
					GameScr.gI().mapRID = num190;
					GameScr.gI().bgRID = bgRID;
					GameScr.gI().zoneRID = num191;
				}
				if (num189 == 1)
				{
					if (TileMap.mapID == GameScr.gI().mapRID && TileMap.zoneID == GameScr.gI().zoneRID)
					{
						GameScr.gI().hideRongThanEff();
					}
					else
					{
						GameScr.gI().isRongThanXuatHien = false;
						if (GameScr.gI().isRongNamek)
						{
							GameScr.gI().isRongNamek = false;
						}
					}
				}
				if (num189 == 2)
				{
				}
				break;
			}
			case -82:
			{
				sbyte size = msg.reader().readByte();
				TileMap.tileIndex = new int[size][][];
				TileMap.tileType = new int[size][];
				for (int num179 = 0; num179 < size; num179++)
				{
					sbyte b52 = msg.reader().readByte();
					TileMap.tileType[num179] = new int[b52];
					TileMap.tileIndex[num179] = new int[b52][];
					for (int num180 = 0; num180 < b52; num180++)
					{
						TileMap.tileType[num179][num180] = msg.reader().readInt();
						sbyte b53 = msg.reader().readByte();
						TileMap.tileIndex[num179][num180] = new int[b53];
						for (int num181 = 0; num181 < b53; num181++)
						{
							TileMap.tileIndex[num179][num180][num181] = msg.reader().readByte();
						}
					}
				}
				break;
			}
			case -81:
			{
				sbyte b61 = msg.reader().readByte();
				if (b61 == 0)
				{
					string src = msg.reader().readUTF();
					string src2 = msg.reader().readUTF();
					GameCanvas.panel.setTypeCombine();
					GameCanvas.panel.combineInfo = mFont.tahoma_7b_blue.splitFontArray(src, Panel.WIDTH_PANEL);
					GameCanvas.panel.combineTopInfo = mFont.tahoma_7.splitFontArray(src2, Panel.WIDTH_PANEL);
					GameCanvas.panel.show();
				}
				if (b61 == 1)
				{
					GameCanvas.panel.vItemCombine.removeAllElements();
					sbyte b62 = msg.reader().readByte();
					for (int num202 = 0; num202 < b62; num202++)
					{
						sbyte b63 = msg.reader().readByte();
						for (int num203 = 0; num203 < Char.myCharz().arrItemBag.Length; num203++)
						{
							Item item4 = Char.myCharz().arrItemBag[num203];
							if (item4 != null && item4.indexUI == b63)
							{
								item4.isSelect = true;
								GameCanvas.panel.vItemCombine.addElement(item4);
							}
						}
					}
					if (GameCanvas.panel.isShow)
					{
						GameCanvas.panel.setTabCombine();
					}
				}
				if (b61 == 2)
				{
					GameCanvas.panel.combineSuccess = 0;
					GameCanvas.panel.setCombineEff(0);
				}
				if (b61 == 3)
				{
					GameCanvas.panel.combineSuccess = 1;
					GameCanvas.panel.setCombineEff(0);
				}
				if (b61 == 4)
				{
					short iconID = msg.reader().readShort();
					GameCanvas.panel.iconID3 = iconID;
					GameCanvas.panel.combineSuccess = 0;
					GameCanvas.panel.setCombineEff(1);
				}
				if (b61 == 5)
				{
					short iconID2 = msg.reader().readShort();
					GameCanvas.panel.iconID3 = iconID2;
					GameCanvas.panel.combineSuccess = 0;
					GameCanvas.panel.setCombineEff(2);
				}
				if (b61 == 6)
				{
					short iconID3 = msg.reader().readShort();
					short iconID4 = msg.reader().readShort();
					GameCanvas.panel.combineSuccess = 0;
					GameCanvas.panel.setCombineEff(3);
					GameCanvas.panel.iconID1 = iconID3;
					GameCanvas.panel.iconID3 = iconID4;
				}
				if (b61 == 7)
				{
					short iconID5 = msg.reader().readShort();
					GameCanvas.panel.iconID3 = iconID5;
					GameCanvas.panel.combineSuccess = 0;
					GameCanvas.panel.setCombineEff(4);
				}
				if (b61 == 8)
				{
					GameCanvas.panel.iconID3 = -1;
					GameCanvas.panel.combineSuccess = 1;
					GameCanvas.panel.setCombineEff(4);
				}
				short num204 = 21;
				int num205 = 0;
				int num206 = 0;
				try
				{
					num204 = msg.reader().readShort();
					num205 = msg.reader().readShort();
					num206 = msg.reader().readShort();
					GameCanvas.panel.xS = num205 - GameScr.cmx;
					GameCanvas.panel.yS = num206 - GameScr.cmy;
				}
				catch (Exception)
				{
				}
				for (int num207 = 0; num207 < GameScr.vNpc.size(); num207++)
				{
					Npc npc7 = (Npc)GameScr.vNpc.elementAt(num207);
					if (npc7.template.npcTemplateId == num204)
					{
						GameCanvas.panel.xS = npc7.cx - GameScr.cmx;
						GameCanvas.panel.yS = npc7.cy - GameScr.cmy;
						GameCanvas.panel.idNPC = num204;
						break;
					}
				}
				break;
			}
			case -80:
			{
				sbyte b48 = msg.reader().readByte();
				InfoDlg.hide();
				if (b48 == 0)
				{
					GameCanvas.panel.vFriend.removeAllElements();
					int num159 = msg.reader().readUnsignedByte();
					for (int num160 = 0; num160 < num159; num160++)
					{
						Char char9 = new Char();
						char9.charID = msg.reader().readInt();
						char9.head = msg.reader().readShort();
						char9.headICON = msg.reader().readShort();
						char9.body = msg.reader().readShort();
						char9.leg = msg.reader().readShort();
						char9.bag = msg.reader().readUnsignedByte();
						char9.cName = msg.reader().readUTF();
						bool isOnline = msg.reader().readBoolean();
						InfoItem infoItem2 = new InfoItem(mResources.power + ": " + msg.reader().readUTF());
						infoItem2.charInfo = char9;
						infoItem2.isOnline = isOnline;
						GameCanvas.panel.vFriend.addElement(infoItem2);
					}
					GameCanvas.panel.setTypeFriend();
					GameCanvas.panel.show();
				}
				if (b48 == 3)
				{
					MyVector vFriend = GameCanvas.panel.vFriend;
					int num161 = msg.reader().readInt();
					for (int num162 = 0; num162 < vFriend.size(); num162++)
					{
						InfoItem infoItem3 = (InfoItem)vFriend.elementAt(num162);
						if (infoItem3.charInfo != null && infoItem3.charInfo.charID == num161)
						{
							infoItem3.isOnline = msg.reader().readBoolean();
							break;
						}
					}
				}
				if (b48 != 2)
				{
					break;
				}
				MyVector vFriend2 = GameCanvas.panel.vFriend;
				int num163 = msg.reader().readInt();
				for (int num164 = 0; num164 < vFriend2.size(); num164++)
				{
					InfoItem infoItem4 = (InfoItem)vFriend2.elementAt(num164);
					if (infoItem4.charInfo != null && infoItem4.charInfo.charID == num163)
					{
						vFriend2.removeElement(infoItem4);
						break;
					}
				}
				if (GameCanvas.panel.isShow)
				{
					GameCanvas.panel.setTabFriend();
				}
				break;
			}
			case -99:
				InfoDlg.hide();
				if (msg.reader().readByte() == 0)
				{
					GameCanvas.panel.vEnemy.removeAllElements();
					int num95 = msg.reader().readUnsignedByte();
					for (int num96 = 0; num96 < num95; num96++)
					{
						Char char6 = new Char();
						char6.charID = msg.reader().readInt();
						char6.head = msg.reader().readShort();
						char6.headICON = msg.reader().readShort();
						char6.body = msg.reader().readShort();
						char6.leg = msg.reader().readShort();
						char6.bag = msg.reader().readShort();
						char6.cName = msg.reader().readUTF();
						InfoItem infoItem = new InfoItem(msg.reader().readUTF());
						bool flag8 = msg.reader().readBoolean();
						infoItem.charInfo = char6;
						infoItem.isOnline = flag8;
						GameCanvas.panel.vEnemy.addElement(infoItem);
					}
					GameCanvas.panel.setTypeEnemy();
					GameCanvas.panel.show();
				}
				break;
			case -79:
			{
				InfoDlg.hide();
				msg.reader().readInt();
				Char charMenu = GameCanvas.panel.charMenu;
				if (charMenu == null)
				{
					return;
				}
				charMenu.cPower = msg.reader().readLong();
				charMenu.currStrLevel = msg.reader().readUTF();
				break;
			}
			case -93:
			{
				short num51 = msg.reader().readShort();
				BgItem.newSmallVersion = new sbyte[num51];
				for (int m = 0; m < num51; m++)
				{
					BgItem.newSmallVersion[m] = msg.reader().readByte();
				}
				break;
			}
			case -77:
			{
				short num260 = msg.reader().readShort();
				SmallImage.newSmallVersion = new sbyte[num260];
				SmallImage.maxSmall = num260;
				SmallImage.imgNew = new Small[num260];
				for (int num261 = 0; num261 < num260; num261++)
				{
					SmallImage.newSmallVersion[num261] = msg.reader().readByte();
				}
				break;
			}
			case -76:
				switch (msg.reader().readByte())
				{
				case 0:
				{
					sbyte sz = msg.reader().readByte();
					if (sz <= 0)
					{
						return;
					}
					Char.myCharz().arrArchive = new Archivement[sz];
					for (int num234 = 0; num234 < sz; num234++)
					{
						Char.myCharz().arrArchive[num234] = new Archivement
						{
							info1 = num234 + 1 + ". " + msg.reader().readUTF(),
							info2 = msg.reader().readUTF(),
							money = msg.reader().readShort(),
							isFinish = msg.reader().readBoolean(),
							isRecieve = msg.reader().readBoolean()
						};
					}
					GameCanvas.panel.setTypeArchivement();
					GameCanvas.panel.show();
					break;
				}
				case 1:
				{
					int idArchive = msg.reader().readUnsignedByte();
					if (Char.myCharz().arrArchive[idArchive] != null)
					{
						Char.myCharz().arrArchive[idArchive].isRecieve = true;
					}
					break;
				}
				}
				break;
			case -74:
			{
				if (ServerListScreen.stopDownload)
				{
					return;
				}
				if (!GameCanvas.isGetResourceFromServer())
				{
					Service.gI().getResource(3, null);
					SmallImage.loadBigRMS();
					if (Rms.loadRMSString("acc") != null || Rms.loadRMSString("userAo" + ServerListScreen.ipSelect) != null)
					{
						LoginScr.isContinueToLogin = true;
					}
					GameCanvas.loginScr = new LoginScr();
					GameCanvas.loginScr.switchToMe();
					return;
				}
				sbyte b38 = msg.reader().readByte();
				if (b38 == 0)
				{
					int num89 = msg.reader().readInt();
					string text3 = Rms.loadRMSString("ResVersion");
					int num90 = ((text3 == null || !(text3 != string.Empty)) ? (-1) : int.Parse(text3));
					if (Session_ME.gI().isCompareIPConnect())
					{
						if (num90 == -1 || num90 != num89)
						{
							GameCanvas.serverScreen.show2();
						}
						else
						{
							SmallImage.loadBigRMS();
							ServerListScreen.loadScreen = true;
							if (GameCanvas.currentScreen != GameCanvas.loginScr)
							{
								GameCanvas.serverScreen.switchToMe();
							}
						}
					}
					else
					{
						Session_ME.gI().close();
						ServerListScreen.loadScreen = true;
						ServerListScreen.isAutoConect = false;
						ServerListScreen.countDieConnect = 1000;
						GameCanvas.serverScreen.switchToMe();
					}
					ServerListScreen.keyDecryptString = msg.reader().readUTF();
				}
				if (b38 == 1)
				{
					ServerListScreen.strWait = mResources.downloading_data;
					ServerListScreen.nBig = msg.reader().readShort();
					Service.gI().getResource(2, null);
				}
				if (b38 == 2)
				{
					try
					{
						isLoadingData = true;
						GameCanvas.endDlg();
						ServerListScreen.demPercent++;
						ServerListScreen.percent = ServerListScreen.demPercent * 100 / ServerListScreen.nBig;
						string[] array8 = Res.split(msg.reader().readUTF(), "/", 0);
						string filename = "x" + mGraphics.zoomLevel + array8[array8.Length - 1];
						int num91 = msg.reader().readInt();
						sbyte[] data = new sbyte[num91];
						msg.reader().read(ref data, 0, num91);
						Rms.saveRMS(filename, data);
					}
					catch (Exception)
					{
						GameCanvas.startOK(mResources.pls_restart_game_error, 8885, null);
					}
				}
				if (b38 == 3)
				{
					Rms.saveRMSInt("musicSize", ModFunc.musicCount);
					ModFunc.InitMusic();
					isLoadingData = false;
					Rms.saveRMSString("ResVersion", msg.reader().readInt() + string.Empty);
					Service.gI().getResource(3, null);
					GameCanvas.endDlg();
					SmallImage.loadBigRMS();
					mSystem.gcc();
					ServerListScreen.bigOk = true;
					ServerListScreen.loadScreen = true;
					GameScr.gI().loadGameScr();
					if (GameCanvas.currentScreen != GameCanvas.loginScr)
					{
						GameCanvas.serverScreen.switchToMe();
					}
				}
				if (b38 == 4)
				{
					string name = msg.reader().readUTF();
					sbyte[] data2 = null;
					try
					{
						data2 = NinjaUtil.readByteArray(msg);
					}
					catch (Exception)
					{
						data2 = null;
					}
					if (data2 != null)
					{
						ModFunc.musicCount++;
						Rms.saveRMS("music_" + name, data2);
					}
				}
				break;
			}
			case -43:
			{
				sbyte itemAction = msg.reader().readByte();
				sbyte where = msg.reader().readByte();
				sbyte index = msg.reader().readByte();
				string info = msg.reader().readUTF();
				GameCanvas.panel.itemRequest(itemAction, info, where, index);
				break;
			}
			case -59:
			{
				sbyte typePK = msg.reader().readByte();
				GameScr.gI().player_vs_player(msg.reader().readInt(), msg.reader().readInt(), msg.reader().readUTF(), typePK);
				break;
			}
			case -62:
			{
				byte id4 = msg.reader().readUnsignedByte();
				sbyte size5 = msg.reader().readByte();
				int[] idImage = new int[size5];
				if (size5 > 0)
				{
					for (int num235 = 0; num235 < size5; num235++)
					{
						idImage[num235] = msg.reader().readShort();
						if (idImage[num235] > 0)
						{
							SmallImage.vKeys.addElement(idImage[num235] + string.Empty);
						}
					}
				}
				short idNew2;
				try
				{
					idNew2 = msg.reader().readShort();
				}
				catch
				{
					idNew2 = id4;
				}
				if (size5 > 0)
				{
					for (int num236 = 0; num236 < size5; num236++)
					{
						try
						{
							idImage[num236] = msg.reader().readInt();
						}
						catch
						{
						}
						if (idImage[num236] > 0)
						{
							SmallImage.vKeys.addElement(idImage[num236] + string.Empty);
						}
					}
				}
				ClanImage clanImage4 = ClanImage.getClanImage(idNew2);
				if (clanImage4 != null)
				{
					clanImage4.idImage = idImage;
				}
				break;
			}
			case -65:
			{
				InfoDlg.hide();
				int num169 = msg.reader().readInt();
				sbyte b49 = msg.reader().readByte();
				if (b49 == 0)
				{
					break;
				}
				if (Char.myCharz().charID == num169)
				{
					isStopReadMessage = true;
					GameScr.lockTick = 500;
					GameScr.gI().center = null;
					if (b49 == 0 || b49 == 1 || b49 == 3)
					{
						Teleport.addTeleport(new Teleport(Char.myCharz().cx, Char.myCharz().cy, Char.myCharz().head, Char.myCharz().cdir, 0, isMe: true, (b49 != 1) ? b49 : Char.myCharz().cgender));
					}
					if (b49 == 2)
					{
						GameScr.lockTick = 50;
						Char.myCharz().hide();
					}
				}
				else
				{
					Char char10 = GameScr.findCharInMap(num169);
					if ((b49 == 0 || b49 == 1 || b49 == 3) && char10 != null)
					{
						char10.isUsePlane = true;
						Teleport.addTeleport(new Teleport(char10.cx, char10.cy, char10.head, char10.cdir, 0, isMe: false, (b49 != 1) ? b49 : char10.cgender)
						{
							id = num169
						});
					}
					if (b49 == 2)
					{
						char10.hide();
					}
				}
				break;
			}
			case -64:
			{
				int num276 = msg.reader().readInt();
				int num277 = msg.reader().readUnsignedByte();
				@char = null;
				@char = ((num276 != Char.myCharz().charID) ? GameScr.findCharInMap(num276) : Char.myCharz());
				if (@char == null)
				{
					return;
				}
				@char.bag = num277;
				for (int num278 = 0; num278 < 54; num278++)
				{
					@char.removeEffChar(0, 201 + num278);
				}
				if (@char.bag >= 201 && @char.bag < 255)
				{
					Effect effect = new Effect(@char.bag, @char, 2, -1, 10, 1);
					effect.typeEff = 5;
					@char.addEffChar(effect);
				}
				break;
			}
			case -63:
			{
				byte id3 = msg.reader().readUnsignedByte();
				sbyte size4 = msg.reader().readByte();
				int[] idImages = new int[size4];
				if (size4 > 0)
				{
					for (int num214 = 0; num214 < size4; num214++)
					{
						idImages[num214] = msg.reader().readShort();
					}
				}
				short idNew;
				try
				{
					idNew = msg.reader().readShort();
				}
				catch
				{
					idNew = id3;
				}
				if (size4 > 0)
				{
					for (int num215 = 0; num215 < size4; num215++)
					{
						try
						{
							idImages[num215] = msg.reader().readInt();
						}
						catch
						{
						}
					}
				}
				ClanImage clanImage3 = new ClanImage
				{
					ID = idNew,
					idImage = idImages
				};
				if (size4 > 0)
				{
					ClanImage.idImages.put(id3 + string.Empty, clanImage3);
				}
				break;
			}
			case -57:
			{
				string strInvite = msg.reader().readUTF();
				int clanID = msg.reader().readInt();
				int code = msg.reader().readInt();
				GameScr.gI().clanInvite(strInvite, clanID, code);
				break;
			}
			case -51:
				InfoDlg.hide();
				readClanMsg(msg, 0);
				if (GameCanvas.panel.isMessage && GameCanvas.panel.type == 5)
				{
					GameCanvas.panel.initTabClans();
				}
				break;
			case -53:
			{
				InfoDlg.hide();
				bool flag12 = false;
				int num171 = msg.reader().readInt();
				if (num171 == -1)
				{
					flag12 = true;
					Char.myCharz().clan = null;
					ClanMessage.vMessage.removeAllElements();
					if (GameCanvas.panel.member != null)
					{
						GameCanvas.panel.member.removeAllElements();
					}
					if (GameCanvas.panel.myMember != null)
					{
						GameCanvas.panel.myMember.removeAllElements();
					}
					if (GameCanvas.currentScreen == GameScr.gI())
					{
						GameCanvas.panel.setTabClans();
					}
					return;
				}
				GameCanvas.panel.tabIcon = null;
				if (Char.myCharz().clan == null)
				{
					Char.myCharz().clan = new Clan();
				}
				Char.myCharz().clan.ID = num171;
				Char.myCharz().clan.name = msg.reader().readUTF();
				Char.myCharz().clan.slogan = msg.reader().readUTF();
				Char.myCharz().clan.imgID = msg.reader().readUnsignedByte();
				Char.myCharz().clan.powerPoint = msg.reader().readUTF();
				Char.myCharz().clan.leaderName = msg.reader().readUTF();
				Char.myCharz().clan.currMember = msg.reader().readUnsignedByte();
				Char.myCharz().clan.maxMember = msg.reader().readUnsignedByte();
				Char.myCharz().role = msg.reader().readByte();
				Char.myCharz().clan.clanPoint = msg.reader().readInt();
				Char.myCharz().clan.level = msg.reader().readByte();
				GameCanvas.panel.myMember = new MyVector();
				for (int num172 = 0; num172 < Char.myCharz().clan.currMember; num172++)
				{
					Member member7 = new Member();
					member7.ID = msg.reader().readInt();
					member7.head = msg.reader().readShort();
					member7.headICON = msg.reader().readShort();
					member7.leg = msg.reader().readShort();
					member7.body = msg.reader().readShort();
					member7.name = msg.reader().readUTF();
					member7.role = msg.reader().readByte();
					member7.powerPoint = msg.reader().readUTF();
					member7.donate = msg.reader().readInt();
					member7.receive_donate = msg.reader().readInt();
					member7.clanPoint = msg.reader().readInt();
					member7.curClanPoint = msg.reader().readInt();
					member7.joinTime = NinjaUtil.getDate(msg.reader().readInt());
					GameCanvas.panel.myMember.addElement(member7);
				}
				int num173 = msg.reader().readUnsignedByte();
				for (int num174 = 0; num174 < num173; num174++)
				{
					readClanMsg(msg, -1);
				}
				if (GameCanvas.panel.isSearchClan || GameCanvas.panel.isViewMember || GameCanvas.panel.isMessage)
				{
					GameCanvas.panel.setTabClans();
				}
				if (flag12)
				{
					GameCanvas.panel.setTabClans();
				}
				break;
			}
			case -52:
			{
				sbyte num49 = msg.reader().readByte();
				if (num49 == 0)
				{
					Member member3 = new Member
					{
						ID = msg.reader().readInt(),
						head = msg.reader().readShort(),
						headICON = msg.reader().readShort(),
						leg = msg.reader().readShort(),
						body = msg.reader().readShort(),
						name = msg.reader().readUTF(),
						role = msg.reader().readByte(),
						powerPoint = msg.reader().readUTF(),
						donate = msg.reader().readInt(),
						receive_donate = msg.reader().readInt(),
						clanPoint = msg.reader().readInt(),
						joinTime = NinjaUtil.getDate(msg.reader().readInt())
					};
					if (GameCanvas.panel.myMember == null)
					{
						GameCanvas.panel.myMember = new MyVector();
					}
					GameCanvas.panel.myMember.addElement(member3);
					GameCanvas.panel.initTabClans();
				}
				if (num49 == 1)
				{
					GameCanvas.panel.myMember.removeElementAt(msg.reader().readByte());
					GameCanvas.panel.currentListLength--;
					GameCanvas.panel.initTabClans();
				}
				if (num49 != 2)
				{
					break;
				}
				Member member4 = new Member();
				member4.ID = msg.reader().readInt();
				member4.head = msg.reader().readShort();
				member4.headICON = msg.reader().readShort();
				member4.leg = msg.reader().readShort();
				member4.body = msg.reader().readShort();
				member4.name = msg.reader().readUTF();
				member4.role = msg.reader().readByte();
				member4.powerPoint = msg.reader().readUTF();
				member4.donate = msg.reader().readInt();
				member4.receive_donate = msg.reader().readInt();
				member4.clanPoint = msg.reader().readInt();
				member4.joinTime = NinjaUtil.getDate(msg.reader().readInt());
				for (int l = 0; l < GameCanvas.panel.myMember.size(); l++)
				{
					Member member5 = (Member)GameCanvas.panel.myMember.elementAt(l);
					if (member5.ID == member4.ID)
					{
						if (Char.myCharz().charID == member4.ID)
						{
							Char.myCharz().role = member4.role;
						}
						Member o = member4;
						GameCanvas.panel.myMember.removeElement(member5);
						GameCanvas.panel.myMember.insertElementAt(o, l);
						return;
					}
				}
				break;
			}
			case -50:
			{
				InfoDlg.hide();
				GameCanvas.panel.member = new MyVector();
				sbyte b39 = msg.reader().readByte();
				for (int num92 = 0; num92 < b39; num92++)
				{
					Member member6 = new Member();
					member6.ID = msg.reader().readInt();
					member6.head = msg.reader().readShort();
					member6.headICON = msg.reader().readShort();
					member6.leg = msg.reader().readShort();
					member6.body = msg.reader().readShort();
					member6.name = msg.reader().readUTF();
					member6.role = msg.reader().readByte();
					member6.powerPoint = msg.reader().readUTF();
					member6.donate = msg.reader().readInt();
					member6.receive_donate = msg.reader().readInt();
					member6.clanPoint = msg.reader().readInt();
					member6.joinTime = NinjaUtil.getDate(msg.reader().readInt());
					GameCanvas.panel.member.addElement(member6);
				}
				GameCanvas.panel.isViewMember = true;
				GameCanvas.panel.isSearchClan = false;
				GameCanvas.panel.isMessage = false;
				GameCanvas.panel.currentListLength = GameCanvas.panel.member.size() + 2;
				GameCanvas.panel.initTabClans();
				break;
			}
			case -47:
			{
				InfoDlg.hide();
				sbyte b71 = msg.reader().readByte();
				if (b71 == 0)
				{
					GameCanvas.panel.clanReport = mResources.cannot_find_clan;
					GameCanvas.panel.clans = null;
				}
				else
				{
					GameCanvas.panel.clans = new Clan[b71];
					for (int num233 = 0; num233 < GameCanvas.panel.clans.Length; num233++)
					{
						GameCanvas.panel.clans[num233] = new Clan();
						GameCanvas.panel.clans[num233].ID = msg.reader().readInt();
						GameCanvas.panel.clans[num233].name = msg.reader().readUTF();
						GameCanvas.panel.clans[num233].slogan = msg.reader().readUTF();
						GameCanvas.panel.clans[num233].imgID = msg.reader().readUnsignedByte();
						GameCanvas.panel.clans[num233].powerPoint = msg.reader().readUTF();
						GameCanvas.panel.clans[num233].leaderName = msg.reader().readUTF();
						GameCanvas.panel.clans[num233].currMember = msg.reader().readUnsignedByte();
						GameCanvas.panel.clans[num233].maxMember = msg.reader().readUnsignedByte();
						GameCanvas.panel.clans[num233].date = msg.reader().readInt();
					}
				}
				GameCanvas.panel.isSearchClan = true;
				GameCanvas.panel.isViewMember = false;
				GameCanvas.panel.isMessage = false;
				if (GameCanvas.panel.isSearchClan)
				{
					GameCanvas.panel.initTabClans();
				}
				break;
			}
			case -46:
			{
				InfoDlg.hide();
				sbyte type2 = msg.reader().readByte();
				if (type2 == 1 || type2 == 3)
				{
					GameCanvas.endDlg();
					ClanImage.vClanImage.removeAllElements();
					int size3 = msg.reader().readUnsignedByte();
					for (int num197 = 0; num197 < size3; num197++)
					{
						byte id2 = msg.reader().readUnsignedByte();
						string name4 = msg.reader().readUTF();
						int xu = msg.reader().readInt();
						int luong = msg.reader().readInt();
						ClanImage clanImage2 = new ClanImage
						{
							ID = id2,
							name = name4,
							xu = xu,
							luong = luong
						};
						if (!ClanImage.isExistClanImage(clanImage2.ID))
						{
							ClanImage.addClanImage(clanImage2);
							continue;
						}
						ClanImage.getClanImage((short)clanImage2.ID).name = clanImage2.name;
						ClanImage.getClanImage((short)clanImage2.ID).xu = clanImage2.xu;
						ClanImage.getClanImage((short)clanImage2.ID).luong = clanImage2.luong;
					}
					if (Char.myCharz().clan != null)
					{
						GameCanvas.panel.changeIcon();
					}
				}
				if (type2 == 4)
				{
					Char.myCharz().clan.imgID = msg.reader().readUnsignedByte();
					Char.myCharz().clan.slogan = msg.reader().readUTF();
				}
				break;
			}
			case -61:
			{
				int num187 = msg.reader().readInt();
				if (num187 != Char.myCharz().charID)
				{
					if (GameScr.findCharInMap(num187) != null)
					{
						GameScr.findCharInMap(num187).clanID = msg.reader().readInt();
						if (GameScr.findCharInMap(num187).clanID == -2)
						{
							GameScr.findCharInMap(num187).isCopy = true;
						}
					}
				}
				else if (Char.myCharz().clan != null)
				{
					Char.myCharz().clan.ID = msg.reader().readInt();
				}
				break;
			}
			case -42:
				Char.myCharz().cHPGoc = msg.reader().readIntToLongDQT();
				Char.myCharz().cMPGoc = msg.reader().readIntToLongDQT();
				Char.myCharz().cDamGoc = msg.reader().readIntToLongDQT();
				Char.myCharz().cHPFull = msg.reader().readIntToLongDQT();
				Char.myCharz().cMPFull = msg.reader().readIntToLongDQT();
				Char.myCharz().cHP = msg.reader().readIntToLongDQT();
				Char.myCharz().cMP = msg.reader().readIntToLongDQT();
				Char.myCharz().cspeed = msg.reader().readByte();
				Char.myCharz().hpFrom1000TiemNang = msg.reader().readByte();
				Char.myCharz().mpFrom1000TiemNang = msg.reader().readByte();
				Char.myCharz().damFrom1000TiemNang = msg.reader().readByte();
				Char.myCharz().cDamFull = msg.reader().readIntToLongDQT();
				Char.myCharz().cDefull = msg.reader().readIntToLongDQT();
				Char.myCharz().cCriticalFull = msg.reader().readByte();
				Char.myCharz().cTiemNang = msg.reader().readLong();
				Char.myCharz().expForOneAdd = msg.reader().readShort();
				Char.myCharz().cDefGoc = msg.reader().DefShotToLong();
				Char.myCharz().cCriticalGoc = msg.reader().readByte();
				InfoDlg.hide();
				try
				{
					Char.myCharz().tlDef = msg.readInt3Byte();
					Char.myCharz().tlPst = msg.readInt3Byte();
					Char.myCharz().tlNeDon = msg.readInt3Byte();
					Char.myCharz().tlHutHp = msg.readInt3Byte();
					Char.myCharz().tlHutMp = msg.readInt3Byte();
					Char.myCharz().tileGiamTDHS = msg.readInt3Byte();
					Char.myCharz().timeGiamTDHS = msg.readInt3Byte();
					Char.myCharz().khangTDHS = msg.reader().readBool();
					Char.myCharz().isKhongLanh = msg.reader().readBool();
					Char.myCharz().wearingVoHinh = msg.reader().readBool();
					Char.myCharz().teleport = msg.reader().readBool();
				}
				catch
				{
					Char.myCharz().tlDef = 0;
					Char.myCharz().tlPst = 0;
					Char.myCharz().tlNeDon = 0;
					Char.myCharz().tlHutHp = 0;
					Char.myCharz().tlHutMp = 0;
					Char.myCharz().tileGiamTDHS = 0;
					Char.myCharz().timeGiamTDHS = 0;
					Char.myCharz().khangTDHS = false;
					Char.myCharz().isKhongLanh = false;
					Char.myCharz().wearingVoHinh = false;
					Char.myCharz().teleport = false;
				}
				InfoDlg.hide();
				break;
			case 1:
			{
				bool flag13 = msg.reader().readBool();
				Res.outz("isRes= " + flag13);
				if (!flag13)
				{
					GameCanvas.startOKDlg(msg.reader().readUTF());
					break;
				}
				GameCanvas.loginScr.isLogin2 = false;
				Rms.saveRMSString("userAo" + ServerListScreen.ipSelect, string.Empty);
				GameCanvas.endDlg();
				GameCanvas.loginScr.doLogin();
				break;
			}
			case 2:
				Char.isLoadingMap = false;
				LoginScr.isLoggingIn = false;
				if (!GameScr.isLoadAllData)
				{
					GameScr.gI().initSelectChar();
				}
				BgItem.clearHashTable();
				GameCanvas.endDlg();
				CreateCharScr.isCreateChar = true;
				CreateCharScr.gI().switchToMe();
				break;
			case -107:
			{
				sbyte num146 = msg.reader().readByte();
				if (num146 == 0)
				{
					Char.myCharz().havePet = false;
				}
				if (num146 == 1)
				{
					Char.myCharz().havePet = true;
				}
				if (num146 != 2)
				{
					break;
				}
				InfoDlg.hide();
				Char.myPetz().head = msg.reader().readShort();
				Char.myPetz().setDefaultPart();
				int num147 = msg.reader().readUnsignedByte();
				Char.myPetz().arrItemBody = new Item[num147];
				for (int num148 = 0; num148 < num147; num148++)
				{
					short num149 = msg.reader().readShort();
					if (num149 == -1)
					{
						continue;
					}
					Char.myPetz().arrItemBody[num148] = new Item
					{
						template = ItemTemplates.get(num149)
					};
					int num150 = Char.myPetz().arrItemBody[num148].template.type;
					Char.myPetz().arrItemBody[num148].quantity = msg.reader().readInt();
					Char.myPetz().arrItemBody[num148].info = msg.reader().readUTF();
					Char.myPetz().arrItemBody[num148].content = msg.reader().readUTF();
					int num151 = msg.reader().readUnsignedByte();
					if (num151 != 0)
					{
						Char.myPetz().arrItemBody[num148].itemOption = new ItemOption[num151];
						for (int num152 = 0; num152 < Char.myPetz().arrItemBody[num148].itemOption.Length; num152++)
						{
							int num153 = msg.reader().readShortOptionTemp();
							int param6 = msg.reader().ReadParamDQT();
							if (num153 != -1)
							{
								Char.myPetz().arrItemBody[num148].itemOption[num152] = new ItemOption(num153, param6);
							}
						}
					}
					switch (num150)
					{
					case 0:
						Char.myPetz().body = Char.myPetz().arrItemBody[num148].template.part;
						break;
					case 1:
						Char.myPetz().leg = Char.myPetz().arrItemBody[num148].template.part;
						break;
					}
				}
				Char.myPetz().cHP = msg.reader().readIntToLongDQT();
				Char.myPetz().cHPFull = msg.reader().readIntToLongDQT();
				Char.myPetz().cMP = msg.reader().readIntToLongDQT();
				Char.myPetz().cMPFull = msg.reader().readIntToLongDQT();
				Char.myPetz().cDamFull = msg.reader().readIntToLongDQT();
				Char.myPetz().cName = msg.reader().readUTF();
				Char.myPetz().currStrLevel = msg.reader().readUTF();
				Char.myPetz().cPower = msg.reader().readLong();
				Char.myPetz().cTiemNang = msg.reader().readLong();
				Char.myPetz().petStatus = msg.reader().readByte();
				Char.myPetz().cStamina = msg.reader().readShort();
				Char.myPetz().cMaxStamina = msg.reader().readShort();
				Char.myPetz().cCriticalFull = msg.reader().readByte();
				Char.myPetz().cDefull = msg.reader().DefShotToLong();
				Char.myPetz().arrPetSkill = new Skill[msg.reader().readByte()];
				for (int num154 = 0; num154 < Char.myPetz().arrPetSkill.Length; num154++)
				{
					short num155 = msg.reader().readShort();
					if (num155 != -1)
					{
						Char.myPetz().arrPetSkill[num154] = Skills.get(num155);
						continue;
					}
					Char.myPetz().arrPetSkill[num154] = new Skill();
					Char.myPetz().arrPetSkill[num154].template = null;
					Char.myPetz().arrPetSkill[num154].moreInfo = msg.reader().readUTF();
				}
				break;
			}
			case 3:
			{
				sbyte num20 = msg.reader().readByte();
				if (num20 == 0)
				{
					Char.myCharz().havePet2 = false;
				}
				if (num20 == 1)
				{
					Char.myCharz().havePet2 = true;
				}
				if (num20 != 2)
				{
					break;
				}
				InfoDlg.hide();
				Char.MyPet2z().head = msg.reader().readShort();
				Char.MyPet2z().setDefaultPart();
				int arrBodySz = msg.reader().readUnsignedByte();
				Char.MyPet2z().arrItemBody = new Item[arrBodySz];
				for (int i = 0; i < arrBodySz; i++)
				{
					short tempId = msg.reader().readShort();
					if (tempId == -1)
					{
						continue;
					}
					Char.MyPet2z().arrItemBody[i] = new Item
					{
						template = ItemTemplates.get(tempId)
					};
					int num43 = Char.MyPet2z().arrItemBody[i].template.type;
					Char.MyPet2z().arrItemBody[i].quantity = msg.reader().readInt();
					Char.MyPet2z().arrItemBody[i].info = msg.reader().readUTF();
					Char.MyPet2z().arrItemBody[i].content = msg.reader().readUTF();
					int num44 = msg.reader().readUnsignedByte();
					if (num44 != 0)
					{
						Char.MyPet2z().arrItemBody[i].itemOption = new ItemOption[num44];
						for (int j = 0; j < Char.MyPet2z().arrItemBody[i].itemOption.Length; j++)
						{
							int num46 = msg.reader().readShortOptionTemp();
							int param3 = msg.reader().ReadParamDQT();
							if (num46 != -1)
							{
								Char.MyPet2z().arrItemBody[i].itemOption[j] = new ItemOption(num46, param3);
							}
						}
					}
					switch (num43)
					{
					case 0:
						Char.MyPet2z().body = Char.MyPet2z().arrItemBody[i].template.part;
						break;
					case 1:
						Char.MyPet2z().leg = Char.MyPet2z().arrItemBody[i].template.part;
						break;
					}
				}
				Char.MyPet2z().cHP = msg.reader().readIntToLongDQT();
				Char.MyPet2z().cHPFull = msg.reader().readIntToLongDQT();
				Char.MyPet2z().cMP = msg.reader().readIntToLongDQT();
				Char.MyPet2z().cMPFull = msg.reader().readIntToLongDQT();
				Char.MyPet2z().cDamFull = msg.reader().readIntToLongDQT();
				Char.MyPet2z().cName = msg.reader().readUTF();
				Char.MyPet2z().currStrLevel = msg.reader().readUTF();
				Char.MyPet2z().cPower = msg.reader().readLong();
				Char.MyPet2z().cTiemNang = msg.reader().readLong();
				Char.MyPet2z().petStatus = msg.reader().readByte();
				Char.MyPet2z().cStamina = msg.reader().readShort();
				Char.MyPet2z().cMaxStamina = msg.reader().readShort();
				Char.MyPet2z().cCriticalFull = msg.reader().readByte();
				Char.MyPet2z().cDefull = msg.reader().readIntToLongDQT();
				Char.MyPet2z().arrPetSkill = new Skill[msg.reader().readByte()];
				for (int k = 0; k < Char.MyPet2z().arrPetSkill.Length; k++)
				{
					short num48 = msg.reader().readShort();
					if (num48 != -1)
					{
						Char.MyPet2z().arrPetSkill[k] = Skills.get(num48);
						continue;
					}
					Char.MyPet2z().arrPetSkill[k] = new Skill();
					Char.MyPet2z().arrPetSkill[k].template = null;
					Char.MyPet2z().arrPetSkill[k].moreInfo = msg.reader().readUTF();
				}
				if (GameCanvas.w > 2 * Panel.WIDTH_PANEL)
				{
					GameCanvas.panel2 = new Panel();
					GameCanvas.panel2.tabName[7] = new string[1][] { new string[1] { string.Empty } };
					GameCanvas.panel2.setTypeBodyOnly();
					GameCanvas.panel2.show();
					GameCanvas.panel.setTypePet2Main();
					GameCanvas.panel.show();
				}
				else
				{
					GameCanvas.panel.tabName[21] = mResources.petMainTab;
					GameCanvas.panel.setTypePet2Main();
					GameCanvas.panel.show();
				}
				break;
			}
			case -109:
				Char.myPetz().cHPGoc = msg.reader().readIntToLongDQT();
				Char.myPetz().cMPGoc = msg.reader().readIntToLongDQT();
				Char.myPetz().cDamGoc = msg.reader().readIntToLongDQT();
				Char.myPetz().cDefGoc = msg.reader().DefShotToLong();
				Char.myPetz().cCriticalGoc = msg.reader().readByteToInt();
				break;
			case -37:
			{
				if (msg.reader().readByte() != 0)
				{
					break;
				}
				Char.myCharz().head = msg.reader().readShort();
				Char.myCharz().setDefaultPart();
				int num265 = msg.reader().readUnsignedByte();
				Res.outz("num body = " + num265);
				Char.myCharz().arrItemBody = new Item[num265];
				for (int num266 = 0; num266 < num265; num266++)
				{
					short num267 = msg.reader().readShort();
					if (num267 == -1)
					{
						continue;
					}
					Char.myCharz().arrItemBody[num266] = new Item();
					Char.myCharz().arrItemBody[num266].template = ItemTemplates.get(num267);
					int num268 = Char.myCharz().arrItemBody[num266].template.type;
					Char.myCharz().arrItemBody[num266].quantity = msg.reader().readInt();
					Char.myCharz().arrItemBody[num266].info = msg.reader().readUTF();
					Char.myCharz().arrItemBody[num266].content = msg.reader().readUTF();
					int num269 = msg.reader().readUnsignedByte();
					if (num269 != 0)
					{
						Char.myCharz().arrItemBody[num266].itemOption = new ItemOption[num269];
						for (int num270 = 0; num270 < Char.myCharz().arrItemBody[num266].itemOption.Length; num270++)
						{
							int num271 = msg.reader().readShortOptionTemp();
							int param10 = msg.reader().ReadParamDQT();
							if (num271 != -1)
							{
								Char.myCharz().arrItemBody[num266].itemOption[num270] = new ItemOption(num271, param10);
							}
						}
					}
					switch (num268)
					{
					case 0:
						Char.myCharz().body = Char.myCharz().arrItemBody[num266].template.part;
						break;
					case 1:
						Char.myCharz().leg = Char.myCharz().arrItemBody[num266].template.part;
						break;
					}
				}
				break;
			}
			case -36:
			{
				sbyte b73 = msg.reader().readByte();
				Res.outz("cAction= " + b73);
				if (b73 == 0)
				{
					int num237 = msg.reader().readUnsignedByte();
					Char.myCharz().arrItemBag = new Item[num237];
					GameScr.hpPotion = 0;
					Res.outz("numC=" + num237);
					for (int num238 = 0; num238 < num237; num238++)
					{
						short num239 = msg.reader().readShort();
						if (num239 == -1)
						{
							continue;
						}
						Char.myCharz().arrItemBag[num238] = new Item();
						Char.myCharz().arrItemBag[num238].template = ItemTemplates.get(num239);
						Char.myCharz().arrItemBag[num238].quantity = msg.reader().readInt();
						Char.myCharz().arrItemBag[num238].info = msg.reader().readUTF();
						Char.myCharz().arrItemBag[num238].content = msg.reader().readUTF();
						Char.myCharz().arrItemBag[num238].indexUI = num238;
						int num240 = msg.reader().readUnsignedByte();
						if (num240 != 0)
						{
							Char.myCharz().arrItemBag[num238].itemOption = new ItemOption[num240];
							for (int num241 = 0; num241 < Char.myCharz().arrItemBag[num238].itemOption.Length; num241++)
							{
								int num242 = msg.reader().readShortOptionTemp();
								int param9 = msg.reader().ReadParamDQT();
								if (num242 != -1)
								{
									Char.myCharz().arrItemBag[num238].itemOption[num241] = new ItemOption(num242, param9);
								}
							}
							Char.myCharz().arrItemBag[num238].compare = GameCanvas.panel.getCompare(Char.myCharz().arrItemBag[num238]);
						}
						_ = Char.myCharz().arrItemBag[num238].template.type;
						_ = 11;
						if (Char.myCharz().arrItemBag[num238].template.type == 6)
						{
							GameScr.hpPotion += Char.myCharz().arrItemBag[num238].quantity;
						}
					}
				}
				if (b73 == 2)
				{
					sbyte b74 = msg.reader().readByte();
					int quantity4 = msg.reader().readInt();
					int quantity5 = Char.myCharz().arrItemBag[b74].quantity;
					Char.myCharz().arrItemBag[b74].quantity = quantity4;
					if (Char.myCharz().arrItemBag[b74].quantity < quantity5 && Char.myCharz().arrItemBag[b74].template.type == 6)
					{
						GameScr.hpPotion -= quantity5 - Char.myCharz().arrItemBag[b74].quantity;
					}
					if (Char.myCharz().arrItemBag[b74].quantity == 0)
					{
						Char.myCharz().arrItemBag[b74] = null;
					}
				}
				break;
			}
			case -35:
			{
				sbyte b68 = msg.reader().readByte();
				Res.outz("cAction= " + b68);
				if (b68 == 0)
				{
					int num221 = msg.reader().readUnsignedByte();
					Char.myCharz().arrItemBox = new Item[num221];
					GameCanvas.panel.hasUse = 0;
					for (int num222 = 0; num222 < num221; num222++)
					{
						short num223 = msg.reader().readShort();
						if (num223 == -1)
						{
							continue;
						}
						Char.myCharz().arrItemBox[num222] = new Item();
						Char.myCharz().arrItemBox[num222].template = ItemTemplates.get(num223);
						Char.myCharz().arrItemBox[num222].quantity = msg.reader().readInt();
						Char.myCharz().arrItemBox[num222].info = msg.reader().readUTF();
						Char.myCharz().arrItemBox[num222].content = msg.reader().readUTF();
						int num224 = msg.reader().readUnsignedByte();
						if (num224 != 0)
						{
							Char.myCharz().arrItemBox[num222].itemOption = new ItemOption[num224];
							for (int num225 = 0; num225 < Char.myCharz().arrItemBox[num222].itemOption.Length; num225++)
							{
								int num226 = msg.reader().readShortOptionTemp();
								int param8 = msg.reader().ReadParamDQT();
								if (num226 != -1)
								{
									Char.myCharz().arrItemBox[num222].itemOption[num225] = new ItemOption(num226, param8);
								}
							}
						}
						GameCanvas.panel.hasUse++;
					}
				}
				if (b68 == 1)
				{
					bool isBoxClan = false;
					try
					{
						if (msg.reader().readByte() == 1)
						{
							isBoxClan = true;
						}
					}
					catch (Exception)
					{
					}
					GameCanvas.panel.setTypeBox();
					GameCanvas.panel.isBoxClan = isBoxClan;
					GameCanvas.panel.show();
				}
				if (b68 == 2)
				{
					sbyte b69 = msg.reader().readByte();
					int quantity3 = msg.reader().readInt();
					Char.myCharz().arrItemBox[b69].quantity = quantity3;
					if (Char.myCharz().arrItemBox[b69].quantity == 0)
					{
						Char.myCharz().arrItemBox[b69] = null;
					}
				}
				break;
			}
			case -45:
			{
				sbyte type3 = msg.reader().readByte();
				int playerId = msg.reader().readInt();
				short skillId = msg.reader().readShort();
				if (type3 == 20)
				{
					sbyte b55 = msg.reader().readByte();
					sbyte dir = msg.reader().readByte();
					short timeGong = msg.reader().readShort();
					bool isFly = msg.reader().readByte() != 0;
					sbyte typePaint = msg.reader().readByte();
					sbyte typeItem = -1;
					try
					{
						typeItem = msg.reader().readByte();
					}
					catch (Exception)
					{
					}
					sbyte level = -1;
					try
					{
						level = msg.reader().readByte();
					}
					catch (Exception)
					{
					}
					@char = ((Char.myCharz().charID != playerId) ? GameScr.findCharInMap(playerId) : Char.myCharz());
					@char.SetSkillPaint_NEW(skillId, isFly, b55, typePaint, dir, timeGong, typeItem, level);
				}
				if (type3 == 21)
				{
					Point point = new Point
					{
						x = msg.reader().readShort(),
						y = msg.reader().readShort()
					};
					short timeDame = msg.reader().readShort();
					short rangeDame = msg.reader().readShort();
					sbyte typePaint2 = 0;
					sbyte typeItem2 = -1;
					Point[] targets = null;
					@char = ((Char.myCharz().charID != playerId) ? GameScr.findCharInMap(playerId) : Char.myCharz());
					try
					{
						typePaint2 = msg.reader().readByte();
						targets = new Point[msg.reader().readByte()];
						for (int num198 = 0; num198 < targets.Length; num198++)
						{
							targets[num198] = new Point
							{
								type = msg.reader().readByte()
							};
							if (targets[num198].type == 0)
							{
								targets[num198].id = msg.reader().readByte();
							}
							else
							{
								targets[num198].id = msg.reader().readInt();
							}
						}
					}
					catch (Exception)
					{
					}
					try
					{
						typeItem2 = msg.reader().readByte();
					}
					catch (Exception)
					{
					}
					sbyte level2 = -1;
					try
					{
						level2 = msg.reader().readByte();
					}
					catch (Exception)
					{
					}
					@char.SetSkillPaint_STT(1, skillId, point, timeDame, rangeDame, typePaint2, targets, typeItem2, level2);
				}
				if (type3 == 0)
				{
					Res.outz("id use= " + playerId);
					if (Char.myCharz().charID != playerId)
					{
						@char = GameScr.findCharInMap(playerId);
						if ((TileMap.tileTypeAtPixel(@char.cx, @char.cy) & 2) == 2)
						{
							@char.setSkillPaint(GameScr.sks[skillId], 0);
						}
						else
						{
							@char.setSkillPaint(GameScr.sks[skillId], 1);
							@char.delayFall = 20;
						}
					}
					else
					{
						Char.myCharz().saveLoadPreviousSkill();
						Res.outz("LOAD LAST SKILL");
					}
					sbyte b56 = msg.reader().readByte();
					Res.outz("npc size= " + b56);
					for (int num199 = 0; num199 < b56; num199++)
					{
						sbyte b57 = msg.reader().readByte();
						sbyte b58 = msg.reader().readByte();
						Res.outz("index= " + b57);
						if (skillId >= 42 && skillId <= 48)
						{
							((Mob)GameScr.vMob.elementAt(b57)).isFreez = true;
							((Mob)GameScr.vMob.elementAt(b57)).seconds = b58;
							((Mob)GameScr.vMob.elementAt(b57)).last = (((Mob)GameScr.vMob.elementAt(b57)).cur = mSystem.currentTimeMillis());
						}
					}
					sbyte b59 = msg.reader().readByte();
					for (int num200 = 0; num200 < b59; num200++)
					{
						int num201 = msg.reader().readInt();
						sbyte b60 = msg.reader().readByte();
						Res.outz("player ID= " + num201 + " my ID= " + Char.myCharz().charID);
						if (skillId < 42 || skillId > 48)
						{
							continue;
						}
						if (num201 == Char.myCharz().charID)
						{
							if (!Char.myCharz().isFlyAndCharge && !Char.myCharz().isStandAndCharge)
							{
								GameScr.gI().isFreez = true;
								Char.myCharz().isFreez = true;
								Char.myCharz().freezSeconds = b60;
								Char.myCharz().lastFreez = (Char.myCharz().currFreez = mSystem.currentTimeMillis());
								Char.myCharz().isLockMove = true;
							}
						}
						else
						{
							@char = GameScr.findCharInMap(num201);
							if (@char != null && !@char.isFlyAndCharge && !@char.isStandAndCharge)
							{
								@char.isFreez = true;
								@char.seconds = b60;
								@char.freezSeconds = b60;
								@char.lastFreez = (GameScr.findCharInMap(num201).currFreez = mSystem.currentTimeMillis());
							}
						}
					}
				}
				if (type3 == 1 && playerId != Char.myCharz().charID)
				{
					GameScr.findCharInMap(playerId).isCharge = true;
				}
				if (type3 == 3)
				{
					if (playerId == Char.myCharz().charID)
					{
						Char.myCharz().isCharge = false;
						SoundMn.gI().taitaoPause();
						Char.myCharz().saveLoadPreviousSkill();
					}
					else
					{
						GameScr.findCharInMap(playerId).isCharge = false;
					}
				}
				if (type3 == 4)
				{
					if (playerId == Char.myCharz().charID)
					{
						Char.myCharz().seconds = msg.reader().readShort() - 1000;
						Char.myCharz().last = mSystem.currentTimeMillis();
						Res.outz("second= " + Char.myCharz().seconds + " last= " + Char.myCharz().last);
					}
					else if (GameScr.findCharInMap(playerId) != null)
					{
						switch (GameScr.findCharInMap(playerId).cgender)
						{
						case 0:
							GameScr.findCharInMap(playerId).useChargeSkill(isGround: false);
							break;
						case 1:
							GameScr.findCharInMap(playerId).useChargeSkill(isGround: true);
							break;
						}
						GameScr.findCharInMap(playerId).skillTemplateId = skillId;
						GameScr.findCharInMap(playerId).isUseSkillAfterCharge = true;
						GameScr.findCharInMap(playerId).seconds = msg.reader().readShort();
						GameScr.findCharInMap(playerId).last = mSystem.currentTimeMillis();
					}
				}
				if (type3 == 5)
				{
					if (playerId == Char.myCharz().charID)
					{
						Char.myCharz().stopUseChargeSkill();
					}
					else if (GameScr.findCharInMap(playerId) != null)
					{
						GameScr.findCharInMap(playerId).stopUseChargeSkill();
					}
				}
				if (type3 == 6)
				{
					if (playerId == Char.myCharz().charID)
					{
						Char.myCharz().setAutoSkillPaint(GameScr.sks[skillId], 0);
					}
					else if (GameScr.findCharInMap(playerId) != null)
					{
						GameScr.findCharInMap(playerId).setAutoSkillPaint(GameScr.sks[skillId], 0);
						SoundMn.gI().gong();
					}
				}
				if (type3 == 7)
				{
					if (playerId == Char.myCharz().charID)
					{
						Char.myCharz().seconds = msg.reader().readShort();
						Res.outz("second = " + Char.myCharz().seconds);
						Char.myCharz().last = mSystem.currentTimeMillis();
					}
					else if (GameScr.findCharInMap(playerId) != null)
					{
						GameScr.findCharInMap(playerId).useChargeSkill(isGround: true);
						GameScr.findCharInMap(playerId).seconds = msg.reader().readShort();
						GameScr.findCharInMap(playerId).last = mSystem.currentTimeMillis();
						SoundMn.gI().gong();
					}
				}
				if (type3 == 8 && playerId != Char.myCharz().charID && GameScr.findCharInMap(playerId) != null)
				{
					GameScr.findCharInMap(playerId).setAutoSkillPaint(GameScr.sks[skillId], 0);
				}
				break;
			}
			case -44:
			{
				bool flag11 = false;
				if (GameCanvas.w > 2 * Panel.WIDTH_PANEL)
				{
					flag11 = true;
				}
				sbyte type_shop = msg.reader().readByte();
				int tabSz = msg.reader().readUnsignedByte();
				Char.myCharz().arrItemShop = new Item[tabSz][];
				GameCanvas.panel.shopTabName = new string[tabSz + ((!flag11) ? 1 : 0)][];
				for (int num165 = 0; num165 < GameCanvas.panel.shopTabName.Length; num165++)
				{
					GameCanvas.panel.shopTabName[num165] = new string[2];
				}
				if (type_shop == 2)
				{
					GameCanvas.panel.maxPageShop = new int[tabSz];
					GameCanvas.panel.currPageShop = new int[tabSz];
				}
				if (!flag11)
				{
					GameCanvas.panel.shopTabName[tabSz] = mResources.inventory;
				}
				for (int num166 = 0; num166 < tabSz; num166++)
				{
					string[] name2 = Res.split(msg.reader().readUTF(), "\n", 0);
					if (type_shop == 2)
					{
						GameCanvas.panel.maxPageShop[num166] = msg.reader().readUnsignedByte();
					}
					if (name2.Length == 2)
					{
						GameCanvas.panel.shopTabName[num166] = name2;
					}
					if (name2.Length == 1)
					{
						GameCanvas.panel.shopTabName[num166][0] = name2[0];
						GameCanvas.panel.shopTabName[num166][1] = string.Empty;
					}
					int itemSz = msg.reader().readUnsignedByte();
					Char.myCharz().arrItemShop[num166] = new Item[itemSz];
					Panel.strWantToBuy = mResources.say_wat_do_u_want_to_buy;
					if (type_shop == 1)
					{
						Panel.strWantToBuy = mResources.say_wat_do_u_want_to_buy2;
					}
					for (int num167 = 0; num167 < itemSz; num167++)
					{
						short itemId = msg.reader().readShort();
						if (itemId == -1)
						{
							continue;
						}
						Char.myCharz().arrItemShop[num166][num167] = new Item();
						Char.myCharz().arrItemShop[num166][num167].template = ItemTemplates.get(itemId);
						Res.outz("name " + num166 + " = " + Char.myCharz().arrItemShop[num166][num167].template.name + " id templat= " + Char.myCharz().arrItemShop[num166][num167].template.id);
						switch (type_shop)
						{
						case 8:
							Char.myCharz().arrItemShop[num166][num167].buyCoin = msg.reader().readInt();
							Char.myCharz().arrItemShop[num166][num167].buyGold = msg.reader().readInt();
							Char.myCharz().arrItemShop[num166][num167].quantity = msg.reader().readInt();
							break;
						case 4:
							Char.myCharz().arrItemShop[num166][num167].reason = msg.reader().readUTF();
							break;
						case 0:
							Char.myCharz().arrItemShop[num166][num167].buyCoin = msg.reader().readInt();
							Char.myCharz().arrItemShop[num166][num167].buyGold = msg.reader().readInt();
							break;
						case 1:
							Char.myCharz().arrItemShop[num166][num167].powerRequire = msg.reader().readLong();
							break;
						case 2:
							Char.myCharz().arrItemShop[num166][num167].itemId = msg.reader().readShort();
							Char.myCharz().arrItemShop[num166][num167].buyCoin = msg.reader().readInt();
							Char.myCharz().arrItemShop[num166][num167].buyGold = msg.reader().readInt();
							Char.myCharz().arrItemShop[num166][num167].buyType = msg.reader().readByte();
							Char.myCharz().arrItemShop[num166][num167].quantity = msg.reader().readInt();
							Char.myCharz().arrItemShop[num166][num167].isMe = msg.reader().readByte();
							break;
						case 3:
							Char.myCharz().arrItemShop[num166][num167].isBuySpec = true;
							Char.myCharz().arrItemShop[num166][num167].iconSpec = msg.reader().readShort();
							Char.myCharz().arrItemShop[num166][num167].buySpec = msg.reader().readInt();
							break;
						}
						int optSz = msg.reader().readUnsignedByte();
						if (optSz != 0)
						{
							Char.myCharz().arrItemShop[num166][num167].itemOption = new ItemOption[optSz];
							for (int num168 = 0; num168 < Char.myCharz().arrItemShop[num166][num167].itemOption.Length; num168++)
							{
								int optId = msg.reader().readShortOptionTemp();
								int param7 = msg.reader().ReadParamDQT();
								if (optId != -1)
								{
									Char.myCharz().arrItemShop[num166][num167].itemOption[num168] = new ItemOption(optId, param7);
									Char.myCharz().arrItemShop[num166][num167].compare = GameCanvas.panel.getCompare(Char.myCharz().arrItemShop[num166][num167]);
								}
							}
						}
						sbyte isNew = msg.reader().readByte();
						Char.myCharz().arrItemShop[num166][num167].newItem = isNew != 0;
						if (msg.reader().readByte() == 1)
						{
							int headTemp = msg.reader().readShort();
							int bodyTemp = msg.reader().readShort();
							int legTemp = msg.reader().readShort();
							int bagTemp = msg.reader().readShort();
							Char.myCharz().arrItemShop[num166][num167].setPartTemp(headTemp, bodyTemp, legTemp, bagTemp);
						}
					}
				}
				if (flag11)
				{
					if (type_shop != 2)
					{
						GameCanvas.panel2 = new Panel();
						GameCanvas.panel2.tabName[7] = new string[1][] { new string[1] { string.Empty } };
						GameCanvas.panel2.setTypeBodyOnly();
						GameCanvas.panel2.show();
					}
					else
					{
						GameCanvas.panel2 = new Panel();
						GameCanvas.panel2.setTypeKiGuiOnly();
						GameCanvas.panel2.show();
					}
				}
				GameCanvas.panel.tabName[1] = GameCanvas.panel.shopTabName;
				if (type_shop == 2)
				{
					string[][] array11 = GameCanvas.panel.tabName[1];
					if (flag11)
					{
						GameCanvas.panel.tabName[1] = new string[4][]
						{
							array11[0],
							array11[1],
							array11[2],
							array11[3]
						};
					}
					else
					{
						GameCanvas.panel.tabName[1] = new string[5][]
						{
							array11[0],
							array11[1],
							array11[2],
							array11[3],
							array11[4]
						};
					}
				}
				GameCanvas.panel.setTypeShop(type_shop);
				GameCanvas.panel.show();
				break;
			}
			case -41:
			{
				sbyte b45 = msg.reader().readByte();
				Char.myCharz().strLevel = new string[b45];
				for (int num145 = 0; num145 < b45; num145++)
				{
					string text4 = msg.reader().readUTF();
					Char.myCharz().strLevel[num145] = text4;
				}
				Res.outz("---   xong  level caption cmd : " + msg.command);
				break;
			}
			case -34:
			{
				sbyte b12 = msg.reader().readByte();
				Res.outz("act= " + b12);
				if (b12 == 0 && GameScr.gI().magicTree != null)
				{
					Res.outz("toi duoc day");
					MagicTree magicTree = GameScr.gI().magicTree;
					magicTree.id = msg.reader().readShort();
					magicTree.name = msg.reader().readUTF();
					magicTree.name = Res.changeString(magicTree.name);
					magicTree.x = msg.reader().readShort();
					magicTree.y = msg.reader().readShort();
					magicTree.level = msg.reader().readByte();
					magicTree.currPeas = msg.reader().readShort();
					magicTree.maxPeas = msg.reader().readShort();
					Res.outz("curr Peas= " + magicTree.currPeas);
					magicTree.strInfo = msg.reader().readUTF();
					magicTree.seconds = msg.reader().readInt();
					magicTree.timeToRecieve = magicTree.seconds;
					sbyte b13 = msg.reader().readByte();
					magicTree.peaPostionX = new int[b13];
					magicTree.peaPostionY = new int[b13];
					for (int n = 0; n < b13; n++)
					{
						magicTree.peaPostionX[n] = msg.reader().readByte();
						magicTree.peaPostionY[n] = msg.reader().readByte();
					}
					magicTree.isUpdate = msg.reader().readBool();
					magicTree.last = (magicTree.cur = mSystem.currentTimeMillis());
					GameScr.gI().magicTree.isUpdateTree = true;
				}
				if (b12 == 1)
				{
					myVector = new MyVector();
					try
					{
						while (msg.reader().available() > 0)
						{
							string caption = msg.reader().readUTF();
							myVector.addElement(new Command(caption, GameCanvas.instance, 888392, null));
						}
					}
					catch (Exception ex2)
					{
						Cout.println("Loi MAGIC_TREE " + ex2.ToString());
					}
					GameCanvas.menu.startAt(myVector, 3);
				}
				if (b12 == 2)
				{
					GameScr.gI().magicTree.remainPeas = msg.reader().readShort();
					GameScr.gI().magicTree.seconds = msg.reader().readInt();
					GameScr.gI().magicTree.last = (GameScr.gI().magicTree.cur = mSystem.currentTimeMillis());
					GameScr.gI().magicTree.isUpdateTree = true;
					GameScr.gI().magicTree.isPeasEffect = true;
				}
				break;
			}
			case 11:
			{
				GameCanvas.debug("SA9", 2);
				int num273 = msg.reader().readByte();
				sbyte b76 = msg.reader().readByte();
				if (b76 != 0)
				{
					Mob.arrMobTemplate[num273].data.readDataNewBoss(NinjaUtil.readByteArray(msg), b76);
				}
				else
				{
					Mob.arrMobTemplate[num273].data.readData(NinjaUtil.readByteArray(msg));
				}
				for (int num274 = 0; num274 < GameScr.vMob.size(); num274++)
				{
					mob = (Mob)GameScr.vMob.elementAt(num274);
					if (mob.templateId == num273)
					{
						mob.w = Mob.arrMobTemplate[num273].data.width;
						mob.h = Mob.arrMobTemplate[num273].data.height;
					}
				}
				sbyte[] array19 = NinjaUtil.readByteArray(msg);
				Image img2 = Image.createImage(array19, 0, array19.Length);
				Mob.arrMobTemplate[num273].data.img = img2;
				int num275 = msg.reader().readByte();
				Mob.arrMobTemplate[num273].data.typeData = num275;
				if (num275 == 1 || num275 == 2)
				{
					readFrameBoss(msg, num273);
				}
				break;
			}
			case -69:
				Char.myCharz().cMaxStamina = msg.reader().readShort();
				break;
			case -68:
				Char.myCharz().cStamina = msg.reader().readShort();
				break;
			case -67:
			{
				int iconId = msg.reader().readInt();
				string key = "";
				if (ModFunc.isEncryptIcon)
				{
					key = msg.reader().readUTF();
				}
				sbyte[] data7 = null;
				try
				{
					data7 = NinjaUtil.readByteArray(msg);
					Image img = ((!ModFunc.isEncryptIcon) ? createImage(data7) : createImage(data7, key));
					SmallImage.imgNew[iconId].img = img;
					if (mGraphics.zoomLevel > 1)
					{
						SmallImage.imageRaw.Add(iconId, img);
					}
				}
				catch (Exception)
				{
					SmallImage.imgNew[iconId].img = Image.createRGBImage(new int[1], 1, 1, bl: true);
				}
				break;
			}
			case -66:
			{
				short id5 = msg.reader().readShort();
				sbyte[] data6 = NinjaUtil.readByteArray(msg);
				EffectData effDataById = Effect.getEffDataById(id5);
				sbyte b72 = msg.reader().readSByte();
				if (b72 == 0)
				{
					effDataById.readData(data6);
				}
				else
				{
					effDataById.readDataNewBoss(data6, b72);
				}
				sbyte[] array17 = NinjaUtil.readByteArray(msg);
				effDataById.img = Image.createImage(array17, 0, array17.Length);
				break;
			}
			case -32:
			{
				short num228 = msg.reader().readShort();
				int num229 = msg.reader().readInt();
				sbyte[] array13 = null;
				Image image = null;
				try
				{
					array13 = new sbyte[num229];
					for (int num230 = 0; num230 < num229; num230++)
					{
						array13[num230] = msg.reader().readByte();
					}
					image = Image.createImage(array13, 0, num229);
					BgItem.imgNew.put(num228 + string.Empty, image);
				}
				catch (Exception)
				{
					array13 = null;
					BgItem.imgNew.put(num228 + string.Empty, Image.createRGBImage(new int[1], 1, 1, bl: true));
				}
				if (array13 != null)
				{
					if (mGraphics.zoomLevel > 1)
					{
						Rms.saveRMS(mGraphics.zoomLevel + "bgItem" + num228, array13);
					}
					BgItemMn.blendcurrBg(num228, image);
				}
				break;
			}
			case 92:
			{
				if (GameCanvas.currentScreen == GameScr.instance)
				{
					GameCanvas.endDlg();
				}
				string text5 = msg.reader().readUTF();
				string str2 = msg.reader().readUTF();
				str2 = Res.changeString(str2);
				string empty = string.Empty;
				Char char11 = null;
				sbyte b54 = 0;
				if (!text5.Equals(string.Empty))
				{
					char11 = new Char();
					char11.charID = msg.reader().readInt();
					char11.head = msg.reader().readShort();
					char11.headICON = msg.reader().readShort();
					char11.body = msg.reader().readShort();
					char11.bag = msg.reader().readShort();
					char11.leg = msg.reader().readShort();
					b54 = msg.reader().readByte();
					char11.cName = text5;
					try
					{
						char11.isTichXanh = msg.reader().readByte() == 1;
					}
					catch (Exception)
					{
						char11.isTichXanh = false;
					}
				}
				empty += str2;
				InfoDlg.hide();
				if (text5.Equals(string.Empty))
				{
					GameScr.info1.addInfo(empty, 0);
					break;
				}
				GameScr.info2.addInfoWithChar(empty, char11, b54 == 0);
				if (GameCanvas.panel.isShow && GameCanvas.panel.type == 8)
				{
					GameCanvas.panel.initLogMessage();
				}
				break;
			}
			case -26:
			{
				ServerListScreen.testConnect = 2;
				string msgDlg = msg.reader().readUTF();
				if (msgDlg == "Vui lòng mở giới hạn sức mạnh" || msgDlg == "")
				{
					ModFunc.indexAutoPoint = -1;
					ModFunc.pointIncrease = 0;
					ModFunc.autoPointForPet = false;
					GameScr.info1.addInfo("Chỉ số đã đạt tối đa", 0);
				}
				GameCanvas.startOKDlg(msgDlg);
				InfoDlg.hide();
				LoginScr.isContinueToLogin = false;
				Char.isLoadingMap = false;
				if (GameCanvas.currentScreen == GameCanvas.loginScr)
				{
					GameCanvas.serverScreen.switchToMe();
				}
				if (ModFunc.autoLogin != null)
				{
					ModFunc.autoLogin.waitToNextLogin = false;
				}
				break;
			}
			case -25:
				GameScr.info1.addInfo(msg.reader().readUTF(), 0);
				break;
			case 94:
				GameScr.info1.addInfo(msg.reader().readUTF(), 0);
				break;
			case 47:
				GameScr.gI().resetButton();
				break;
			case 81:
				((Mob)GameScr.vMob.elementAt(msg.reader().readUnsignedByte())).isDisable = msg.reader().readBool();
				break;
			case 82:
				((Mob)GameScr.vMob.elementAt(msg.reader().readUnsignedByte())).isDontMove = msg.reader().readBool();
				break;
			case 85:
				((Mob)GameScr.vMob.elementAt(msg.reader().readUnsignedByte())).isFire = msg.reader().readBool();
				break;
			case 86:
			{
				Mob mob5 = (Mob)GameScr.vMob.elementAt(msg.reader().readUnsignedByte());
				mob5.isIce = msg.reader().readBool();
				if (!mob5.isIce)
				{
					ServerEffect.addServerEffect(77, mob5.x, mob5.y - 9, 1);
				}
				break;
			}
			case 87:
				((Mob)GameScr.vMob.elementAt(msg.reader().readUnsignedByte())).isWind = msg.reader().readBool();
				break;
			case 56:
			{
				@char = null;
				int charID = msg.reader().readInt();
				if (charID == Char.myCharz().charID)
				{
					bool flag9 = false;
					@char = Char.myCharz();
					@char.cHP = msg.reader().readIntToLongDQT();
					double dameHit = msg.reader().readIntToLongDQT();
					if (dameHit != 0.0)
					{
						@char.doInjure();
					}
					try
					{
						flag9 = msg.reader().readBoolean();
						sbyte effId = msg.reader().readByte();
						if (effId != -1)
						{
							EffecMn.addEff(new Effect(effId, @char.cx, @char.cy, 3, 1, -1));
						}
					}
					catch (Exception)
					{
					}
					if (Char.myCharz().cTypePk != 4)
					{
						if (dameHit == 0.0)
						{
							GameScr.startFlyText(mResources.miss, @char.cx, @char.cy - @char.ch, 0, -3, mFont.MISS_ME);
						}
						else
						{
							GameScr.startFlyText("-" + dameHit, @char.cx, @char.cy - @char.ch, 0, -3, flag9 ? mFont.FATAL : mFont.RED);
						}
					}
					break;
				}
				@char = GameScr.findCharInMap(charID);
				if (@char == null)
				{
					return;
				}
				@char.cHP = msg.reader().readIntToLongDQT();
				bool flag10 = false;
				double dameHit2 = msg.reader().readIntToLongDQT();
				if (dameHit2 != 0.0)
				{
					@char.doInjure();
				}
				int num97 = 0;
				try
				{
					flag10 = msg.reader().readBoolean();
					sbyte effId2 = msg.reader().readByte();
					if (effId2 != -1)
					{
						EffecMn.addEff(new Effect(effId2, @char.cx, @char.cy, 3, 1, -1));
					}
				}
				catch (Exception)
				{
				}
				dameHit2 += (double)num97;
				if (@char.cTypePk != 4)
				{
					if (dameHit2 == 0.0)
					{
						GameScr.startFlyText(mResources.miss, @char.cx, @char.cy - @char.ch, 0, -3, mFont.MISS);
					}
					else
					{
						GameScr.startFlyText("-" + dameHit2, @char.cx, @char.cy - @char.ch, 0, -3, flag10 ? mFont.FATAL : mFont.ORANGE);
					}
				}
				break;
			}
			case 83:
			{
				int num50 = msg.reader().readInt();
				@char = ((num50 != Char.myCharz().charID) ? GameScr.findCharInMap(num50) : Char.myCharz());
				if (@char == null)
				{
					return;
				}
				Mob mobToAttack = (Mob)GameScr.vMob.elementAt(msg.reader().readUnsignedByte());
				if (@char.mobMe != null)
				{
					@char.mobMe.attackOtherMob(mobToAttack);
				}
				break;
			}
			case 84:
			{
				int num18 = msg.reader().readInt();
				if (num18 == Char.myCharz().charID)
				{
					@char = Char.myCharz();
				}
				else
				{
					@char = GameScr.findCharInMap(num18);
					if (@char == null)
					{
						return;
					}
				}
				@char.cHP = @char.cHPFull;
				@char.cMP = @char.cMPFull;
				@char.cx = msg.reader().readShort();
				@char.cy = msg.reader().readShort();
				@char.liveFromDead();
				break;
			}
			case 46:
				GameCanvas.debug("SA5", 2);
				Cout.LogWarning("Controler RESET_POINT  " + Char.ischangingMap);
				Char.isLockKey = false;
				Char.myCharz().setResetPoint(msg.reader().readShort(), msg.reader().readShort());
				break;
			case -29:
				messageNotLogin(msg);
				break;
			case -28:
				messageNotMap(msg);
				break;
			case -30:
				messageSubCommand(msg);
				break;
			case 62:
				@char = GameScr.findCharInMap(msg.reader().readInt());
				if (@char != null)
				{
					@char.killCharId = Char.myCharz().charID;
					Char.myCharz().npcFocus = null;
					Char.myCharz().mobFocus = null;
					Char.myCharz().itemFocus = null;
					Char.myCharz().charFocus = @char;
					Char.isManualFocus = true;
					GameScr.info1.addInfo(@char.cName + mResources.CUU_SAT, 0);
				}
				break;
			case 63:
				Char.myCharz().killCharId = msg.reader().readInt();
				Char.myCharz().npcFocus = null;
				Char.myCharz().mobFocus = null;
				Char.myCharz().itemFocus = null;
				Char.myCharz().charFocus = GameScr.findCharInMap(Char.myCharz().killCharId);
				Char.isManualFocus = true;
				break;
			case 64:
				GameCanvas.debug("SZ5", 2);
				@char = Char.myCharz();
				try
				{
					@char = GameScr.findCharInMap(msg.reader().readInt());
				}
				catch (Exception ex29)
				{
					Cout.println("Loi CLEAR_CUU_SAT " + ex29.ToString());
				}
				@char.killCharId = -9999;
				break;
			case 39:
				GameCanvas.debug("SA49", 2);
				GameScr.gI().typeTradeOrder = 2;
				if (GameScr.gI().typeTrade >= 2 && GameScr.gI().typeTradeOrder >= 2)
				{
					InfoDlg.showWait();
				}
				break;
			case 57:
			{
				GameCanvas.debug("SZ6", 2);
				MyVector myVector2 = new MyVector();
				myVector2.addElement(new Command(msg.reader().readUTF(), GameCanvas.instance, 88817, null));
				GameCanvas.menu.startAt(myVector2, 3);
				break;
			}
			case 58:
			{
				int charId = msg.reader().readInt();
				Char obj6 = ((charId != Char.myCharz().charID) ? GameScr.findCharInMap(charId) : Char.myCharz());
				obj6.moveFast = new short[3];
				obj6.moveFast[0] = 0;
				short x2 = msg.reader().readShort();
				short y2 = msg.reader().readShort();
				obj6.moveFast[1] = x2;
				obj6.moveFast[2] = y2;
				try
				{
					charId = msg.reader().readInt();
					Char obj7 = ((charId != Char.myCharz().charID) ? GameScr.findCharInMap(charId) : Char.myCharz());
					obj7.cx = x2;
					obj7.cy = y2;
				}
				catch (Exception ex28)
				{
					Cout.println("Loi MOVE_FAST " + ex28.ToString());
				}
				break;
			}
			case 88:
			{
				string info4 = msg.reader().readUTF();
				short num272 = msg.reader().readShort();
				GameCanvas.inputDlg.show(info4, new Command(mResources.ACCEPT, GameCanvas.instance, 88818, num272), TField.INPUT_TYPE_ANY);
				break;
			}
			case 27:
			{
				myVector = new MyVector();
				msg.reader().readUTF();
				int num262 = msg.reader().readByte();
				for (int num263 = 0; num263 < num262; num263++)
				{
					string caption4 = msg.reader().readUTF();
					short num264 = msg.reader().readShort();
					myVector.addElement(new Command(caption4, GameCanvas.instance, 88819, num264));
				}
				GameCanvas.menu.startWithoutCloseButton(myVector, 3);
				break;
			}
			case 33:
			{
				InfoDlg.hide();
				GameCanvas.clearKeyHold();
				GameCanvas.clearKeyPressed();
				myVector = new MyVector();
				try
				{
					while (true)
					{
						string caption3 = msg.reader().readUTF();
						myVector.addElement(new Command(caption3, GameCanvas.instance, 88822, null));
					}
				}
				catch (Exception ex26)
				{
					Cout.println("Loi OPEN_UI_MENU " + ex26.ToString());
				}
				if (Char.myCharz().npcFocus == null)
				{
					return;
				}
				for (int num243 = 0; num243 < Char.myCharz().npcFocus.template.menu.Length; num243++)
				{
					string[] array18 = Char.myCharz().npcFocus.template.menu[num243];
					myVector.addElement(new Command(array18[0], GameCanvas.instance, 88820, array18));
				}
				GameCanvas.menu.startAt(myVector, 3);
				break;
			}
			case 40:
			{
				GameCanvas.debug("SA52", 2);
				GameCanvas.taskTick = 150;
				short taskId = msg.reader().readShort();
				sbyte index3 = msg.reader().readByte();
				string str3 = msg.reader().readUTF();
				str3 = Res.changeString(str3);
				string str4 = msg.reader().readUTF();
				str4 = Res.changeString(str4);
				string[] array14 = new string[msg.reader().readByte()];
				string[] array15 = new string[array14.Length];
				GameScr.tasks = new int[array14.Length];
				GameScr.mapTasks = new int[array14.Length];
				short[] array16 = new short[array14.Length];
				short count = -1;
				for (int num231 = 0; num231 < array14.Length; num231++)
				{
					string str5 = msg.reader().readUTF();
					str5 = Res.changeString(str5);
					GameScr.tasks[num231] = msg.reader().readByte();
					GameScr.mapTasks[num231] = msg.reader().readShort();
					string str6 = msg.reader().readUTF();
					str6 = Res.changeString(str6);
					array16[num231] = -1;
					if (!str5.Equals(string.Empty))
					{
						array14[num231] = str5;
						array15[num231] = str6;
					}
				}
				try
				{
					count = msg.reader().readShort();
					for (int num232 = 0; num232 < array14.Length; num232++)
					{
						array16[num232] = msg.reader().readShort();
					}
				}
				catch (Exception ex25)
				{
					Cout.println("Loi TASK_GET " + ex25.ToString());
				}
				Char.myCharz().taskMaint = new Task(taskId, index3, str3, str4, array14, array16, count, array15);
				if (Char.myCharz().npcFocus != null)
				{
					Npc.clearEffTask();
				}
				Char.taskAction(isNextStep: false);
				break;
			}
			case 41:
				GameCanvas.debug("SA53", 2);
				GameCanvas.taskTick = 100;
				Res.outz("TASK NEXT");
				Char.myCharz().taskMaint.index++;
				Char.myCharz().taskMaint.count = 0;
				Npc.clearEffTask();
				Char.taskAction(isNextStep: true);
				break;
			case 50:
			{
				sbyte b70 = msg.reader().readByte();
				Panel.vGameInfo.removeAllElements();
				for (int num227 = 0; num227 < b70; num227++)
				{
					GameInfo gameInfo = new GameInfo();
					gameInfo.id = msg.reader().readShort();
					gameInfo.main = msg.reader().readUTF();
					gameInfo.content = msg.reader().readUTF();
					Panel.vGameInfo.addElement(gameInfo);
					bool hasRead = Rms.loadRMSInt(gameInfo.id + string.Empty) != -1;
					gameInfo.hasRead = hasRead;
				}
				break;
			}
			case 43:
				GameCanvas.taskTick = 50;
				GameCanvas.debug("SA55", 2);
				Char.myCharz().taskMaint.count = msg.reader().readShort();
				if (Char.myCharz().npcFocus != null)
				{
					Npc.clearEffTask();
				}
				try
				{
					short num218 = msg.reader().readShort();
					short num219 = msg.reader().readShort();
					Char.myCharz().x_hint = num218;
					Char.myCharz().y_hint = num219;
					Res.outz("CMD   TASK_UPDATE:43_mapID =    x|y " + num218 + "|" + num219);
					for (int num220 = 0; num220 < TileMap.vGo.size(); num220++)
					{
						Res.outz("===> " + TileMap.vGo.elementAt(num220));
					}
				}
				catch (Exception)
				{
				}
				break;
			case 90:
				GameCanvas.debug("SA577", 2);
				requestItemPlayer(msg);
				break;
			case 29:
				GameCanvas.debug("SA58", 2);
				GameScr.gI().openUIZone(msg);
				break;
			case -21:
			{
				GameCanvas.debug("SA60", 2);
				short itemMapID4 = msg.reader().readShort();
				for (int num216 = 0; num216 < GameScr.vItemMap.size(); num216++)
				{
					if (((ItemMap)GameScr.vItemMap.elementAt(num216)).itemMapID == itemMapID4)
					{
						GameScr.vItemMap.removeElementAt(num216);
						break;
					}
				}
				break;
			}
			case -20:
			{
				GameCanvas.debug("SA61", 2);
				Char.myCharz().itemFocus = null;
				short itemMapID3 = msg.reader().readShort();
				for (int num213 = 0; num213 < GameScr.vItemMap.size(); num213++)
				{
					ItemMap itemMap2 = (ItemMap)GameScr.vItemMap.elementAt(num213);
					if (itemMap2.itemMapID != itemMapID3)
					{
						continue;
					}
					itemMap2.setPoint(Char.myCharz().cx, Char.myCharz().cy - 10);
					string text6 = msg.reader().readUTF();
					num = 0;
					try
					{
						num = msg.reader().readShort();
						if (itemMap2.template.type == 9)
						{
							num = msg.reader().readShort();
							Char.myCharz().xu += num;
							Char.myCharz().xuStr = mSystem.numberTostring(Char.myCharz().xu);
						}
						else if (itemMap2.template.type == 10)
						{
							num = msg.reader().readShort();
							Char.myCharz().luong += num;
							Char.myCharz().luongStr = mSystem.numberTostring(Char.myCharz().luong);
						}
						else if (itemMap2.template.type == 34)
						{
							num = msg.reader().readShort();
							Char.myCharz().luongKhoa += num;
							Char.myCharz().luongKhoaStr = mSystem.numberTostring(Char.myCharz().luongKhoa);
						}
					}
					catch (Exception)
					{
					}
					if (text6.Equals(string.Empty))
					{
						if (itemMap2.template.type == 9)
						{
							GameScr.startFlyText(((num >= 0) ? "+" : string.Empty) + num, Char.myCharz().cx, Char.myCharz().cy - Char.myCharz().ch, 0, -2, mFont.YELLOW);
							SoundMn.gI().getItem();
						}
						else if (itemMap2.template.type == 10)
						{
							GameScr.startFlyText(((num >= 0) ? "+" : string.Empty) + num, Char.myCharz().cx, Char.myCharz().cy - Char.myCharz().ch, 0, -2, mFont.GREEN);
							SoundMn.gI().getItem();
						}
						else if (itemMap2.template.type == 34)
						{
							GameScr.startFlyText(((num >= 0) ? "+" : string.Empty) + num, Char.myCharz().cx, Char.myCharz().cy - Char.myCharz().ch, 0, -2, mFont.RED);
							SoundMn.gI().getItem();
						}
						else
						{
							GameScr.info1.addInfo(mResources.you_receive + " " + ((num <= 0) ? string.Empty : (num + " ")) + itemMap2.template.name, 0);
							SoundMn.gI().getItem();
						}
						if (num > 0 && Char.myCharz().petFollow != null && Char.myCharz().petFollow.smallID == 4683)
						{
							ServerEffect.addServerEffect(55, Char.myCharz().petFollow.cmx, Char.myCharz().petFollow.cmy, 1);
							ServerEffect.addServerEffect(55, Char.myCharz().cx, Char.myCharz().cy, 1);
						}
					}
					else if (text6.Length == 1)
					{
						Cout.LogError3("strInf.Length =1:  " + text6);
					}
					else
					{
						GameScr.info1.addInfo(text6, 0);
					}
					break;
				}
				break;
			}
			case -19:
			{
				short itemMapID2 = msg.reader().readShort();
				@char = GameScr.findCharInMap(msg.reader().readInt());
				for (int num210 = 0; num210 < GameScr.vItemMap.size(); num210++)
				{
					ItemMap itemMap = (ItemMap)GameScr.vItemMap.elementAt(num210);
					if (itemMap.itemMapID != itemMapID2)
					{
						continue;
					}
					if (@char == null)
					{
						return;
					}
					itemMap.setPoint(@char.cx, @char.cy - 10);
					if (itemMap.x < @char.cx)
					{
						@char.cdir = -1;
					}
					else if (itemMap.x > @char.cx)
					{
						@char.cdir = 1;
					}
					break;
				}
				break;
			}
			case -18:
			{
				GameCanvas.debug("SA63", 2);
				int num209 = msg.reader().readByte();
				GameScr.vItemMap.addElement(new ItemMap(msg.reader().readShort(), Char.myCharz().arrItemBag[num209].template.id, Char.myCharz().cx, Char.myCharz().cy, msg.reader().readShort(), msg.reader().readShort()));
				Char.myCharz().arrItemBag[num209] = null;
				break;
			}
			case 68:
			{
				short itemMapID = msg.reader().readShort();
				short itemTemplateID = msg.reader().readShort();
				int x = msg.reader().readShort();
				int y = msg.reader().readShort();
				int num208 = msg.reader().readInt();
				short r = 0;
				if (num208 == -2)
				{
					r = msg.reader().readShort();
				}
				ItemMap o2 = new ItemMap(num208, itemMapID, itemTemplateID, x, y, r);
				GameScr.vItemMap.addElement(o2);
				break;
			}
			case 69:
				SoundMn.IsDelAcc = ((msg.reader().readByte() != 0) ? true : false);
				break;
			case -14:
				@char = GameScr.findCharInMap(msg.reader().readInt());
				if (@char == null)
				{
					return;
				}
				GameScr.vItemMap.addElement(new ItemMap(msg.reader().readShort(), msg.reader().readShort(), @char.cx, @char.cy, msg.reader().readShort(), msg.reader().readShort()));
				break;
			case -22:
				Char.isLockKey = true;
				Char.ischangingMap = true;
				GameScr.gI().timeStartMap = 0;
				GameScr.gI().timeLengthMap = 0;
				Char.myCharz().mobFocus = null;
				Char.myCharz().npcFocus = null;
				Char.myCharz().charFocus = null;
				Char.myCharz().itemFocus = null;
				Char.myCharz().focus.removeAllElements();
				Char.myCharz().testCharId = -9999;
				Char.myCharz().killCharId = -9999;
				GameCanvas.resetBg();
				GameScr.gI().resetButton();
				GameScr.gI().center = null;
				break;
			case -70:
			{
				GameCanvas.endDlg();
				if (PickMob.tanSat)
				{
					ModFunc.GI().perform(44, true);
					return;
				}
				int avatar2 = msg.reader().readShort();
				string chat3 = msg.reader().readUTF();
				Npc npc6 = new Npc(-1, 0, 0, 0, 0, 0)
				{
					avatar = avatar2
				};
				ChatPopup.addBigMessage(chat3, 100000, npc6);
				sbyte num188 = msg.reader().readByte();
				if (num188 == 0)
				{
					ChatPopup.serverChatPopUp.cmdMsg1 = new Command(mResources.CLOSE, ChatPopup.serverChatPopUp, 1001, null)
					{
						x = GameCanvas.w / 2 - 35,
						y = GameCanvas.h - 35
					};
				}
				if (num188 == 1)
				{
					string p2 = msg.reader().readUTF();
					string caption2 = msg.reader().readUTF();
					ChatPopup.serverChatPopUp.cmdMsg1 = new Command(mResources.CLOSE, ChatPopup.serverChatPopUp, 1001, null)
					{
						x = GameCanvas.w / 2 + 11,
						y = GameCanvas.h - 35
					};
					ChatPopup.serverChatPopUp.cmdMsg2 = new Command(caption2, ChatPopup.serverChatPopUp, 1000, p2)
					{
						x = GameCanvas.w / 2 - 75,
						y = GameCanvas.h - 35
					};
				}
				break;
			}
			case 38:
			{
				InfoDlg.hide();
				int num185 = msg.reader().readShort();
				string str = msg.reader().readUTF();
				str = Res.changeString(str);
				for (int num186 = 0; num186 < GameScr.vNpc.size(); num186++)
				{
					Npc npc4 = (Npc)GameScr.vNpc.elementAt(num186);
					if (npc4.template.npcTemplateId == num185)
					{
						ChatPopup.addChatPopupMultiLine(str, 100000, npc4);
						GameCanvas.panel.hideNow();
						return;
					}
				}
				Npc npc5 = new Npc(num185, 0, 0, 0, num185, GameScr.info1.charId[Char.myCharz().cgender][2]);
				if (npc5.template.npcTemplateId == 5)
				{
					npc5.charID = 5;
				}
				try
				{
					npc5.avatar = msg.reader().readShort();
				}
				catch (Exception)
				{
				}
				ChatPopup.addChatPopupMultiLine(str, 100000, npc5);
				GameCanvas.panel.hideNow();
				break;
			}
			case 32:
			{
				int npcId = msg.reader().readShort();
				for (int num182 = 0; num182 < GameScr.vNpc.size(); num182++)
				{
					Npc npc = (Npc)GameScr.vNpc.elementAt(num182);
					if (npc.template.npcTemplateId == npcId && npc.Equals(Char.myCharz().npcFocus))
					{
						string chat = msg.reader().readUTF();
						string[] menu = new string[msg.reader().readByte()];
						for (int num183 = 0; num183 < menu.Length; num183++)
						{
							menu[num183] = msg.reader().readUTF();
						}
						GameScr.gI().createMenu(menu, npc);
						ChatPopup.addChatPopup(chat, 100000, npc);
						if (npcId == 21 && chat.Contains("tối đa"))
						{
							ModFunc.GI().maxPhale = ModFunc.GI().currPhale;
						}
						return;
					}
				}
				Npc npc2 = new Npc(npcId, 0, -100, 100, npcId, GameScr.info1.charId[Char.myCharz().cgender][2]);
				string chat2 = msg.reader().readUTF();
				string[] menu2 = new string[msg.reader().readByte()];
				for (int num184 = 0; num184 < menu2.Length; num184++)
				{
					menu2[num184] = msg.reader().readUTF();
				}
				try
				{
					short avatar = msg.reader().readShort();
					npc2.avatar = avatar;
				}
				catch (Exception)
				{
				}
				GameScr.gI().createMenu(menu2, npc2);
				ChatPopup.addChatPopup(chat2, 100000, npc2);
				break;
			}
			case 7:
			{
				sbyte type = msg.reader().readByte();
				short id = msg.reader().readShort();
				string info3 = msg.reader().readUTF();
				GameCanvas.panel.saleRequest(type, info3, id);
				break;
			}
			case 6:
				Char.myCharz().xu = msg.reader().readLong();
				Char.myCharz().luong = msg.reader().readInt();
				Char.myCharz().luongKhoa = msg.reader().readInt();
				Char.myCharz().xuStr = mSystem.numberTostring(Char.myCharz().xu);
				Char.myCharz().luongStr = mSystem.numberTostring(Char.myCharz().luong);
				Char.myCharz().luongKhoaStr = mSystem.numberTostring(Char.myCharz().luongKhoa);
				GameCanvas.endDlg();
				break;
			case -23:
				LoadAuraNpcs(msg);
				break;
			case -24:
				if (GameCanvas.currentScreen is GameScr)
				{
					GameCanvas.timeBreakLoading = mSystem.currentTimeMillis() + 3000;
				}
				else
				{
					GameCanvas.timeBreakLoading = mSystem.currentTimeMillis() + 30000;
				}
				Char.isLoadingMap = true;
				GameScr.gI().magicTree = null;
				GameCanvas.isLoading = true;
				GameScr.resetAllvector();
				GameCanvas.endDlg();
				TileMap.vGo.removeAllElements();
				PopUp.vPopups.removeAllElements();
				mSystem.gcc();
				TileMap.mapID = msg.reader().readUnsignedByte();
				TileMap.planetID = msg.reader().readByte();
				TileMap.tileID = msg.reader().readByte();
				TileMap.bgID = msg.reader().readByte();
				TileMap.typeMap = msg.reader().readByte();
				TileMap.mapName = msg.reader().readUTF();
				TileMap.zoneID = msg.reader().readByte();
				try
				{
					TileMap.loadMapFromResource(TileMap.mapID);
				}
				catch (Exception)
				{
					Service.gI().requestMaptemplate(TileMap.mapID);
					messWait = msg;
					return;
				}
				loadInfoMap(msg);
				try
				{
					TileMap.isMapDouble = ((msg.reader().readByte() != 0) ? true : false);
				}
				catch (Exception)
				{
				}
				GameScr.cmx = GameScr.cmtoX;
				GameScr.cmy = GameScr.cmtoY;
				break;
			case -31:
			{
				TileMap.vItemBg.removeAllElements();
				short num176 = msg.reader().readShort();
				for (int num177 = 0; num177 < num176; num177++)
				{
					BgItem bgItem = new BgItem();
					bgItem.id = num177;
					bgItem.idImage = msg.reader().readShort();
					bgItem.layer = msg.reader().readByte();
					bgItem.dx = msg.reader().readShort();
					bgItem.dy = msg.reader().readShort();
					sbyte b51 = msg.reader().readByte();
					bgItem.tileX = new int[b51];
					bgItem.tileY = new int[b51];
					for (int num178 = 0; num178 < b51; num178++)
					{
						bgItem.tileX[num177] = msg.reader().readByte();
						bgItem.tileY[num177] = msg.reader().readByte();
					}
					TileMap.vItemBg.addElement(bgItem);
				}
				break;
			}
			case -4:
			{
				GameCanvas.debug("SA76", 2);
				@char = GameScr.findCharInMap(msg.reader().readInt());
				if (@char == null)
				{
					return;
				}
				GameCanvas.debug("SA76v1", 2);
				if ((TileMap.tileTypeAtPixel(@char.cx, @char.cy) & 2) == 2)
				{
					@char.setSkillPaint(GameScr.sks[msg.reader().readUnsignedByte()], 0);
				}
				else
				{
					@char.setSkillPaint(GameScr.sks[msg.reader().readUnsignedByte()], 1);
				}
				GameCanvas.debug("SA76v2", 2);
				@char.attMobs = new Mob[msg.reader().readByte()];
				for (int num156 = 0; num156 < @char.attMobs.Length; num156++)
				{
					Mob mob3 = (Mob)GameScr.vMob.elementAt(msg.reader().readByte());
					@char.attMobs[num156] = mob3;
					if (num156 == 0)
					{
						if (@char.cx <= mob3.x)
						{
							@char.cdir = 1;
						}
						else
						{
							@char.cdir = -1;
						}
					}
				}
				GameCanvas.debug("SA76v3", 2);
				@char.charFocus = null;
				@char.mobFocus = @char.attMobs[0];
				Char[] array10 = new Char[10];
				num = 0;
				try
				{
					for (num = 0; num < array10.Length; num++)
					{
						int num157 = msg.reader().readInt();
						Char char8 = (array10[num] = ((num157 != Char.myCharz().charID) ? GameScr.findCharInMap(num157) : Char.myCharz()));
						if (num == 0)
						{
							if (@char.cx <= char8.cx)
							{
								@char.cdir = 1;
							}
							else
							{
								@char.cdir = -1;
							}
						}
					}
				}
				catch (Exception ex8)
				{
					Cout.println("Loi PLAYER_ATTACK_N_P " + ex8.ToString());
				}
				GameCanvas.debug("SA76v4", 2);
				if (num > 0)
				{
					@char.attChars = new Char[num];
					for (num = 0; num < @char.attChars.Length; num++)
					{
						@char.attChars[num] = array10[num];
					}
					@char.charFocus = @char.attChars[0];
					@char.mobFocus = null;
				}
				GameCanvas.debug("SA76v5", 2);
				break;
			}
			case 54:
			{
				@char = GameScr.findCharInMap(msg.reader().readInt());
				if (@char == null)
				{
					return;
				}
				int num93 = msg.reader().readUnsignedByte();
				if ((TileMap.tileTypeAtPixel(@char.cx, @char.cy) & 2) == 2)
				{
					@char.setSkillPaint(GameScr.sks[num93], 0);
				}
				else
				{
					@char.setSkillPaint(GameScr.sks[num93], 1);
				}
				Mob[] array9 = new Mob[10];
				num = 0;
				try
				{
					for (num = 0; num < array9.Length; num++)
					{
						Mob mob2 = (array9[num] = (Mob)GameScr.vMob.elementAt(msg.reader().readByte()));
						if (num == 0)
						{
							if (@char.cx <= mob2.x)
							{
								@char.cdir = 1;
							}
							else
							{
								@char.cdir = -1;
							}
						}
					}
				}
				catch (Exception)
				{
				}
				if (num > 0)
				{
					@char.attMobs = new Mob[num];
					for (num = 0; num < @char.attMobs.Length; num++)
					{
						@char.attMobs[num] = array9[num];
					}
					@char.charFocus = null;
					@char.mobFocus = @char.attMobs[0];
				}
				break;
			}
			case -60:
			{
				GameCanvas.debug("SA7666", 2);
				int num2 = msg.reader().readInt();
				int num3 = -1;
				if (num2 != Char.myCharz().charID)
				{
					Char char2 = GameScr.findCharInMap(num2);
					if (char2 == null)
					{
						return;
					}
					if (char2.currentMovePoint != null)
					{
						char2.createShadow(char2.cx, char2.cy, 10);
						char2.cx = char2.currentMovePoint.xEnd;
						char2.cy = char2.currentMovePoint.yEnd;
					}
					int num4 = msg.reader().readUnsignedByte();
					if ((TileMap.tileTypeAtPixel(char2.cx, char2.cy) & 2) == 2)
					{
						char2.setSkillPaint(GameScr.sks[num4], 0);
					}
					else
					{
						char2.setSkillPaint(GameScr.sks[num4], 1);
					}
					Char[] array = new Char[msg.reader().readByte()];
					for (num = 0; num < array.Length; num++)
					{
						num3 = msg.reader().readInt();
						Char char3;
						if (num3 == Char.myCharz().charID)
						{
							char3 = Char.myCharz();
							if (!GameScr.isChangeZone && GameScr.isAutoPlay && GameScr.canAutoPlay)
							{
								Service.gI().requestChangeZone(-1, -1);
								GameScr.isChangeZone = true;
							}
						}
						else
						{
							char3 = GameScr.findCharInMap(num3);
						}
						array[num] = char3;
						if (num == 0)
						{
							if (char2.cx <= char3.cx)
							{
								char2.cdir = 1;
							}
							else
							{
								char2.cdir = -1;
							}
						}
					}
					if (num > 0)
					{
						char2.attChars = new Char[num];
						for (num = 0; num < char2.attChars.Length; num++)
						{
							char2.attChars[num] = array[num];
						}
						char2.mobFocus = null;
						char2.charFocus = char2.attChars[0];
					}
				}
				else
				{
					msg.reader().readByte();
					msg.reader().readByte();
					num3 = msg.reader().readInt();
				}
				try
				{
					if (msg.reader().readByte() != 1)
					{
						break;
					}
					sbyte b5 = msg.reader().readByte();
					if (num3 == Char.myCharz().charID)
					{
						bool flag = false;
						@char = Char.myCharz();
						double num5 = msg.reader().readIntToLongDQT();
						@char.isDie = msg.reader().readBoolean();
						if (@char.isDie)
						{
							Char.isLockKey = true;
						}
						double num6 = 0.0;
						flag = (@char.isCrit = msg.reader().readBoolean());
						@char.isMob = false;
						num5 = (@char.damHP = num5 + num6);
						if (b5 == 0)
						{
							@char.doInjure(num5, 0.0, flag, isMob: false);
						}
					}
					else
					{
						@char = GameScr.findCharInMap(num3);
						if (@char == null)
						{
							return;
						}
						bool flag2 = false;
						double num7 = msg.reader().readIntToLongDQT();
						@char.isDie = msg.reader().readBoolean();
						double num8 = 0.0;
						flag2 = (@char.isCrit = msg.reader().readBoolean());
						@char.isMob = false;
						num7 = (@char.damHP = num7 + num8);
						if (b5 == 0)
						{
							@char.doInjure(num7, 0.0, flag2, isMob: false);
						}
					}
				}
				catch (Exception)
				{
				}
				break;
			}
			}
			switch (msg.command)
			{
			case -2:
			{
				GameCanvas.debug("SA77", 22);
				int num282 = msg.reader().readInt();
				Char.myCharz().yen += num282;
				GameScr.startFlyText((num282 <= 0) ? (string.Empty + num282) : ("+" + num282), Char.myCharz().cx, Char.myCharz().cy - Char.myCharz().ch - 10, 0, -2, mFont.YELLOW);
				break;
			}
			case 95:
			{
				GameCanvas.debug("SA77", 22);
				int num296 = msg.reader().readInt();
				Char.myCharz().xu += num296;
				Char.myCharz().xuStr = mSystem.numberTostring(Char.myCharz().xu);
				GameScr.startFlyText((num296 <= 0) ? (string.Empty + num296) : ("+" + num296), Char.myCharz().cx, Char.myCharz().cy - Char.myCharz().ch - 10, 0, -2, mFont.YELLOW);
				break;
			}
			case 96:
				GameCanvas.debug("SA77a", 22);
				Char.myCharz().taskOrders.addElement(new TaskOrder(msg.reader().readByte(), msg.reader().readShort(), msg.reader().readShort(), msg.reader().readUTF(), msg.reader().readUTF(), msg.reader().readByte(), msg.reader().readByte()));
				break;
			case 97:
			{
				sbyte b79 = msg.reader().readByte();
				for (int num285 = 0; num285 < Char.myCharz().taskOrders.size(); num285++)
				{
					TaskOrder taskOrder = (TaskOrder)Char.myCharz().taskOrders.elementAt(num285);
					if (taskOrder.taskId == b79)
					{
						taskOrder.count = msg.reader().readShort();
						break;
					}
				}
				break;
			}
			case -1:
			{
				GameCanvas.debug("SA77", 222);
				int num286 = msg.reader().readInt();
				Char.myCharz().xu += num286;
				Char.myCharz().xuStr = mSystem.numberTostring(Char.myCharz().xu);
				Char.myCharz().yen -= num286;
				GameScr.startFlyText("+" + num286, Char.myCharz().cx, Char.myCharz().cy - Char.myCharz().ch - 10, 0, -2, mFont.YELLOW);
				break;
			}
			case -3:
			{
				sbyte num281 = msg.reader().readByte();
				int param11 = msg.reader().readInt();
				if (num281 == 0)
				{
					Char.myCharz().cPower += param11;
				}
				if (num281 == 1)
				{
					Char.myCharz().cTiemNang += param11;
				}
				if (num281 == 2)
				{
					Char.myCharz().cPower += param11;
					Char.myCharz().cTiemNang += param11;
				}
				Char.myCharz().applyCharLevelPercent();
				if (Char.myCharz().cTypePk != 3)
				{
					GameScr.startFlyText(((param11 <= 0.0) ? string.Empty : "+") + param11, Char.myCharz().cx, Char.myCharz().cy - Char.myCharz().ch, 0, -4, mFont.GREEN);
					if (param11 > 0.0 && Char.myCharz().petFollow != null && Char.myCharz().petFollow.smallID == 5002)
					{
						ServerEffect.addServerEffect(55, Char.myCharz().petFollow.cmx, Char.myCharz().petFollow.cmy, 1);
						ServerEffect.addServerEffect(55, Char.myCharz().cx, Char.myCharz().cy, 1);
					}
				}
				break;
			}
			case -73:
			{
				sbyte npcId2 = msg.reader().readByte();
				for (int num295 = 0; num295 < GameScr.vNpc.size(); num295++)
				{
					Npc npc8 = (Npc)GameScr.vNpc.elementAt(num295);
					if (npc8.template.npcTemplateId == npcId2)
					{
						if (msg.reader().readByte() == 0)
						{
							npc8.isHide = true;
						}
						else
						{
							npc8.isHide = false;
						}
						break;
					}
				}
				break;
			}
			case -5:
			{
				int charID2 = msg.reader().readInt();
				int num291 = msg.reader().readInt();
				Char char15 = ((num291 == -100) ? new Mabu
				{
					charID = charID2,
					clanID = num291
				} : new Char
				{
					charID = charID2,
					clanID = num291
				});
				if (char15.clanID == -2)
				{
					char15.isCopy = true;
				}
				if (readCharInfo(char15, msg))
				{
					sbyte b81 = msg.reader().readByte();
					if (char15.cy <= 10 && b81 != 0 && b81 != 2)
					{
						Teleport p3 = new Teleport(char15.cx, char15.cy, char15.head, char15.cdir, 1, isMe: false, (b81 != 1) ? b81 : char15.cgender)
						{
							id = char15.charID
						};
						char15.isTeleport = true;
						Teleport.addTeleport(p3);
					}
					if (b81 == 2)
					{
						char15.show();
					}
					for (int num292 = 0; num292 < GameScr.vMob.size(); num292++)
					{
						Mob mob17 = (Mob)GameScr.vMob.elementAt(num292);
						if (mob17 != null && mob17.isMobMe && mob17.mobId == char15.charID)
						{
							char15.mobMe = mob17;
							char15.mobMe.x = char15.cx;
							char15.mobMe.y = char15.cy - 40;
							break;
						}
					}
					if (GameScr.findCharInMap(char15.charID) == null)
					{
						GameScr.vCharInMap.addElement(char15);
					}
					char15.isMonkey = msg.reader().readByte();
					short num293 = msg.reader().readShort();
					if (num293 != -1)
					{
						char15.isHaveMount = true;
						switch (num293)
						{
						case 346:
						case 347:
						case 348:
							char15.isMountVip = false;
							break;
						case 349:
						case 350:
						case 351:
							char15.isMountVip = true;
							break;
						case 396:
							char15.isEventMount = true;
							break;
						case 532:
							char15.isSpeacialMount = true;
							break;
						default:
							if (num293 >= Char.ID_NEW_MOUNT)
							{
								char15.idMount = num293;
							}
							break;
						}
					}
					else
					{
						char15.isHaveMount = false;
					}
				}
				sbyte b82 = msg.reader().readByte();
				char15.cFlag = b82;
				char15.isNhapThe = msg.reader().readByte() == 1;
				try
				{
					char15.idAuraEff = msg.reader().readShort();
					char15.idEff_Set_Item = msg.reader().readSByte();
					char15.idHat = msg.reader().readShort();
					if (char15.bag >= 201 && char15.bag < 255)
					{
						Effect effect2 = new Effect(char15.bag, char15, 2, -1, 10, 1);
						effect2.typeEff = 5;
						char15.addEffChar(effect2);
					}
					else
					{
						for (int num294 = 0; num294 < 54; num294++)
						{
							char15.removeEffChar(0, 201 + num294);
						}
					}
				}
				catch (Exception ex41)
				{
					Res.outz("cmd: -5 err: " + ex41.StackTrace);
				}
				char15.isTichXanh = msg.reader().readByte() == 1;
				GameScr.gI().getFlagImage(char15.charID, char15.cFlag);
				break;
			}
			case -7:
			{
				int num287 = msg.reader().readInt();
				for (int num288 = 0; num288 < GameScr.vCharInMap.size(); num288++)
				{
					Char char14 = null;
					try
					{
						char14 = (Char)GameScr.vCharInMap.elementAt(num288);
					}
					catch (Exception)
					{
					}
					if (char14 == null)
					{
						break;
					}
					if (char14.charID == num287)
					{
						GameCanvas.debug("SA8x2y" + num288, 2);
						char14.moveTo(msg.reader().readShort(), msg.reader().readShort(), 0);
						char14.lastUpdateTime = mSystem.currentTimeMillis();
						break;
					}
				}
				GameCanvas.debug("SA80x3", 2);
				break;
			}
			case -6:
			{
				GameCanvas.debug("SA81", 2);
				int num283 = msg.reader().readInt();
				for (int num284 = 0; num284 < GameScr.vCharInMap.size(); num284++)
				{
					Char char13 = (Char)GameScr.vCharInMap.elementAt(num284);
					if (char13 != null && char13.charID == num283)
					{
						if (!char13.isInvisiblez && !char13.isUsePlane)
						{
							ServerEffect.addServerEffect(60, char13.cx, char13.cy, 1);
						}
						if (!char13.isUsePlane)
						{
							GameScr.vCharInMap.removeElementAt(num284);
						}
						break;
					}
				}
				break;
			}
			case -13:
			{
				int num289 = msg.reader().readUnsignedByte();
				if (num289 > GameScr.vMob.size() - 1 || num289 < 0)
				{
					break;
				}
				Mob mob15 = (Mob)GameScr.vMob.elementAt(num289);
				if (mob15.status == 0 || mob15.status == 1)
				{
					mob15.sys = msg.reader().readByte();
					mob15.levelBoss = msg.reader().readByte();
					if (mob15.levelBoss != 0)
					{
						mob15.typeSuperEff = Res.random(0, 3);
					}
					mob15.x = mob15.xFirst;
					mob15.y = mob15.yFirst;
					mob15.status = 5;
					mob15.injureThenDie = false;
					mob15.hp = msg.reader().readIntToLongDQT();
					mob15.maxHp = mob15.hp;
					mob15.updateHp_bar();
					ServerEffect.addServerEffect(60, mob15.x, mob15.y, 1);
				}
				break;
			}
			case -75:
			{
				Mob mob12 = null;
				try
				{
					mob12 = (Mob)GameScr.vMob.elementAt(msg.reader().readUnsignedByte());
				}
				catch (Exception)
				{
				}
				if (mob12 != null)
				{
					mob12.levelBoss = msg.reader().readByte();
					if (mob12.levelBoss > 0)
					{
						mob12.typeSuperEff = Res.random(0, 3);
					}
				}
				break;
			}
			case -9:
			{
				Mob mob11 = null;
				try
				{
					mob11 = (Mob)GameScr.vMob.elementAt(msg.reader().readUnsignedByte());
				}
				catch (Exception)
				{
				}
				if (mob11 == null)
				{
					break;
				}
				mob11.hp = msg.reader().readIntToLongDQT();
				mob11.updateHp_bar();
				double dame2 = msg.reader().readIntToLongDQT();
				if (dame2 != 1.0)
				{
					if (dame2 > 1.0)
					{
						mob11.setInjure();
					}
					bool flag14 = false;
					try
					{
						flag14 = msg.reader().readBoolean();
					}
					catch (Exception)
					{
					}
					sbyte b78 = msg.reader().readByte();
					if (b78 != -1)
					{
						EffecMn.addEff(new Effect(b78, mob11.x, mob11.getY(), 3, 1, -1));
					}
					if (flag14)
					{
						GameScr.startFlyText("-" + dame2, mob11.x, mob11.getY() - mob11.getH(), 0, -2, mFont.FATAL);
					}
					else if (dame2 == 0.0)
					{
						mob11.x = mob11.xFirst;
						mob11.y = mob11.yFirst;
						GameScr.startFlyText(mResources.miss, mob11.x, mob11.getY() - mob11.getH(), 0, -2, mFont.MISS);
					}
					else if (dame2 > 1.0)
					{
						GameScr.startFlyText("-" + dame2, mob11.x, mob11.getY() - mob11.getH(), 0, -2, mFont.ORANGE);
					}
				}
				break;
			}
			case 45:
			{
				Mob mob10 = null;
				try
				{
					mob10 = (Mob)GameScr.vMob.elementAt(msg.reader().readUnsignedByte());
				}
				catch (Exception)
				{
				}
				if (mob10 != null)
				{
					mob10.hp = msg.reader().readInt();
					mob10.updateHp_bar();
					GameScr.startFlyText(mResources.miss, mob10.x, mob10.y - mob10.h, 0, -2, mFont.MISS);
				}
				break;
			}
			case -12:
			{
				Mob mob16 = null;
				try
				{
					mob16 = (Mob)GameScr.vMob.elementAt(msg.reader().readUnsignedByte());
				}
				catch (Exception)
				{
				}
				if (mob16 == null || mob16.status == 0 || mob16.status == 0)
				{
					break;
				}
				mob16.startDie();
				try
				{
					double dameHit4 = msg.reader().readIntToLongDQT();
					if (msg.reader().readBool())
					{
						GameScr.startFlyText("-" + dameHit4, mob16.x, mob16.y - mob16.h, 0, -2, mFont.FATAL);
					}
					else
					{
						GameScr.startFlyText("-" + dameHit4, mob16.x, mob16.y - mob16.h, 0, -2, mFont.ORANGE);
					}
					sbyte b80 = msg.reader().readByte();
					for (int num290 = 0; num290 < b80; num290++)
					{
						ItemMap itemMap4 = new ItemMap(msg.reader().readShort(), msg.reader().readShort(), mob16.x, mob16.y, msg.reader().readShort(), msg.reader().readShort());
						itemMap4.playerId = msg.reader().readInt();
						GameScr.vItemMap.addElement(itemMap4);
						if (Res.abs(itemMap4.y - Char.myCharz().cy) < 24 && Res.abs(itemMap4.x - Char.myCharz().cx) < 24)
						{
							Char.myCharz().charFocus = null;
						}
					}
					break;
				}
				catch (Exception)
				{
					break;
				}
			}
			case 74:
			{
				Mob mob13 = null;
				try
				{
					mob13 = (Mob)GameScr.vMob.elementAt(msg.reader().readUnsignedByte());
				}
				catch (Exception)
				{
				}
				if (mob13 != null && mob13.status != 0 && mob13.status != 0)
				{
					mob13.status = 0;
					ServerEffect.addServerEffect(60, mob13.x, mob13.y, 1);
					ItemMap itemMap3 = new ItemMap(msg.reader().readShort(), msg.reader().readShort(), mob13.x, mob13.y, msg.reader().readShort(), msg.reader().readShort());
					GameScr.vItemMap.addElement(itemMap3);
					if (Res.abs(itemMap3.y - Char.myCharz().cy) < 24 && Res.abs(itemMap3.x - Char.myCharz().cx) < 24)
					{
						Char.myCharz().charFocus = null;
					}
				}
				break;
			}
			case -11:
			{
				Mob mob18 = null;
				try
				{
					int index4 = msg.reader().readUnsignedByte();
					mob18 = (Mob)GameScr.vMob.elementAt(index4);
				}
				catch (Exception)
				{
				}
				if (mob18 != null)
				{
					Char.myCharz().isDie = false;
					Char.isLockKey = false;
					double dame3 = msg.reader().readIntToLongDQT();
					double num297;
					try
					{
						num297 = msg.reader().readIntToLongDQT();
					}
					catch (Exception)
					{
						num297 = 0.0;
					}
					if (mob18.isBusyAttackSomeOne)
					{
						Char.myCharz().doInjure(dame3, num297, isCrit: false, isMob: true);
						break;
					}
					mob18.dame = dame3;
					mob18.dameMp = num297;
					mob18.setAttack(Char.myCharz());
				}
				break;
			}
			case -10:
			{
				Mob mob14 = null;
				try
				{
					mob14 = (Mob)GameScr.vMob.elementAt(msg.reader().readUnsignedByte());
				}
				catch (Exception)
				{
				}
				if (mob14 == null)
				{
					break;
				}
				@char = GameScr.findCharInMap(msg.reader().readInt());
				if (@char != null)
				{
					double cHP = msg.reader().readIntToLongDQT();
					mob14.dame = @char.cHP - cHP;
					@char.cHPNew = cHP;
					try
					{
						@char.cMP = msg.reader().readIntToLongDQT();
					}
					catch (Exception)
					{
					}
					if (mob14.isBusyAttackSomeOne)
					{
						@char.doInjure(mob14.dame, 0.0, isCrit: false, isMob: true);
					}
					else
					{
						mob14.setAttack(@char);
					}
				}
				break;
			}
			case -17:
				Char.myCharz().meDead = true;
				Char.myCharz().cPk = msg.reader().readByte();
				Char.myCharz().startDie(msg.reader().readShort(), msg.reader().readShort());
				try
				{
					Char.myCharz().cPower = msg.reader().readLong();
					Char.myCharz().applyCharLevelPercent();
				}
				catch (Exception)
				{
					Cout.println("Loi tai ME_DIE " + msg.command);
				}
				Char.myCharz().countKill = 0;
				break;
			case -8:
				@char = GameScr.findCharInMap(msg.reader().readInt());
				if (@char != null)
				{
					@char.cPk = msg.reader().readByte();
					@char.waitToDie(msg.reader().readShort(), msg.reader().readShort());
				}
				break;
			case -16:
				if (Char.myCharz().wdx != 0 || Char.myCharz().wdy != 0)
				{
					Char.myCharz().cx = Char.myCharz().wdx;
					Char.myCharz().cy = Char.myCharz().wdy;
					Char.myCharz().wdx = (Char.myCharz().wdy = 0);
				}
				Char.myCharz().liveFromDead();
				Char.myCharz().isLockMove = false;
				Char.myCharz().meDead = false;
				break;
			case 44:
			{
				int num280 = msg.reader().readInt();
				string text8 = msg.reader().readUTF();
				((Char.myCharz().charID != num280) ? GameScr.findCharInMap(num280) : Char.myCharz())?.addInfo(text8);
				break;
			}
			case 18:
			{
				sbyte b77 = msg.reader().readByte();
				for (int num279 = 0; num279 < b77; num279++)
				{
					int charId2 = msg.reader().readInt();
					int cx = msg.reader().readShort();
					int cy = msg.reader().readShort();
					double cHPShow = msg.reader().readIntToLongDQT();
					Char char12 = GameScr.findCharInMap(charId2);
					if (char12 != null)
					{
						char12.cx = cx;
						char12.cy = cy;
						char12.cHP = (char12.cHPShow = cHPShow);
						char12.lastUpdateTime = mSystem.currentTimeMillis();
					}
				}
				break;
			}
			case 19:
				Char.myCharz().countKill = msg.reader().readUnsignedShort();
				Char.myCharz().countKillMax = msg.reader().readUnsignedShort();
				break;
			}
		}
		catch (Exception)
		{
		}
		finally
		{
			msg?.cleanup();
		}
	}

    private void readLogin(Message msg)
    {
        sbyte b = msg.reader().readByte();
        ChooseCharScr.playerData = new PlayerData[b];
        Res.outz("[LEN] sl nguoi choi " + b);
        for (int i = 0; i < b; i++)
        {
            int playerID = msg.reader().readInt();
            string name = msg.reader().readUTF();
            short head = msg.reader().readShort();
            short body = msg.reader().readShort();
            short leg = msg.reader().readShort();
            long ppoint = msg.reader().readLong();
            ChooseCharScr.playerData[i] = new PlayerData(playerID, name, head, body, leg, ppoint);
        }
        GameCanvas.chooseCharScr.switchToMe();
        GameCanvas.chooseCharScr.updateChooseCharacter((byte)b);
    }

    private void createItem(myReader d)
    {
        GameScr.vcItem = d.readByte();
        ItemTemplates.itemTemplates.clear();
        GameScr.gI().iOptionTemplates = new ItemOptionTemplate[d.readUnsignedByte()];
        for (int i = 0; i < GameScr.gI().iOptionTemplates.Length; i++)
        {
            GameScr.gI().iOptionTemplates[i] = new ItemOptionTemplate();
            GameScr.gI().iOptionTemplates[i].id = i;
            GameScr.gI().iOptionTemplates[i].name = d.readUTF();
            GameScr.gI().iOptionTemplates[i].type = d.readByte();
        }
        int num = d.readShort();
        for (int j = 0; j < num; j++)
        {
            ItemTemplate it = new ItemTemplate((short)j, d.readByte(), d.readByte(), d.readUTF(), d.readUTF(), d.readByte(), d.readInt(), d.readShort(), d.readShort(), d.readBool());
            ItemTemplates.add(it);
        }
    }

    private void createSkill(myReader d)
    {
        GameScr.vcSkill = d.readByte();
        GameScr.gI().sOptionTemplates = new SkillOptionTemplate[d.readByte()];
        for (int i = 0; i < GameScr.gI().sOptionTemplates.Length; i++)
        {
            GameScr.gI().sOptionTemplates[i] = new SkillOptionTemplate();
            GameScr.gI().sOptionTemplates[i].id = i;
            GameScr.gI().sOptionTemplates[i].name = d.readUTF();
        }
        GameScr.nClasss = new NClass[d.readByte()];
        for (int j = 0; j < GameScr.nClasss.Length; j++)
        {
            GameScr.nClasss[j] = new NClass();
            GameScr.nClasss[j].classId = j;
            GameScr.nClasss[j].name = d.readUTF();
            GameScr.nClasss[j].skillTemplates = new SkillTemplate[d.readByte()];
            for (int k = 0; k < GameScr.nClasss[j].skillTemplates.Length; k++)
            {
                GameScr.nClasss[j].skillTemplates[k] = new SkillTemplate();
                GameScr.nClasss[j].skillTemplates[k].id = d.readByte();
                GameScr.nClasss[j].skillTemplates[k].name = d.readUTF();
                GameScr.nClasss[j].skillTemplates[k].maxPoint = d.readByte();
                GameScr.nClasss[j].skillTemplates[k].manaUseType = d.readByte();
                GameScr.nClasss[j].skillTemplates[k].type = d.readByte();
                GameScr.nClasss[j].skillTemplates[k].iconId = d.readShort();
                GameScr.nClasss[j].skillTemplates[k].damInfo = d.readUTF();
                int lineWidth = 130;
                if (GameCanvas.w == 128 || GameCanvas.h <= 208)
                {
                    lineWidth = 100;
                }
                GameScr.nClasss[j].skillTemplates[k].description = mFont.tahoma_7_green2.splitFontArray(d.readUTF(), lineWidth);
                GameScr.nClasss[j].skillTemplates[k].skills = new Skill[d.readByte()];
                for (int l = 0; l < GameScr.nClasss[j].skillTemplates[k].skills.Length; l++)
                {
                    GameScr.nClasss[j].skillTemplates[k].skills[l] = new Skill();
                    GameScr.nClasss[j].skillTemplates[k].skills[l].skillId = d.readShort();
                    GameScr.nClasss[j].skillTemplates[k].skills[l].template = GameScr.nClasss[j].skillTemplates[k];
                    GameScr.nClasss[j].skillTemplates[k].skills[l].point = d.readByte();
                    GameScr.nClasss[j].skillTemplates[k].skills[l].powRequire = d.readLong();
                    GameScr.nClasss[j].skillTemplates[k].skills[l].manaUse = d.readShort();
                    GameScr.nClasss[j].skillTemplates[k].skills[l].coolDown = d.readInt();
                    GameScr.nClasss[j].skillTemplates[k].skills[l].dx = d.readShort();
                    GameScr.nClasss[j].skillTemplates[k].skills[l].dy = d.readShort();
                    GameScr.nClasss[j].skillTemplates[k].skills[l].maxFight = d.readByte();
                    GameScr.nClasss[j].skillTemplates[k].skills[l].damage = d.readShort();
                    GameScr.nClasss[j].skillTemplates[k].skills[l].price = d.readShort();
                    GameScr.nClasss[j].skillTemplates[k].skills[l].moreInfo = d.readUTF();
                    Skills.add(GameScr.nClasss[j].skillTemplates[k].skills[l]);
                }
            }
        }
    }

    private void createMap(myReader d)
    {
        GameScr.vcMap = d.readByte();
        TileMap.mapNames = new string[d.readUnsignedByte()];
        for (int i = 0; i < TileMap.mapNames.Length; i++)
        {
            TileMap.mapNames[i] = d.readUTF();
        }
        Npc.arrNpcTemplate = new NpcTemplate[d.readByte()];
        for (sbyte b = 0; b < Npc.arrNpcTemplate.Length; b++)
        {
            Npc.arrNpcTemplate[b] = new NpcTemplate();
            Npc.arrNpcTemplate[b].npcTemplateId = b;
            Npc.arrNpcTemplate[b].name = d.readUTF();
            Npc.arrNpcTemplate[b].headId = d.readShort();
            Npc.arrNpcTemplate[b].bodyId = d.readShort();
            Npc.arrNpcTemplate[b].legId = d.readShort();
            Npc.arrNpcTemplate[b].menu = new string[d.readByte()][];
            for (int j = 0; j < Npc.arrNpcTemplate[b].menu.Length; j++)
            {
                Npc.arrNpcTemplate[b].menu[j] = new string[d.readByte()];
                for (int k = 0; k < Npc.arrNpcTemplate[b].menu[j].Length; k++)
                {
                    Npc.arrNpcTemplate[b].menu[j][k] = d.readUTF();
                }
            }
        }
        Mob.arrMobTemplate = new MobTemplate[d.readByte()];
        for (sbyte b2 = 0; b2 < Mob.arrMobTemplate.Length; b2++)
        {
            Mob.arrMobTemplate[b2] = new MobTemplate();
            Mob.arrMobTemplate[b2].mobTemplateId = b2;
            Mob.arrMobTemplate[b2].type = d.readByte();
            Mob.arrMobTemplate[b2].name = d.readUTF();
            Mob.arrMobTemplate[b2].hp = d.readIntToLongDQT();
            Mob.arrMobTemplate[b2].rangeMove = d.readByte();
            Mob.arrMobTemplate[b2].speed = d.readByte();
            Mob.arrMobTemplate[b2].dartType = d.readByte();
        }
    }

    private void createData(myReader d, bool isSaveRMS)
    {
        GameScr.vcData = d.readByte();
        if (isSaveRMS)
        {
            Rms.saveRMS("NR_dart", NinjaUtil.readByteArray(d));
            Rms.saveRMS("NR_arrow", NinjaUtil.readByteArray(d));
            Rms.saveRMS("NR_effect", NinjaUtil.readByteArray(d));
            Rms.saveRMS("NR_image", NinjaUtil.readByteArray(d));
            Rms.saveRMS("NR_part", NinjaUtil.readByteArray(d));
            Rms.saveRMS("NR_skill", NinjaUtil.readByteArray(d));
            Rms.DeleteStorage("NRdata");
        }
    }

   private Image createImage(sbyte[] arr)
	{
		try
		{
			return Image.createImage(arr, 0, arr.Length);
		}
		catch (Exception)
		{
		}
		return null;
	}

public Image createImage(sbyte[] arr, string key)
	{
		try
		{
			string keyOrigin = DecryptString(key);
			sbyte[] decrypt = Array.ConvertAll(DecryptImage(Array.ConvertAll(arr, (sbyte a) => (byte)a), keyOrigin), (byte a) => (sbyte)a);
			return Image.createImage(decrypt, 0, decrypt.Length);
		}
		catch (Exception)
		{
		}
		return null;
	}
public static string DecryptString(string encryptedText)
	{
		byte[] keyData = Encoding.UTF8.GetBytes(ServerListScreen.keyDecryptString);
		using Aes aesAlg = Aes.Create();
		aesAlg.Key = keyData;
		aesAlg.Mode = CipherMode.ECB;
		aesAlg.Padding = PaddingMode.PKCS7;
		ICryptoTransform decryptor = aesAlg.CreateDecryptor(aesAlg.Key, aesAlg.IV);
		byte[] byteArray = Convert.FromBase64String(encryptedText);
		for (int i = 0; i < 4; i++)
		{
			byteArray = decryptor.TransformFinalBlock(byteArray, 0, byteArray.Length);
		}
		return Encoding.UTF8.GetString(byteArray);
	}

	public static byte[] DecryptImage(byte[] dataImage, string key)
	{
		Texture2D encryptedTexture = new Texture2D(1, 1);
		encryptedTexture.LoadImage(dataImage);
		int width = encryptedTexture.width;
		int height = encryptedTexture.height;
		Assets.Assets.Scripts.Assembly_CSharp.Random random = new Assets.Assets.Scripts.Assembly_CSharp.Random((ulong)JavaHashCode(key));
		Color32[] pixels = encryptedTexture.GetPixels32();
		Color32[] rearrangedPixels = new Color32[pixels.Length];
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				rearrangedPixels[(height - y - 1) * width + x] = pixels[y * width + x];
			}
		}
		for (int i = 0; i < rearrangedPixels.Length; i++)
		{
			Color32 pixel = rearrangedPixels[i];
			if (pixel.a != 0)
			{
				int red = (pixel.r - random.NextInt(256) + 256) % 256;
				int green = (pixel.g - random.NextInt(256) + 256) % 256;
				int blue = (pixel.b - random.NextInt(256) + 256) % 256;
				rearrangedPixels[i] = new Color32((byte)red, (byte)green, (byte)blue, pixel.a);
			}
		}
		for (int j = 0; j < height; j++)
		{
			for (int k = 0; k < width; k++)
			{
				encryptedTexture.SetPixel(k, j, rearrangedPixels[j * width + (width - 1 - k)]);
			}
		}
		for (int l = 0; l < height; l++)
		{
			for (int m = 0; m < width; m++)
			{
				encryptedTexture.SetPixel(m, height - 1 - l, rearrangedPixels[l * width + m]);
			}
		}
		encryptedTexture.Apply();
		return encryptedTexture.EncodeToPNG();
	}

public static int JavaHashCode(string str)
	{
		int hash = 0;
		for (int i = 0; i < str.Length; i++)
		{
			hash = 31 * hash + str[i];
		}
		return hash;
	}
    public int[] arrayByte2Int(sbyte[] b)
    {
        int[] array = new int[b.Length];
        for (int i = 0; i < b.Length; i++)
        {
            int num = b[i];
            if (num < 0)
            {
                num += 256;
            }
            array[i] = num;
        }
        return array;
    }

    public void readClanMsg(Message msg, int index)
    {
        try
        {
            ClanMessage clanMessage = new ClanMessage();
            sbyte b = msg.reader().readByte();
            clanMessage.type = b;
            clanMessage.id = msg.reader().readInt();
            clanMessage.playerId = msg.reader().readInt();
            clanMessage.playerName = msg.reader().readUTF();
            clanMessage.role = msg.reader().readByte();
            clanMessage.time = msg.reader().readInt() + 1000000000;
            bool flag = false;
            GameScr.isNewClanMessage = false;
            if (b == 0)
            {
                string text = msg.reader().readUTF();
                GameScr.isNewClanMessage = true;
                if (mFont.tahoma_7.getWidth(text) > Panel.WIDTH_PANEL - 60)
                {
                    clanMessage.chat = mFont.tahoma_7.splitFontArray(text, Panel.WIDTH_PANEL - 10);
                }
                else
                {
                    clanMessage.chat = new string[1];
                    clanMessage.chat[0] = text;
                }
                clanMessage.color = msg.reader().readByte();
            }
            else if (b == 1)
            {
                clanMessage.recieve = msg.reader().readByte();
                clanMessage.maxCap = msg.reader().readByte();
                flag = msg.reader().readByte() == 1;
                if (flag)
                {
                    GameScr.isNewClanMessage = true;
                }
                if (clanMessage.playerId != Char.myCharz().charID)
                {
                    if (clanMessage.recieve < clanMessage.maxCap)
                    {
                        clanMessage.option = new string[1] { mResources.donate };
                    }
                    else
                    {
                        clanMessage.option = null;
                    }
                }
                if (GameCanvas.panel.cp != null)
                {
                    GameCanvas.panel.updateRequest(clanMessage.recieve, clanMessage.maxCap);
                }
            }
            else if (b == 2 && Char.myCharz().role == 0)
            {
                GameScr.isNewClanMessage = true;
                clanMessage.option = new string[2]
                {
                    mResources.CANCEL,
                    mResources.receive
                };
            }
            if (GameCanvas.currentScreen != GameScr.instance)
            {
                GameScr.isNewClanMessage = false;
            }
            else if (GameCanvas.panel.isShow && GameCanvas.panel.type == 0 && GameCanvas.panel.currentTabIndex == 3)
            {
                GameScr.isNewClanMessage = false;
            }
            ClanMessage.addMessage(clanMessage, index, flag);
        }
        catch (Exception)
        {
            Cout.println("LOI TAI CMD -= " + msg.command);
        }
    }

   public void loadCurrMap(sbyte teleport3)
	{
		GameScr.gI().auto = 0;
		GameScr.isChangeZone = false;
		CreateCharScr.instance = null;
		GameScr.info1.isUpdate = false;
		GameScr.info2.isUpdate = false;
		GameScr.lockTick = 0;
		GameCanvas.panel.isShow = false;
		SoundMn.gI().stopAll();
		if (!GameScr.isLoadAllData && !CreateCharScr.isCreateChar)
		{
			GameScr.gI().initSelectChar();
		}
		GameScr.loadCamera(fullmScreen: false, (teleport3 != 1) ? (-1) : Char.myCharz().cx, (teleport3 == 0) ? (-1) : 0);
		TileMap.loadMainTile();
		TileMap.loadMap(TileMap.tileID);
		Char.myCharz().cvx = 0;
		Char.myCharz().statusMe = 4;
		Char.myCharz().currentMovePoint = null;
		Char.myCharz().mobFocus = null;
		Char.myCharz().charFocus = null;
		Char.myCharz().npcFocus = null;
		Char.myCharz().itemFocus = null;
		Char.myCharz().skillPaint = null;
		Char.myCharz().setMabuHold(m: false);
		Char.myCharz().skillPaintRandomPaint = null;
		GameCanvas.clearAllPointerEvent();
		if (Char.myCharz().cy >= TileMap.pxh - 100)
		{
			Char.myCharz().isFlyUp = true;
			Char.myCharz().cx += Res.abs(Res.random(0, 80));
			Service.gI().charMove();
		}
		GameScr.gI().loadGameScr();
		GameCanvas.loadBG(TileMap.bgID);
		Char.isLockKey = false;
		for (int i = 0; i < Char.myCharz().vEff.size(); i++)
		{
			if (((EffectChar)Char.myCharz().vEff.elementAt(i)).template.type == 10)
			{
				Char.isLockKey = true;
				break;
			}
		}
		GameCanvas.clearKeyHold();
		GameCanvas.clearKeyPressed();
		GameScr.gI().dHP = Char.myCharz().cHP;
		GameScr.gI().dMP = Char.myCharz().cMP;
		Char.ischangingMap = false;
		GameScr.gI().switchToMe();
		if (Char.myCharz().cy <= 10 && teleport3 != 0 && teleport3 != 2)
		{
			Teleport.addTeleport(new Teleport(Char.myCharz().cx, Char.myCharz().cy, Char.myCharz().head, Char.myCharz().cdir, 1, isMe: true, (teleport3 != 1) ? teleport3 : Char.myCharz().cgender));
			Char.myCharz().isTeleport = true;
		}
		if (teleport3 == 2)
		{
			Char.myCharz().show();
		}
		if (GameScr.gI().isRongThanXuatHien)
		{
			if (TileMap.mapID == GameScr.gI().mapRID && TileMap.zoneID == GameScr.gI().zoneRID)
			{
				GameScr.gI().callRongThan(GameScr.gI().xR, GameScr.gI().yR);
			}
			if (mGraphics.zoomLevel > 1)
			{
				GameScr.gI().doiMauTroi();
			}
		}
		InfoDlg.hide();
		InfoDlg.show(TileMap.mapName, mResources.zone + " " + TileMap.zoneID, 30);
		GameCanvas.endDlg();
		GameCanvas.isLoading = false;
		Hint.clickMob();
		Hint.clickNpc();
	}

    public void loadInfoMap(Message msg)
	{
		try
		{
			if (mGraphics.zoomLevel == 1)
			{
				SmallImage.clearHastable();
			}
			Char.myCharz().cx = (Char.myCharz().cxSend = (Char.myCharz().cxFocus = msg.reader().readShort()));
			Char.myCharz().cy = (Char.myCharz().cySend = (Char.myCharz().cyFocus = msg.reader().readShort()));
			Char.myCharz().xSd = Char.myCharz().cx;
			Char.myCharz().ySd = Char.myCharz().cy;
			if (Char.myCharz().cx >= 0 && Char.myCharz().cx <= 100)
			{
				Char.myCharz().cdir = 1;
			}
			else if (Char.myCharz().cx >= TileMap.tmw - 100 && Char.myCharz().cx <= TileMap.tmw)
			{
				Char.myCharz().cdir = -1;
			}
			int num = msg.reader().readByte();
			if (!GameScr.info1.isDone)
			{
				GameScr.info1.cmx = Char.myCharz().cx - GameScr.cmx;
				GameScr.info1.cmy = Char.myCharz().cy - GameScr.cmy;
			}
			for (int i = 0; i < num; i++)
			{
				Waypoint waypoint = new Waypoint(msg.reader().readShort(), msg.reader().readShort(), msg.reader().readShort(), msg.reader().readShort(), msg.reader().readBoolean(), msg.reader().readBoolean(), msg.reader().readUTF());
				if ((TileMap.mapID == 21 || TileMap.mapID == 22 || TileMap.mapID == 23) && waypoint.minX >= 0)
				{
					_ = waypoint.minX;
					_ = 24;
				}
			}
			Resources.UnloadUnusedAssets();
			GC.Collect();
			num = msg.reader().readByte();
			Mob.newMob.removeAllElements();
			for (sbyte b = 0; b < num; b++)
			{
				Mob mob = new Mob(b, msg.reader().readBoolean(), msg.reader().readBoolean(), msg.reader().readBoolean(), msg.reader().readBoolean(), msg.reader().readBoolean(), msg.reader().readByte(), msg.reader().readByte(), msg.reader().readIntToLongDQT(), msg.reader().readByte(), msg.reader().readIntToLongDQT(), msg.reader().readShort(), msg.reader().readShort(), msg.reader().readByte(), msg.reader().readByte());
				mob.xSd = mob.x;
				mob.ySd = mob.y;
				mob.isBoss = msg.reader().readBoolean();
				if (Mob.arrMobTemplate[mob.templateId].type != 0)
				{
					if (b % 3 == 0)
					{
						mob.dir = -1;
					}
					else
					{
						mob.dir = 1;
					}
					mob.x += 10 - b % 20;
				}
				mob.isMobMe = false;
				BigBoss bigBoss = null;
				BachTuoc bachTuoc = null;
				BigBoss2 bigBoss2 = null;
				NewBoss newBoss = null;
				if (mob.templateId == 70)
				{
					bigBoss = new BigBoss(b, (short)mob.x, (short)mob.y, 70, mob.hp, mob.maxHp, mob.sys);
				}
				if (mob.templateId == 71)
				{
					bachTuoc = new BachTuoc(b, (short)mob.x, (short)mob.y, 71, mob.hp, mob.maxHp, mob.sys);
				}
				if (mob.templateId == 72)
				{
					bigBoss2 = new BigBoss2(b, (short)mob.x, (short)mob.y, 72, mob.hp, mob.maxHp, 3);
				}
				if (mob.isBoss)
				{
					newBoss = new NewBoss(b, (short)mob.x, (short)mob.y, mob.templateId, mob.hp, mob.maxHp, mob.sys);
				}
				if (newBoss != null)
				{
					GameScr.vMob.addElement(newBoss);
				}
				else if (bigBoss != null)
				{
					GameScr.vMob.addElement(bigBoss);
				}
				else if (bachTuoc != null)
				{
					GameScr.vMob.addElement(bachTuoc);
				}
				else if (bigBoss2 != null)
				{
					GameScr.vMob.addElement(bigBoss2);
				}
				else
				{
					GameScr.vMob.addElement(mob);
				}
			}
			if (Char.myCharz().mobMe != null && GameScr.findMobInMap(Char.myCharz().mobMe.mobId) == null)
			{
				Char.myCharz().mobMe.getData();
				Char.myCharz().mobMe.x = Char.myCharz().cx;
				Char.myCharz().mobMe.y = Char.myCharz().cy - 40;
				GameScr.vMob.addElement(Char.myCharz().mobMe);
			}
			num = msg.reader().readByte();
			for (byte b2 = 0; b2 < num; b2++)
			{
			}
			num = msg.reader().readByte();
			for (int j = 0; j < num; j++)
			{
				sbyte b3 = msg.reader().readByte();
				short cx = msg.reader().readShort();
				short num2 = msg.reader().readShort();
				sbyte b4 = msg.reader().readByte();
				short num3 = msg.reader().readShort();
				if (b4 != 6 && ((Char.myCharz().taskMaint.taskId >= 7 && (Char.myCharz().taskMaint.taskId != 7 || Char.myCharz().taskMaint.index > 1)) || (b4 != 7 && b4 != 8 && b4 != 9)) && (Char.myCharz().taskMaint.taskId >= 6 || b4 != 16))
				{
					if (b4 == 4)
					{
						GameScr.gI().magicTree = new MagicTree(j, b3, cx, num2, b4, num3);
						Service.gI().magicTree(2);
						GameScr.vNpc.addElement(GameScr.gI().magicTree);
					}
					else
					{
						Npc o = new Npc(j, b3, cx, num2 + 3, b4, num3);
						GameScr.vNpc.addElement(o);
					}
				}
			}
			num = msg.reader().readByte();
			string empty = string.Empty;
			empty = empty + "item: " + num;
			for (int k = 0; k < num; k++)
			{
				short itemMapID = msg.reader().readShort();
				short num4 = msg.reader().readShort();
				int x = msg.reader().readShort();
				int y = msg.reader().readShort();
				int num5 = msg.reader().readInt();
				short r = 0;
				if (num5 == -2)
				{
					r = msg.reader().readShort();
				}
				ItemMap itemMap = new ItemMap(num5, itemMapID, num4, x, y, r);
				bool flag = false;
				for (int l = 0; l < GameScr.vItemMap.size(); l++)
				{
					if (((ItemMap)GameScr.vItemMap.elementAt(l)).itemMapID == itemMap.itemMapID)
					{
						flag = true;
						break;
					}
				}
				if (!flag)
				{
					GameScr.vItemMap.addElement(itemMap);
				}
				empty = empty + num4 + ",";
			}
			TileMap.vCurrItem.removeAllElements();
			if (mGraphics.zoomLevel == 1)
			{
				BgItem.clearHashTable();
			}
			BgItem.vKeysNew.removeAllElements();
			if (!GameCanvas.lowGraphic || (GameCanvas.lowGraphic && TileMap.isVoDaiMap()) || TileMap.mapID == 45 || TileMap.mapID == 46 || TileMap.mapID == 47 || TileMap.mapID == 48)
			{
				short num6 = msg.reader().readShort();
				empty = "item high graphic: ";
				for (int m = 0; m < num6; m++)
				{
					short num7 = msg.reader().readShort();
					short num8 = msg.reader().readShort();
					short num9 = msg.reader().readShort();
					if (TileMap.getBIById(num7) != null)
					{
						BgItem bIById = TileMap.getBIById(num7);
						BgItem bgItem = new BgItem();
						bgItem.id = num7;
						bgItem.idImage = bIById.idImage;
						bgItem.dx = bIById.dx;
						bgItem.dy = bIById.dy;
						bgItem.x = num8 * TileMap.size;
						bgItem.y = num9 * TileMap.size;
						bgItem.layer = bIById.layer;
						if (TileMap.isExistMoreOne(bgItem.id))
						{
							bgItem.trans = ((m % 2 != 0) ? 2 : 0);
							if (TileMap.mapID == 45)
							{
								bgItem.trans = 0;
							}
						}
						Image image = null;
						if (!BgItem.imgNew.containsKey(bgItem.idImage + string.Empty))
						{
							if (mGraphics.zoomLevel == 1)
							{
								image = GameCanvas.loadImage("/mapBackGround/" + bgItem.idImage + ".png");
								if (image == null)
								{
									image = Image.createRGBImage(new int[1], 1, 1, bl: true);
									Service.gI().getBgTemplate(bgItem.idImage);
								}
								BgItem.imgNew.put(bgItem.idImage + string.Empty, image);
							}
							else
							{
								bool flag2 = false;
								sbyte[] array = Rms.loadRMS(mGraphics.zoomLevel + "bgItem" + bgItem.idImage);
								if (array != null)
								{
									if (BgItem.newSmallVersion != null && array.Length % 127 != BgItem.newSmallVersion[bgItem.idImage])
									{
										flag2 = true;
									}
									if (!flag2)
									{
										image = Image.createImage(array, 0, array.Length);
										if (image != null)
										{
											BgItem.imgNew.put(bgItem.idImage + string.Empty, image);
										}
										else
										{
											flag2 = true;
										}
									}
								}
								else
								{
									flag2 = true;
								}
								if (flag2)
								{
									image = GameCanvas.loadImage("/mapBackGround/" + bgItem.idImage + ".png");
									if (image == null)
									{
										image = Image.createRGBImage(new int[1], 1, 1, bl: true);
										Service.gI().getBgTemplate(bgItem.idImage);
									}
									BgItem.imgNew.put(bgItem.idImage + string.Empty, image);
								}
							}
							BgItem.vKeysLast.addElement(bgItem.idImage + string.Empty);
						}
						if (!BgItem.isExistKeyNews(bgItem.idImage + string.Empty))
						{
							BgItem.vKeysNew.addElement(bgItem.idImage + string.Empty);
						}
						bgItem.changeColor();
						TileMap.vCurrItem.addElement(bgItem);
					}
					empty = empty + num7 + ",";
				}
				for (int n = 0; n < BgItem.vKeysLast.size(); n++)
				{
					string text = (string)BgItem.vKeysLast.elementAt(n);
					if (!BgItem.isExistKeyNews(text))
					{
						BgItem.imgNew.remove(text);
						if (BgItem.imgNew.containsKey(text + "blend" + 1))
						{
							BgItem.imgNew.remove(text + "blend" + 1);
						}
						if (BgItem.imgNew.containsKey(text + "blend" + 3))
						{
							BgItem.imgNew.remove(text + "blend" + 3);
						}
						BgItem.vKeysLast.removeElementAt(n);
						n--;
					}
				}
				BackgroudEffect.isFog = false;
				BackgroudEffect.nCloud = 0;
				EffecMn.vEff.removeAllElements();
				BackgroudEffect.vBgEffect.removeAllElements();
				Effect.newEff.removeAllElements();
				short num10 = msg.reader().readShort();
				for (int num11 = 0; num11 < num10; num11++)
				{
					string key = msg.reader().readUTF();
					string value = msg.reader().readUTF();
					keyValueAction(key, value);
				}
			}
			else
			{
				short num12 = msg.reader().readShort();
				for (int num13 = 0; num13 < num12; num13++)
				{
					msg.reader().readShort();
					msg.reader().readShort();
					msg.reader().readShort();
				}
				short num17 = msg.reader().readShort();
				for (int num18 = 0; num18 < num17; num18++)
				{
					msg.reader().readUTF();
					msg.reader().readUTF();
				}
			}
			TileMap.bgType = msg.reader().readByte();
			sbyte teleport = msg.reader().readByte();
			loadCurrMap(teleport);
			Char.isLoadingMap = false;
			Resources.UnloadUnusedAssets();
			GC.Collect();
			ModFunc.GI().canUpdate = true;
		}
		catch (Exception)
		{
			AutoXmap.FixBlackScreen();
		}
	}
	public void LoadAuraNpcs(Message msg)
	{
		sbyte sz = msg.reader().readByte();
		for (sbyte i = 0; i < sz; i++)
		{
			sbyte tempId = msg.reader().readByte();
			short auraId = msg.reader().readShort();
			Npc npc = ModFunc.GetNpcByTempId(tempId);
			if (npc != null)
			{
				npc.idAura = auraId;
			}
		}
	}
    public void keyValueAction(string key, string value)
    {
        if (key.Equals("eff"))
        {
            if (Panel.graphics > 0)
            {
                return;
            }
            string[] array = Res.split(value, ".", 0);
            int id = int.Parse(array[0]);
            int layer = int.Parse(array[1]);
            int x = int.Parse(array[2]);
            int y = int.Parse(array[3]);
            int loop;
            int loopCount;
            if (array.Length <= 4)
            {
                loop = -1;
                loopCount = 1;
            }
            else
            {
                loop = int.Parse(array[4]);
                loopCount = int.Parse(array[5]);
            }
            Effect effect = new Effect(id, x, y, layer, loop, loopCount);
            if (array.Length > 6)
            {
                effect.typeEff = int.Parse(array[6]);
                if (array.Length > 7)
                {
                    effect.indexFrom = int.Parse(array[7]);
                    effect.indexTo = int.Parse(array[8]);
                }
            }
            EffecMn.addEff(effect);
        }
        else if (key.Equals("beff") && Panel.graphics <= 1)
        {
            BackgroudEffect.addEffect(int.Parse(value));
        }
    }

    public void messageNotMap(Message msg)
    {
        GameCanvas.debug("SA6", 2);
        try
        {
            sbyte b = msg.reader().readByte();
            Res.outz("---messageNotMap : " + b);
            switch (b)
            {
                case 16:
                    MoneyCharge.gI().switchToMe();
                    break;
                case 17:
                    GameCanvas.debug("SYB123", 2);
                    Char.myCharz().clearTask();
                    break;
                case 18:
                    {
                        GameCanvas.isLoading = false;
                        GameCanvas.endDlg();
                        int num2 = msg.reader().readInt();
                        GameCanvas.inputDlg.show(mResources.changeNameChar, new Command(mResources.OK, GameCanvas.instance, 88829, num2), TField.INPUT_TYPE_ANY);
                        break;
                    }
                case 20:
                    Char.myCharz().cPk = msg.reader().readByte();
                    GameScr.info1.addInfo(mResources.PK_NOW + " " + Char.myCharz().cPk, 0);
                    break;
                case 35:
                    GameCanvas.endDlg();
                    GameScr.gI().resetButton();
                    GameScr.info1.addInfo(msg.reader().readUTF(), 0);
                    break;
                case 36:
                    GameScr.typeActive = msg.reader().readByte();
                    Res.outz("load Me Active: " + GameScr.typeActive);
                    break;
                case 4:
                    {
                        GameCanvas.debug("SA8", 2);
                        GameCanvas.loginScr.savePass();
                        GameScr.isAutoPlay = false;
                        GameScr.canAutoPlay = false;
                        LoginScr.isUpdateAll = true;
                        LoginScr.isUpdateData = true;
                        LoginScr.isUpdateMap = true;
                        LoginScr.isUpdateSkill = true;
                        LoginScr.isUpdateItem = true;
                        GameScr.vsData = msg.reader().readByte();
                        GameScr.vsMap = msg.reader().readByte();
                        GameScr.vsSkill = msg.reader().readByte();
                        GameScr.vsItem = msg.reader().readByte();
                        sbyte b3 = msg.reader().readByte();
                        if (GameCanvas.loginScr.isLogin2)
                        {
                            Rms.saveRMSString("acc", string.Empty);
                            Rms.saveRMSString("pass", string.Empty);
                        }
                        else
                        {
                            Rms.saveRMSString("userAo" + ServerListScreen.ipSelect, string.Empty);
                        }
                        if (GameScr.vsData != GameScr.vcData)
                        {
                            GameScr.isLoadAllData = false;
                            Service.gI().updateData();
                        }
                        else
                        {
                            try
                            {
                                LoginScr.isUpdateData = false;
                            }
                            catch (Exception)
                            {
                                GameScr.vcData = -1;
                                Service.gI().updateData();
                            }
                        }
                        if (GameScr.vsMap != GameScr.vcMap)
                        {
                            GameScr.isLoadAllData = false;
                            Service.gI().updateMap();
                        }
                        else
                        {
                            try
                            {
                                if (!GameScr.isLoadAllData)
                                {
                                    DataInputStream dataInputStream = new DataInputStream(Rms.loadRMS("NRmap"));
                                    createMap(dataInputStream.r);
                                }
                                LoginScr.isUpdateMap = false;
                            }
                            catch (Exception)
                            {
                                GameScr.vcMap = -1;
                                Service.gI().updateMap();
                            }
                        }
                        if (GameScr.vsSkill != GameScr.vcSkill)
                        {
                            GameScr.isLoadAllData = false;
                            Service.gI().updateSkill();
                        }
                        else
                        {
                            try
                            {
                                if (!GameScr.isLoadAllData)
                                {
                                    DataInputStream dataInputStream2 = new DataInputStream(Rms.loadRMS("NRskill"));
                                    createSkill(dataInputStream2.r);
                                }
                                LoginScr.isUpdateSkill = false;
                            }
                            catch (Exception)
                            {
                                GameScr.vcSkill = -1;
                                Service.gI().updateSkill();
                            }
                        }
                        if (GameScr.vsItem != GameScr.vcItem)
                        {
                            GameScr.isLoadAllData = false;
                            Service.gI().updateItem();
                        }
                        else
                        {
                            try
                            {
                                DataInputStream dataInputStream3 = new DataInputStream(Rms.loadRMS("NRitem0"));
                                loadItemNew(dataInputStream3.r, 0, false);
                                DataInputStream dataInputStream4 = new DataInputStream(Rms.loadRMS("NRitem1"));
                                loadItemNew(dataInputStream4.r, 1, false);
                                DataInputStream dataInputStream5 = new DataInputStream(Rms.loadRMS("NRitem2"));
                                loadItemNew(dataInputStream5.r, 2, false);
                                DataInputStream dataInputStream6 = new DataInputStream(Rms.loadRMS("NRitem100"));
                                loadItemNew(dataInputStream6.r, 100, false);
                                LoginScr.isUpdateItem = false;
                            }
                            catch (Exception)
                            {
                                GameScr.vcItem = -1;
                                Service.gI().updateItem();
                            }
                            try
                            {
                                DataInputStream dataInputStream7 = new DataInputStream(Rms.loadRMS("NRitem101"));
                                loadItemNew(dataInputStream7.r, 101, false);
                            }
                            catch (Exception)
                            {
                            }
                        }
                        if (GameScr.vsData == GameScr.vcData && GameScr.vsMap == GameScr.vcMap && GameScr.vsSkill == GameScr.vcSkill && GameScr.vsItem == GameScr.vcItem)
                        {
                            if (!GameScr.isLoadAllData)
                            {
                                GameScr.gI().readDart();
                                GameScr.gI().readEfect();
                                GameScr.gI().readArrow();
                                GameScr.gI().readSkill();
                            }
                            Service.gI().clientOk();
                        }
                        sbyte b4 = msg.reader().readByte();
                        Res.outz("CAPTION LENT= " + b4);
                        GameScr.exps = new long[b4];
                        for (int j = 0; j < GameScr.exps.Length; j++)
                        {
                            GameScr.exps[j] = msg.reader().readLong();
                        }
                        break;
                    }
                case 6:
                    {
                        Res.outz("GET UPDATE_MAP " + msg.reader().available() + " bytes");
                        msg.reader().mark(100000);
                        createMap(msg.reader());
                        msg.reader().reset();
                        sbyte[] data3 = new sbyte[msg.reader().available()];
                        msg.reader().readFully(ref data3);
                        Rms.saveRMS("NRmap", data3);
                        sbyte[] data4 = new sbyte[1] { GameScr.vcMap };
                        Rms.saveRMS("NRmapVersion", data4);
                        LoginScr.isUpdateMap = false;
                        if (GameScr.vsData == GameScr.vcData && GameScr.vsMap == GameScr.vcMap && GameScr.vsSkill == GameScr.vcSkill && GameScr.vsItem == GameScr.vcItem)
                        {
                            GameScr.gI().readDart();
                            GameScr.gI().readEfect();
                            GameScr.gI().readArrow();
                            GameScr.gI().readSkill();
                            Service.gI().clientOk();
                        }
                        break;
                    }
                case 7:
                    {
                        Res.outz("GET UPDATE_SKILL " + msg.reader().available() + " bytes");
                        msg.reader().mark(100000);
                        createSkill(msg.reader());
                        msg.reader().reset();
                        sbyte[] data = new sbyte[msg.reader().available()];
                        msg.reader().readFully(ref data);
                        Rms.saveRMS("NRskill", data);
                        sbyte[] data2 = new sbyte[1] { GameScr.vcSkill };
                        Rms.saveRMS("NRskillVersion", data2);
                        LoginScr.isUpdateSkill = false;
                        if (GameScr.vsData == GameScr.vcData && GameScr.vsMap == GameScr.vcMap && GameScr.vsSkill == GameScr.vcSkill && GameScr.vsItem == GameScr.vcItem)
                        {
                            GameScr.gI().readDart();
                            GameScr.gI().readEfect();
                            GameScr.gI().readArrow();
                            GameScr.gI().readSkill();
                            Service.gI().clientOk();
                        }
                        break;
                    }
                case 8:
                    Res.outz("GET UPDATE_ITEM " + msg.reader().available() + " bytes");
                    createItemNew(msg.reader());
                    break;
                case 10:
                    try
                    {
                        Char.isLoadingMap = true;
                        Res.outz("REQUEST MAP TEMPLATE");
                        GameCanvas.isLoading = true;
                        TileMap.maps = null;
                        TileMap.types = null;
                        mSystem.gcc();
                        GameCanvas.debug("SA99", 2);
                        TileMap.tmw = msg.reader().readByte();
                        TileMap.tmh = msg.reader().readByte();
                        TileMap.maps = new int[TileMap.tmw * TileMap.tmh];
                        Res.err("   M apsize= " + TileMap.tmw * TileMap.tmh);
                        for (int i = 0; i < TileMap.maps.Length; i++)
                        {
                            int num = msg.reader().readByte();
                            if (num < 0)
                            {
                                num += 256;
                            }
                            TileMap.maps[i] = (ushort)num;
                        }
                        TileMap.types = new int[TileMap.maps.Length];
                        msg = messWait;
                        loadInfoMap(msg);
                        try
                        {
                            sbyte b2 = msg.reader().readByte();
                            TileMap.isMapDouble = ((b2 != 0) ? true : false);
                        }
                        catch (Exception ex)
                        {
                            Res.err(" 1 LOI TAI CASE REQUEST_MAPTEMPLATE " + ex.ToString());
                        }
                    }
                    catch (Exception ex2)
                    {
                        Res.err("2 LOI TAI CASE REQUEST_MAPTEMPLATE " + ex2.ToString());
                    }
                    msg.cleanup();
                    messWait.cleanup();
                    msg = (messWait = null);
                    GameScr.gI().switchToMe();
                    break;
                case 12:
                    GameCanvas.debug("SA10", 2);
                    break;
                case 9:
                    GameCanvas.debug("SA11", 2);
                    break;
            }
        }
        catch (Exception)
        {
            Cout.LogError("LOI TAI messageNotMap + " + msg.command);
        }
        finally
        {
            if (msg != null)
            {
                msg.cleanup();
            }
        }
    }

   public void messageNotLogin(Message msg)
	{
		try
		{
			if (msg.reader().readByte() != 2)
			{
				return;
			}
			string linkDefault = msg.reader().readUTF();
			if (ServerListScreen.isMultiSever)
			{
				linkDefault = ServerListScreen.ListIP + linkDefault;
			}
			if (Rms.loadRMSInt("AdminLink") == 1)
			{
				return;
			}
			if (mSystem.clientType == 1)
			{
				ServerListScreen.linkDefault = linkDefault;
			}
			else
			{
				ServerListScreen.linkDefault = linkDefault;
			}
			mSystem.AddIpTest();
			ServerListScreen.GetServerList(ServerListScreen.linkDefault);
			try
			{
				Panel.CanNapTien = msg.reader().readByte() == 1;
				sbyte b3 = msg.reader().readByte();
				Rms.saveRMSInt("AdminLink", b3);
			}
			catch (Exception)
			{
			}
		}
		catch (Exception)
		{
		}
		finally
		{
			msg?.cleanup();
		}
	}


   public void messageSubCommand(Message msg)
	{
		try
		{
			switch (msg.reader().readByte())
			{
			case 63:
			{
				sbyte b5 = msg.reader().readByte();
				if (b5 > 0)
				{
					GameCanvas.panel.vPlayerMenu_id.removeAllElements();
					InfoDlg.showWait();
					MyVector vPlayerMenu = GameCanvas.panel.vPlayerMenu;
					for (int j = 0; j < b5; j++)
					{
						string caption = msg.reader().readUTF();
						string caption2 = msg.reader().readUTF();
						short num6 = msg.reader().readShort();
						GameCanvas.panel.vPlayerMenu_id.addElement(num6 + string.Empty);
						Char.myCharz().charFocus.menuSelect = num6;
						Command command = new Command(caption, 11115, Char.myCharz().charFocus);
						command.caption2 = caption2;
						vPlayerMenu.addElement(command);
					}
					InfoDlg.hide();
					GameCanvas.panel.setTabPlayerMenu();
				}
				break;
			}
			case 1:
				GameCanvas.debug("SA13", 2);
				Char.myCharz().nClass = GameScr.nClasss[msg.reader().readByte()];
				Char.myCharz().cTiemNang = msg.reader().readLong();
				Char.myCharz().vSkill.removeAllElements();
				Char.myCharz().vSkillFight.removeAllElements();
				Char.myCharz().myskill = null;
				break;
			case 2:
			{
				GameCanvas.debug("SA14", 2);
				if (Char.myCharz().statusMe != 14 && Char.myCharz().statusMe != 5)
				{
					Char.myCharz().cHP = Char.myCharz().cHPFull;
					Char.myCharz().cMP = Char.myCharz().cMPFull;
					Cout.LogError2(" ME_LOAD_SKILL");
				}
				Char.myCharz().vSkill.removeAllElements();
				Char.myCharz().vSkillFight.removeAllElements();
				sbyte b2 = msg.reader().readByte();
				for (sbyte b3 = 0; b3 < b2; b3++)
				{
					Skill skill2 = Skills.get(msg.reader().readShort());
					useSkill(skill2);
				}
				GameScr.gI().sortSkill();
				if (GameScr.isPaintInfoMe)
				{
					GameScr.indexRow = -1;
					GameScr.gI().left = (GameScr.gI().center = null);
				}
				break;
			}
			case 19:
				GameCanvas.debug("SA17", 2);
				Char.myCharz().boxSort();
				break;
			case 21:
			{
				int num5 = msg.reader().readInt();
				Char.myCharz().xuInBox -= num5;
				Char.myCharz().xu += num5;
				Char.myCharz().xuStr = mSystem.numberTostring(Char.myCharz().xu);
				break;
			}
			case 0:
				try
				{
					RadarScr.list = new MyVector();
					Teleport.vTeleport.removeAllElements();
					GameScr.vCharInMap.removeAllElements();
					GameScr.vItemMap.removeAllElements();
					Char.vItemTime.removeAllElements();
					GameScr.loadImg();
					GameScr.currentCharViewInfo = Char.myCharz();
					Char.myCharz().charID = msg.reader().readInt();
					Char.myCharz().ctaskId = msg.reader().readByte();
					Char.myCharz().cgender = msg.reader().readByte();
					Char.myCharz().head = msg.reader().readShort();
					Char.myCharz().cName = msg.reader().readUTF();
					Char.myCharz().cPk = msg.reader().readByte();
					Char.myCharz().cTypePk = msg.reader().readByte();
					Char.myCharz().cPower = msg.reader().readLong();
					Char.myCharz().applyCharLevelPercent();
					Char.myCharz().eff5BuffHp = msg.reader().readShort();
					Char.myCharz().eff5BuffMp = msg.reader().readShort();
					Char.myCharz().nClass = GameScr.nClasss[msg.reader().readByte()];
					Char.myCharz().vSkill.removeAllElements();
					Char.myCharz().vSkillFight.removeAllElements();
					GameScr.gI().dHP = Char.myCharz().cHP;
					GameScr.gI().dMP = Char.myCharz().cMP;
					sbyte b6 = msg.reader().readByte();
					for (sbyte b7 = 0; b7 < b6; b7++)
					{
						Skill skill3 = Skills.get(msg.reader().readShort());
						useSkill(skill3);
					}
					GameScr.gI().sortSkill();
					GameScr.gI().loadSkillShortcut();
					Char.myCharz().xu = msg.reader().readLong();
					Char.myCharz().luongKhoa = msg.reader().readInt();
					Char.myCharz().luong = msg.reader().readInt();
					Char.myCharz().xuStr = mSystem.numberTostring(Char.myCharz().xu);
					Char.myCharz().luongStr = mSystem.numberTostring(Char.myCharz().luong);
					Char.myCharz().luongKhoaStr = mSystem.numberTostring(Char.myCharz().luongKhoa);
					Char.myCharz().arrItemBody = new Item[msg.reader().readByte()];
					try
					{
						Char.myCharz().setDefaultPart();
						for (int k = 0; k < Char.myCharz().arrItemBody.Length; k++)
						{
							short num7 = msg.reader().readShort();
							if (num7 == -1)
							{
								continue;
							}
							ItemTemplate itemTemplate = ItemTemplates.get(num7);
							int num8 = itemTemplate.type;
							Char.myCharz().arrItemBody[k] = new Item();
							Char.myCharz().arrItemBody[k].template = itemTemplate;
							Char.myCharz().arrItemBody[k].quantity = msg.reader().readInt();
							Char.myCharz().arrItemBody[k].info = msg.reader().readUTF();
							Char.myCharz().arrItemBody[k].content = msg.reader().readUTF();
							int num9 = msg.reader().readUnsignedByte();
							if (num9 != 0)
							{
								Char.myCharz().arrItemBody[k].itemOption = new ItemOption[num9];
								for (int l = 0; l < Char.myCharz().arrItemBody[k].itemOption.Length; l++)
								{
									int num10 = msg.reader().readShortOptionTemp();
									int param = msg.reader().ReadParamDQT();
									if (num10 != -1)
									{
										Char.myCharz().arrItemBody[k].itemOption[l] = new ItemOption(num10, param);
									}
								}
							}
							switch (num8)
							{
							case 0:
								Char.myCharz().body = Char.myCharz().arrItemBody[k].template.part;
								break;
							case 1:
								Char.myCharz().leg = Char.myCharz().arrItemBody[k].template.part;
								break;
							}
						}
					}
					catch (Exception exception)
					{
						Debug.LogException(exception);
					}
					Char.myCharz().arrItemBag = new Item[msg.reader().readByte()];
					GameScr.hpPotion = 0;
					for (int m = 0; m < Char.myCharz().arrItemBag.Length; m++)
					{
						short num11 = msg.reader().readShort();
						if (num11 == -1)
						{
							continue;
						}
						Char.myCharz().arrItemBag[m] = new Item();
						Char.myCharz().arrItemBag[m].template = ItemTemplates.get(num11);
						Char.myCharz().arrItemBag[m].quantity = msg.reader().readInt();
						Char.myCharz().arrItemBag[m].info = msg.reader().readUTF();
						Char.myCharz().arrItemBag[m].content = msg.reader().readUTF();
						Char.myCharz().arrItemBag[m].indexUI = m;
						sbyte b8 = msg.reader().readByte();
						if (b8 != 0)
						{
							Char.myCharz().arrItemBag[m].itemOption = new ItemOption[b8];
							for (int n = 0; n < Char.myCharz().arrItemBag[m].itemOption.Length; n++)
							{
								int num12 = msg.reader().readShortOptionTemp();
								int param2 = msg.reader().ReadParamDQT();
								if (num12 != -1)
								{
									Char.myCharz().arrItemBag[m].itemOption[n] = new ItemOption(num12, param2);
									Char.myCharz().arrItemBag[m].getCompare();
								}
							}
						}
						if (Char.myCharz().arrItemBag[m].template.type == 6)
						{
							GameScr.hpPotion += Char.myCharz().arrItemBag[m].quantity;
						}
					}
					Char.myCharz().arrItemBox = new Item[msg.reader().readByte()];
					GameCanvas.panel.hasUse = 0;
					for (int num13 = 0; num13 < Char.myCharz().arrItemBox.Length; num13++)
					{
						short num14 = msg.reader().readShort();
						if (num14 == -1)
						{
							continue;
						}
						Char.myCharz().arrItemBox[num13] = new Item();
						Char.myCharz().arrItemBox[num13].template = ItemTemplates.get(num14);
						Char.myCharz().arrItemBox[num13].quantity = msg.reader().readInt();
						Char.myCharz().arrItemBox[num13].info = msg.reader().readUTF();
						Char.myCharz().arrItemBox[num13].content = msg.reader().readUTF();
						Char.myCharz().arrItemBox[num13].itemOption = new ItemOption[msg.reader().readByte()];
						for (int num15 = 0; num15 < Char.myCharz().arrItemBox[num13].itemOption.Length; num15++)
						{
							int num16 = msg.reader().readShortOptionTemp();
							int param3 = msg.reader().ReadParamDQT();
							if (num16 != -1)
							{
								Char.myCharz().arrItemBox[num13].itemOption[num15] = new ItemOption(num16, param3);
								Char.myCharz().arrItemBox[num13].getCompare();
							}
						}
						GameCanvas.panel.hasUse++;
					}
					Char.myCharz().statusMe = 4;
					if (Rms.loadRMSInt(Char.myCharz().cName + "vci") < 1)
					{
						GameScr.isViewClanInvite = false;
					}
					else
					{
						GameScr.isViewClanInvite = true;
					}
					short num17 = msg.reader().readShort();
					Char.idHead = new short[num17];
					Char.idAvatar = new short[num17];
					for (int num18 = 0; num18 < num17; num18++)
					{
						Char.idHead[num18] = msg.reader().readShort();
						Char.idAvatar[num18] = msg.reader().readShort();
					}
					for (int num19 = 0; num19 < GameScr.info1.charId.Length; num19++)
					{
						GameScr.info1.charId[num19] = new int[3];
					}
					GameScr.info1.charId[Char.myCharz().cgender][0] = msg.reader().readShort();
					GameScr.info1.charId[Char.myCharz().cgender][1] = msg.reader().readShort();
					GameScr.info1.charId[Char.myCharz().cgender][2] = msg.reader().readShort();
					Char.myCharz().isNhapThe = msg.reader().readByte() == 1;
					GameScr.deltaTime = mSystem.currentTimeMillis() - (long)msg.reader().readInt() * 1000L;
					GameScr.isNewMember = msg.reader().readByte();
					Char.myCharz().isTichXanh = GameScr.isNewMember == 1;
					Service.gI().updateCaption((sbyte)Char.myCharz().cgender);
					Service.gI().androidPack();
					try
					{
						Char.myCharz().idAuraEff = msg.reader().readShort();
						Char.myCharz().idEff_Set_Item = msg.reader().readSByte();
						Char.myCharz().idHat = msg.reader().readShort();
						break;
					}
					catch (Exception)
					{
						break;
					}
				}
				catch (Exception exception2)
				{
					Debug.LogException(exception2);
					break;
				}
			case 4:
				Char.myCharz().xu = msg.reader().readLong();
				Char.myCharz().luong = msg.reader().readInt();
				Char.myCharz().cHP = msg.reader().readIntToLongDQT();
				Char.myCharz().cMP = msg.reader().readIntToLongDQT();
				Char.myCharz().luongKhoa = msg.reader().readInt();
				Char.myCharz().xuStr = mSystem.numberTostring(Char.myCharz().xu);
				Char.myCharz().luongStr = mSystem.numberTostring(Char.myCharz().luong);
				Char.myCharz().luongKhoaStr = mSystem.numberTostring(Char.myCharz().luongKhoa);
				break;
			case 5:
			{
				double cHP = Char.myCharz().cHP;
				Char.myCharz().cHP = msg.reader().readIntToLongDQT();
				if (Char.myCharz().cHP > cHP && Char.myCharz().cTypePk != 4)
				{
					GameScr.startFlyText("+" + (Char.myCharz().cHP - cHP) + " " + mResources.HP, Char.myCharz().cx, Char.myCharz().cy - Char.myCharz().ch - 20, 0, -1, mFont.HP);
					SoundMn.gI().HP_MPup();
					if (Char.myCharz().petFollow != null && Char.myCharz().petFollow.smallID == 5003)
					{
						MonsterDart.addMonsterDart(Char.myCharz().petFollow.cmx + ((Char.myCharz().petFollow.dir != 1) ? (-10) : 10), Char.myCharz().petFollow.cmy + 10, isBoss: true, -1.0, -1.0, Char.myCharz(), 29);
					}
				}
				if (Char.myCharz().cHP < cHP)
				{
					GameScr.startFlyText("-" + (cHP - Char.myCharz().cHP) + " " + mResources.HP, Char.myCharz().cx, Char.myCharz().cy - Char.myCharz().ch - 20, 0, -1, mFont.HP);
				}
				GameScr.gI().dHP = Char.myCharz().cHP;
				if (!GameScr.isPaintInfoMe)
				{
				}
				break;
			}
			case 6:
			{
				if (Char.myCharz().statusMe == 14 || Char.myCharz().statusMe == 5)
				{
					break;
				}
				double cMP = Char.myCharz().cMP;
				Char.myCharz().cMP = msg.reader().readIntToLongDQT();
				if (Char.myCharz().cMP > cMP)
				{
					GameScr.startFlyText("+" + (Char.myCharz().cMP - cMP) + " " + mResources.KI, Char.myCharz().cx, Char.myCharz().cy - Char.myCharz().ch - 23, 0, -2, mFont.MP);
					SoundMn.gI().HP_MPup();
					if (Char.myCharz().petFollow != null && Char.myCharz().petFollow.smallID == 5001)
					{
						MonsterDart.addMonsterDart(Char.myCharz().petFollow.cmx + ((Char.myCharz().petFollow.dir != 1) ? (-10) : 10), Char.myCharz().petFollow.cmy + 10, isBoss: true, -1.0, -1.0, Char.myCharz(), 29);
					}
				}
				if (Char.myCharz().cMP < cMP)
				{
					GameScr.startFlyText("-" + (cMP - Char.myCharz().cMP) + " " + mResources.KI, Char.myCharz().cx, Char.myCharz().cy - Char.myCharz().ch - 23, 0, -2, mFont.MP);
				}
				GameScr.gI().dMP = Char.myCharz().cMP;
				if (!GameScr.isPaintInfoMe)
				{
				}
				break;
			}
			case 7:
			{
				Char char9 = GameScr.findCharInMap(msg.reader().readInt());
				if (char9 == null)
				{
					break;
				}
				char9.clanID = msg.reader().readInt();
				if (char9.clanID == -2)
				{
					char9.isCopy = true;
				}
				readCharInfo(char9, msg);
				try
				{
					char9.idAuraEff = msg.reader().readShort();
					char9.idEff_Set_Item = msg.reader().readSByte();
					char9.idHat = msg.reader().readShort();
					if (char9.bag >= 201)
					{
						Effect effect = new Effect(char9.bag, char9, 2, -1, 10, 1);
						effect.typeEff = 5;
						char9.addEffChar(effect);
					}
					else
					{
						char9.removeEffChar(0, 201);
					}
					break;
				}
				catch (Exception)
				{
					break;
				}
			}
			case 8:
			{
				GameCanvas.debug("SA26", 2);
				Char char10 = GameScr.findCharInMap(msg.reader().readInt());
				if (char10 != null)
				{
					char10.cspeed = msg.reader().readByte();
				}
				break;
			}
			case 9:
			{
				GameCanvas.debug("SA27", 2);
				Char char8 = GameScr.findCharInMap(msg.reader().readInt());
				if (char8 != null)
				{
					char8.cHP = msg.reader().readIntToLongDQT();
					char8.cHPFull = msg.reader().readIntToLongDQT();
				}
				break;
			}
			case 10:
			{
				GameCanvas.debug("SA28", 2);
				Char char5 = GameScr.findCharInMap(msg.reader().readInt());
				if (char5 != null)
				{
					char5.cHP = msg.reader().readIntToLongDQT();
					char5.cHPFull = msg.reader().readIntToLongDQT();
					char5.eff5BuffHp = msg.reader().readShort();
					char5.eff5BuffMp = msg.reader().readShort();
					char5.wp = msg.reader().readShort();
					if (char5.wp == -1)
					{
						char5.setDefaultWeapon();
					}
				}
				break;
			}
			case 11:
			{
				GameCanvas.debug("SA29", 2);
				Char char2 = GameScr.findCharInMap(msg.reader().readInt());
				if (char2 != null)
				{
					char2.cHP = msg.reader().readIntToLongDQT();
					char2.cHPFull = msg.reader().readIntToLongDQT();
					char2.eff5BuffHp = msg.reader().readShort();
					char2.eff5BuffMp = msg.reader().readShort();
					char2.body = msg.reader().readShort();
					if (char2.body == -1)
					{
						char2.setDefaultBody();
					}
				}
				break;
			}
			case 12:
			{
				GameCanvas.debug("SA30", 2);
				Char char11 = GameScr.findCharInMap(msg.reader().readInt());
				if (char11 != null)
				{
					char11.cHP = msg.reader().readIntToLongDQT();
					char11.cHPFull = msg.reader().readIntToLongDQT();
					char11.eff5BuffHp = msg.reader().readShort();
					char11.eff5BuffMp = msg.reader().readShort();
					char11.leg = msg.reader().readShort();
					if (char11.leg == -1)
					{
						char11.setDefaultLeg();
					}
				}
				break;
			}
			case 13:
			{
				GameCanvas.debug("SA31", 2);
				int num2 = msg.reader().readInt();
				Char @char = ((num2 != Char.myCharz().charID) ? GameScr.findCharInMap(num2) : Char.myCharz());
				if (@char != null)
				{
					@char.cHP = msg.reader().readIntToLongDQT();
					@char.cHPFull = msg.reader().readIntToLongDQT();
					@char.eff5BuffHp = msg.reader().readShort();
					@char.eff5BuffMp = msg.reader().readShort();
				}
				break;
			}
			case 14:
			{
				Char char4 = GameScr.findCharInMap(msg.reader().readInt());
				if (char4 != null)
				{
					char4.cHP = msg.reader().readIntToLongDQT();
					sbyte num3 = msg.reader().readByte();
					if (num3 == 1)
					{
						ServerEffect.addServerEffect(11, char4, 5);
						ServerEffect.addServerEffect(104, char4, 4);
					}
					if (num3 == 2)
					{
						char4.doInjure();
					}
					try
					{
						char4.cHPFull = msg.reader().readIntToLongDQT();
						break;
					}
					catch (Exception)
					{
						break;
					}
				}
				break;
			}
			case 15:
			{
				Char char3 = GameScr.findCharInMap(msg.reader().readInt());
				if (char3 != null)
				{
					char3.cHP = msg.reader().readIntToLongDQT();
					char3.cHPFull = msg.reader().readIntToLongDQT();
					char3.cx = msg.reader().readShort();
					char3.cy = msg.reader().readShort();
					char3.statusMe = 1;
					char3.cp3 = 3;
					ServerEffect.addServerEffect(109, char3, 2);
				}
				break;
			}
			case 35:
			{
				GameCanvas.debug("SY3", 2);
				int num4 = msg.reader().readInt();
				Res.outz("CID = " + num4);
				if (TileMap.mapID == 130)
				{
					GameScr.gI().starVS();
				}
				if (num4 == Char.myCharz().charID)
				{
					Char.myCharz().cTypePk = msg.reader().readByte();
					if (GameScr.gI().isVS() && Char.myCharz().cTypePk != 0)
					{
						GameScr.gI().starVS();
					}
					Res.outz("type pk= " + Char.myCharz().cTypePk);
					Char.myCharz().npcFocus = null;
					if (!GameScr.gI().isMeCanAttackMob(Char.myCharz().mobFocus))
					{
						Char.myCharz().mobFocus = null;
					}
					Char.myCharz().itemFocus = null;
				}
				else
				{
					Char char6 = GameScr.findCharInMap(num4);
					if (char6 != null)
					{
						Res.outz("type pk= " + char6.cTypePk);
						char6.cTypePk = msg.reader().readByte();
						if (char6.isAttacPlayerStatus())
						{
							Char.myCharz().charFocus = char6;
						}
					}
				}
				for (int i = 0; i < GameScr.vCharInMap.size(); i++)
				{
					Char char7 = GameScr.findCharInMap(i);
					if (char7 != null && char7.cTypePk != 0 && char7.cTypePk == Char.myCharz().cTypePk)
					{
						if (!Char.myCharz().mobFocus.isMobMe)
						{
							Char.myCharz().mobFocus = null;
						}
						Char.myCharz().npcFocus = null;
						Char.myCharz().itemFocus = null;
						break;
					}
				}
				Res.outz("update type pk= ");
				break;
			}
			case 61:
			{
				string text = msg.reader().readUTF();
				sbyte[] data = new sbyte[msg.reader().readInt()];
				msg.reader().read(ref data);
				if (data.Length == 0)
				{
					data = null;
				}
				if (text.Equals("KSkill"))
				{
					GameScr.gI().onKSkill(data);
				}
				else if (text.Equals("OSkill"))
				{
					GameScr.gI().onOSkill(data);
				}
				else if (text.Equals("CSkill"))
				{
					GameScr.gI().onCSkill(data);
				}
				break;
			}
			case 23:
			{
				short num = msg.reader().readShort();
				Skill skill = Skills.get(num);
				useSkill(skill);
				if (num != 0 && num != 14 && num != 28)
				{
					GameScr.info1.addInfo(mResources.LEARN_SKILL + " " + skill.template.name, 0);
				}
				break;
			}
			case 62:
				Res.outz("ME UPDATE SKILL");
				read_UpdateSkill(msg);
				break;
			}
		}
		catch (Exception ex4)
		{
			Cout.println("Loi tai Sub : " + ex4.ToString());
		}
		finally
		{
			msg?.cleanup();
		}
	}

    private void useSkill(Skill skill)
    {
        if (Char.myCharz().myskill == null)
        {
            Char.myCharz().myskill = skill;
        }
        else if (skill.template.Equals(Char.myCharz().myskill.template))
        {
            Char.myCharz().myskill = skill;
        }
        Char.myCharz().vSkill.addElement(skill);
        if ((skill.template.type == 1 || skill.template.type == 4 || skill.template.type == 2 || skill.template.type == 3) && (skill.template.maxPoint == 0 || (skill.template.maxPoint > 0 && skill.point > 0)))
        {
            if (skill.template.id == Char.myCharz().skillTemplateId)
            {
                Service.gI().selectSkill(Char.myCharz().skillTemplateId);
            }
            Char.myCharz().vSkillFight.addElement(skill);
        }
    }

    public bool readCharInfo(Char c, Message msg)
    {
        try
        {
            c.clevel = msg.reader().readByte();
            c.isInvisiblez = msg.reader().readBoolean();
            c.cTypePk = msg.reader().readByte();
            Res.outz("ADD TYPE PK= " + c.cTypePk + " to player " + c.charID + " @@ " + c.cName);
            c.nClass = GameScr.nClasss[msg.reader().readByte()];
            c.cgender = msg.reader().readByte();
            c.head = msg.reader().readShort();
            c.cName = msg.reader().readUTF();
            c.cHP = msg.reader().readIntToLongDQT();
            c.dHP = c.cHP;
            if (c.cHP == 0)
            {
                c.statusMe = 14;
            }
            c.cHPFull = msg.reader().readIntToLongDQT();
            if (c.cy >= TileMap.pxh - 100)
            {
                c.isFlyUp = true;
            }
            c.body = msg.reader().readShort();
            c.leg = msg.reader().readShort();
            c.bag = msg.reader().readUnsignedByte();
            Res.outz(" body= " + c.body + " leg= " + c.leg + " bag=" + c.bag + "BAG ==" + c.bag + "*********************************");
            c.isShadown = true;
            sbyte b = msg.reader().readByte();
            if (c.wp == -1)
            {
                c.setDefaultWeapon();
            }
            if (c.body == -1)
            {
                c.setDefaultBody();
            }
            if (c.leg == -1)
            {
                c.setDefaultLeg();
            }
            c.cx = msg.reader().readShort();
            c.cy = msg.reader().readShort();
            c.xSd = c.cx;
            c.ySd = c.cy;
            c.eff5BuffHp = msg.reader().readShort();
            c.eff5BuffMp = msg.reader().readShort();
            int num = msg.reader().readByte();
            for (int i = 0; i < num; i++)
            {
                EffectChar effectChar = new EffectChar(msg.reader().readByte(), msg.reader().readInt(), msg.reader().readInt(), msg.reader().readShort());
                c.vEff.addElement(effectChar);
                if (effectChar.template.type == 12 || effectChar.template.type == 11)
                {
                    c.isInvisiblez = true;
                }
            }
            return true;
        }
        catch (Exception ex)
        {
            ex.StackTrace.ToString();
        }
        return false;
    }

    private void readGetImgByName(Message msg)
    {
        try
        {
            string name = msg.reader().readUTF();
            sbyte nFrame = msg.reader().readByte();
            sbyte[] array = null;
            array = NinjaUtil.readByteArray(msg);
            Image img = createImage(array);
            ImgByName.SetImage(name, img, nFrame);
            if (array == null)
            {
            }
        }
        catch (Exception)
        {
        }
    }

    private void createItemNew(myReader d)
    {
        try
        {
            loadItemNew(d, -1, true);
        }
        catch (Exception)
        {
        }
    }

   private void loadItemNew(myReader d, sbyte type, bool isSave)
	{
		try
		{
			d.mark(100000);
			GameScr.vcItem = d.readByte();
			type = d.readByte();
			switch (type)
			{
			case 0:
			{
				GameScr.gI().iOptionTemplates = new ItemOptionTemplate[d.readShortOptionTemp()];
				for (int i = 0; i < GameScr.gI().iOptionTemplates.Length; i++)
				{
					GameScr.gI().iOptionTemplates[i] = new ItemOptionTemplate();
					GameScr.gI().iOptionTemplates[i].id = i;
					GameScr.gI().iOptionTemplates[i].name = d.readUTF();
					GameScr.gI().iOptionTemplates[i].type = d.readByte();
				}
				if (isSave)
				{
					d.reset();
					sbyte[] data9 = new sbyte[d.available()];
					d.readFully(ref data9);
					Rms.saveRMS("NRitem0", data9);
				}
				break;
			}
			case 1:
			{
				ItemTemplates.itemTemplates.clear();
				int num = d.readShort();
				for (int j = 0; j < num; j++)
				{
					ItemTemplates.add(new ItemTemplate((short)j, d.readByte(), d.readByte(), d.readUTF(), d.readUTF(), d.readByte(), d.readInt(), d.readShort(), d.readShort(), d.readBoolean()));
				}
				if (isSave)
				{
					d.reset();
					sbyte[] data6 = new sbyte[d.available()];
					d.readFully(ref data6);
					Rms.saveRMS("NRitem1", data6);
				}
				break;
			}
			case 2:
			{
				short num2 = d.readShort();
				int num3 = d.readShort();
				for (int k = num2; k < num3; k++)
				{
					ItemTemplates.add(new ItemTemplate((short)k, d.readByte(), d.readByte(), d.readUTF(), d.readUTF(), d.readByte(), d.readInt(), d.readShort(), d.readShort(), d.readBoolean()));
				}
				if (isSave)
				{
					d.reset();
					sbyte[] data7 = new sbyte[d.available()];
					d.readFully(ref data7);
					Rms.saveRMS("NRitem2", data7);
					sbyte[] data8 = new sbyte[1] { GameScr.vcItem };
					Rms.saveRMS("NRitemVersion", data8);
					LoginScr.isUpdateItem = false;
					if (GameScr.vsData == GameScr.vcData && GameScr.vsMap == GameScr.vcMap && GameScr.vsSkill == GameScr.vcSkill && GameScr.vsItem == GameScr.vcItem)
					{
						GameScr.gI().readDart();
						GameScr.gI().readEfect();
						GameScr.gI().readArrow();
						GameScr.gI().readSkill();
						Service.gI().clientOk();
					}
				}
				break;
			}
			case 100:
				Char.Arr_Head_2Fr = readArrHead(d);
				if (isSave)
				{
					d.reset();
					sbyte[] data5 = new sbyte[d.available()];
					d.readFully(ref data5);
					Rms.saveRMS("NRitem100", data5);
				}
				break;
			}
		}
		catch (Exception ex)
		{
			ex.ToString();
		}
	}
	public ItemOption readItemOption(Message msg)
	{
		ItemOption result = null;
		try
		{
			int num = msg.reader().readShort();
			int param = msg.reader().readInt();
			if (num != -1)
			{
				result = new ItemOption(num, param);
			}
		}
		catch (Exception)
		{
			Res.err(">>>>read.ItemOption  errr:");
		}
		return result;
	}
    private void readFrameBoss(Message msg, int mobTemplateId)
    {
        try
        {
            int num = msg.reader().readByte();
            int[][] array = new int[num][];
            for (int i = 0; i < num; i++)
            {
                int num2 = msg.reader().readByte();
                array[i] = new int[num2];
                for (int j = 0; j < num2; j++)
                {
                    array[i][j] = msg.reader().readByte();
                }
            }
            frameHT_NEWBOSS.put(mobTemplateId + string.Empty, array);
        }
        catch (Exception)
        {
        }
    }

    private int[][] readArrHead(myReader d)
    {
        int[][] array = new int[1][] { new int[2] { 542, 543 } };
        try
        {
            int num = d.readShort();
            array = new int[num][];
            for (int i = 0; i < array.Length; i++)
            {
                int num2 = d.readByte();
                array[i] = new int[num2];
                for (int j = 0; j < num2; j++)
                {
                    array[i][j] = d.readShort();
                }
            }
        }
        catch (Exception)
        {
        }
        return array;
    }

    public void phuban_Info(Message msg)
    {
        try
        {
            sbyte b = msg.reader().readByte();
            if (b == 0)
            {
                readPhuBan_CHIENTRUONGNAMEK(msg, b);
            }
        }
        catch (Exception)
        {
        }
    }

    private void readPhuBan_CHIENTRUONGNAMEK(Message msg, int type_PB)
    {
        try
        {
            sbyte b = msg.reader().readByte();
            if (b == 0)
            {
                short idmapPaint = msg.reader().readShort();
                string nameTeam = msg.reader().readUTF();
                string nameTeam2 = msg.reader().readUTF();
                int maxPoint = msg.reader().readInt();
                short timeSecond = msg.reader().readShort();
                int maxLife = msg.reader().readByte();
                GameScr.phuban_Info = new InfoPhuBan(type_PB, idmapPaint, nameTeam, nameTeam2, maxPoint, timeSecond);
                GameScr.phuban_Info.maxLife = maxLife;
                GameScr.phuban_Info.updateLife(type_PB, 0, 0);
            }
            else if (b == 1)
            {
                int pointTeam = msg.reader().readInt();
                int pointTeam2 = msg.reader().readInt();
                if (GameScr.phuban_Info != null)
                {
                    GameScr.phuban_Info.updatePoint(type_PB, pointTeam, pointTeam2);
                }
            }
            else if (b == 2)
            {
                sbyte b2 = msg.reader().readByte();
                short type = 0;
                short num = -1;
                if (b2 == 1)
                {
                    type = 1;
                    num = 3;
                }
                else if (b2 == 2)
                {
                    type = 2;
                }
                num = -1;
                GameScr.phuban_Info = null;
                GameScr.addEffectEnd(type, num, 0, GameCanvas.hw, GameCanvas.hh, 0, 0, -1, null);
            }
            else if (b == 5)
            {
                short timeSecond2 = msg.reader().readShort();
                if (GameScr.phuban_Info != null)
                {
                    GameScr.phuban_Info.updateTime(type_PB, timeSecond2);
                }
            }
            else if (b == 4)
            {
                int lifeTeam = msg.reader().readByte();
                int lifeTeam2 = msg.reader().readByte();
                if (GameScr.phuban_Info != null)
                {
                    GameScr.phuban_Info.updateLife(type_PB, lifeTeam, lifeTeam2);
                }
            }
        }
        catch (Exception)
        {
        }
    }

   public void read_opt(Message msg)
	{
		try
		{
			switch (msg.reader().readByte())
			{
			case 0:
			{
				short idHat = msg.reader().readShort();
				Char.myCharz().idHat = idHat;
				SoundMn.gI().getStrOption();
				break;
			}
			case 2:
			{
				int num2 = msg.reader().readInt();
				sbyte b2 = msg.reader().readByte();
				short num3 = msg.reader().readShort();
				string v = num3 + "," + b2;
				ImgByName.getImagePath("banner_" + num3, ImgByName.hashImagePath);
				GameCanvas.danhHieu.put(num2 + string.Empty, v);
				break;
			}
			case 3:
			{
				short num = msg.reader().readShort();
				SmallImage.createImage(num);
				BackgroudEffect.id_water1 = num;
				break;
			}
			case 4:
			{
				string o = msg.reader().readUTF();
				GameCanvas.messageServer.addElement(o);
				break;
			}
			}
		}
		catch (Exception)
		{
		}
	}
    public void read_UpdateSkill(Message msg)
    {
        try
        {
            short num = msg.reader().readShort();
            sbyte b = -1;
            try
            {
                b = msg.reader().readSByte();
            }
            catch (Exception)
            {
            }
            if (b == 0)
            {
                short curExp = msg.reader().readShort();
                for (int i = 0; i < Char.myCharz().vSkill.size(); i++)
                {
                    Skill skill = (Skill)Char.myCharz().vSkill.elementAt(i);
                    if (skill.skillId == num)
                    {
                        skill.curExp = curExp;
                        break;
                    }
                }
            }
            else if (b == 1)
            {
                sbyte b2 = msg.reader().readByte();
                for (int j = 0; j < Char.myCharz().vSkill.size(); j++)
                {
                    Skill skill2 = (Skill)Char.myCharz().vSkill.elementAt(j);
                    if (skill2.skillId == num)
                    {
                        for (int k = 0; k < 20; k++)
                        {
                            string nameImg = "Skills_" + skill2.template.id + "_" + b2 + "_" + k;
                            MainImage imagePath = ImgByName.getImagePath(nameImg, ImgByName.hashImagePath);
                        }
                        break;
                    }
                }
            }
            else
            {
                if (b != -1)
                {
                    return;
                }
                Skill skill3 = Skills.get(num);
                for (int l = 0; l < Char.myCharz().vSkill.size(); l++)
                {
                    Skill skill4 = (Skill)Char.myCharz().vSkill.elementAt(l);
                    if (skill4.template.id == skill3.template.id)
                    {
                        Char.myCharz().vSkill.setElementAt(skill3, l);
                        break;
                    }
                }
                for (int m = 0; m < Char.myCharz().vSkillFight.size(); m++)
                {
                    Skill skill5 = (Skill)Char.myCharz().vSkillFight.elementAt(m);
                    if (skill5.template.id == skill3.template.id)
                    {
                        Char.myCharz().vSkillFight.setElementAt(skill3, m);
                        break;
                    }
                }
                for (int n = 0; n < GameScr.onScreenSkill.Length; n++)
                {
                    if (GameScr.onScreenSkill[n] != null && GameScr.onScreenSkill[n].template.id == skill3.template.id)
                    {
                        GameScr.onScreenSkill[n] = skill3;
                        break;
                    }
                }
                for (int num2 = 0; num2 < GameScr.keySkill.Length; num2++)
                {
                    if (GameScr.keySkill[num2] != null && GameScr.keySkill[num2].template.id == skill3.template.id)
                    {
                        GameScr.keySkill[num2] = skill3;
                        break;
                    }
                }
                if (Char.myCharz().myskill.template.id == skill3.template.id)
                {
                    Char.myCharz().myskill = skill3;
                }
                GameScr.info1.addInfo(mResources.hasJustUpgrade1 + skill3.template.name + mResources.hasJustUpgrade2 + skill3.point, 0);
            }
        }
        catch (Exception)
        {
        }
    }
}
