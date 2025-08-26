using System;
using System.Collections.Generic;
using System.Threading;

public class QuayTamBao
{
	private static readonly int ITEM_THUONG_START = 13;

	private static readonly int ITEM_THUONG_END = 23;

	private static readonly int TOTAL_RATE = 10000;

	private static readonly int MAX_NO = 5000;

	private static readonly int NO_BAR_WIDTH = 10;

	private static readonly int NO_BAR_HEIGHT = 200;

	private static readonly int BOX_INFO_WIDTH = 200;

	private static readonly int BOX_INFO_HEIGHT = 200;

	private static readonly int ROLL_BUTTON_WIDTH = 90;

	private static readonly int ROLL_BUTTON_HEIGHT = 50;

	private static int currentNo = 0;

	public static int phiQuayx1 = 1;

	public static int phiQuayx10 = 10;

	public static int iconTHUONG;

	public static int iconVIP;

	public static bool isTamBao = false;

	public static bool isTamBaoVip = false;

	public static bool isNhan = false;

	public static bool isQuay = false;

	public static long count = 0L;

	public static long select = 0L;

	public static long lastSelect = mSystem.currentTimeMillis();

	public static long last = mSystem.currentTimeMillis();

	public static long lastQuay;

	public static int soLuot;

	public static int speed = 10;

	public static List<ItemTamBao> listItem = new List<ItemTamBao>();

	public static List<ItemTamBao> listNhan = new List<ItemTamBao>();

	public static Image background;

	public static Image btnExit;

	public static Image btnRollx1;

	public static Image btnRollx10;

	public static Image thuong;

	public static Image vip;

	public static Image txtThuong;

	public static Image txtVip;

	public static Image chucMung;

	public static Image btnDong;

	public static Image btnReRoll;

	public static Image imgSelect;

	public static Image[] bg = new Image[5];

	public static Image[] effr = new Image[4];

	public static Image[] effy = new Image[4];

	public static Image quay;

	public static Item item = new Item();

	public static int bg_i_W = 30;

	public static int bg_i_H = 30;

	public static int bg_i_x = 16;

	public static int bg_i_y = 14;

	private static ItemTamBao selectedItem;

	private static readonly int MIN_SPIN_SPEED = 10;

	private static readonly int MAX_SPIN_SPEED = 50;

	private static readonly int SPIN_DURATION = 1000;

	private static readonly int SLOW_DOWN_TIME = 6000;

	private static readonly int SELECT_MOVE_SPEED = 500;

	public static void loadImage()
	{
		try
		{
			LoadBasicImages();
			LoadBackgroundImages();
			LoadEffectImages();
		}
		catch (Exception)
		{
			showMaintenanceMessage();
		}
	}

	private static void LoadBasicImages()
	{
		background = GameCanvas.loadImage("/quayTamBao/background.png");
		btnExit = GameCanvas.loadImage("/quayTamBao/btnExit.png");
		btnRollx1 = GameCanvas.loadImage("/quayTamBao/btnRollx1.png");
		btnRollx10 = GameCanvas.loadImage("/quayTamBao/btnRollx10.png");
		thuong = GameCanvas.loadImage("/quayTamBao/thuong.png");
		txtThuong = GameCanvas.loadImage("/quayTamBao/txtThuong.png");
		vip = GameCanvas.loadImage("/quayTamBao/vip.png");
		txtVip = GameCanvas.loadImage("/quayTamBao/txtVip.png");
		imgSelect = GameCanvas.loadImage("/quayTamBao/select.png");
		btnDong = GameCanvas.loadImage("/quayTamBao/btnDong.png");
		btnReRoll = GameCanvas.loadImage("/quayTamBao/btnReRoll.png");
		quay = GameCanvas.loadImage("/quayTamBao/quay.png");
		chucMung = GameCanvas.loadImage("/quayTamBao/chucMung.png");
	}

	private static void LoadBackgroundImages()
	{
		for (int i = 0; i < 5; i++)
		{
			bg[i] = GameCanvas.loadImage($"/quayTamBao/bg{i}.png");
		}
	}

	private static void LoadEffectImages()
	{
		for (int i = 0; i < 4; i++)
		{
			effr[i] = GameCanvas.loadImage($"/quayTamBao/effr{i}.png");
			effy[i] = GameCanvas.loadImage($"/quayTamBao/effy{i}.png");
			Thread.Sleep(200);
		}
	}

	public static void sendTamBao(int luotQuay)
	{
		try
		{
			Message msg = new Message(70);
			byte typeQuay = (byte)((!isTamBaoVip) ? 1u : 4u);
			msg.writer().writeByte(typeQuay);
			msg.writer().writeByte(luotQuay);
			Session_ME.gI().sendMessage(msg);
		}
		catch (Exception e)
		{
			HandleNetworkError("gửi message tầm bảo", e);
		}
	}

	public static void sendDataTamBao()
	{
		try
		{
			Message msg = new Message(70);
			msg.writer().writeByte(isTamBaoVip ? 3 : 0);
			Session_ME.gI().sendMessage(msg);
		}
		catch (Exception e)
		{
			HandleNetworkError("gửi data tầm bảo", e);
		}
	}

	private static void HandleNetworkError(string action, Exception e)
	{
		showMaintenanceMessage();
	}

	public static void paint(mGraphics g)
	{
		DrawBackground(g);
		DrawMainInterface(g);
		if (isNhan)
		{
			paintNhanThuong(g, listNhan);
		}
		else
		{
			paintListItem(g, listItem);
		}
		if (selectedItem == null)
		{
			DrawButtons(g);
		}
		else
		{
			paintItemInfo(g);
		}
		paintNoBar(g);
	}

	private static void DrawBackground(mGraphics g)
	{
		g.setColor(0, 0.5f);
		g.fillRect(0, 0, GameCanvas.w, GameCanvas.h);
		g.drawImageScale(background, -1, -2, GameCanvas.w + 10, GameCanvas.h + 10, 0);
	}

	private static void DrawMainInterface(mGraphics g)
	{
		g.drawImageScale(btnExit, GameCanvas.w - 30, 10, 20, 20, 0);
		g.drawImage(thuong, GameCanvas.hw - 70, 15);
		g.drawImage(vip, GameCanvas.hw + 10, 15);
	}

	private static void DrawButtons(mGraphics g)
	{
		int baseY = GameCanvas.h - 45;
		if (!isNhan)
		{
			DrawCostInfo(g, baseY);
		}
		DrawActionButtons(g, baseY);
	}

	private static void DrawCostInfo(mGraphics g, int baseY)
	{
		mFont.tahoma_7b_white.drawString(g, $"Tốn {phiQuayx1}", GameCanvas.w / 2 - 90, baseY, mFont.LEFT);
		SmallImage.drawSmallImage(g, isTamBaoVip ? iconVIP : iconTHUONG, GameCanvas.w / 2 - 55, baseY + 5, 0, 3);
		mFont.tahoma_7b_white.drawString(g, $"Tốn {phiQuayx10}", GameCanvas.w / 2 + 40, baseY, mFont.LEFT);
		SmallImage.drawSmallImage(g, isTamBaoVip ? iconVIP : iconTHUONG, GameCanvas.w / 2 + 80, baseY + 5, 0, 3);
	}

	private static void DrawActionButtons(mGraphics g, int baseY)
	{
		g.drawImageScale(isNhan ? btnDong : btnRollx1, GameCanvas.w / 2 - 120, baseY, ROLL_BUTTON_WIDTH, ROLL_BUTTON_HEIGHT, 0);
		g.drawImageScale(isNhan ? btnReRoll : btnRollx10, GameCanvas.w / 2 + 10, baseY, ROLL_BUTTON_WIDTH, ROLL_BUTTON_HEIGHT, 0);
	}

	public static void paintNhanThuong(mGraphics g, List<ItemTamBao> listItemz)
	{
		int y = GameCanvas.hh;
		g.drawImageScale(chucMung, GameCanvas.hw - 70, 40, 140, 60, 0);
		for (int i = 0; i < listItemz.Count; i++)
		{
			int x = GameCanvas.w / 2 - 30 * (listItemz.Count / 2) + 35 * i;
			int id = listItemz[i].id;
			int type = ItemTemplates.get((short)id).type;
			g.drawImageScale(bg[type switch
			{
				21 => 4, 
				93 => 1, 
				5 => 3, 
				11 => 2, 
				_ => 0, 
			}], x - bg_i_x, y - bg_i_y, bg_i_W, bg_i_H, 0);
			if (isQuay && i == select % listItemz.Count)
			{
				g.drawImageScale(imgSelect, x - 20, y - 20, 40, 40, 0);
			}
			if (type == 21 || type == 5)
			{
				g.drawImageScale(effr[count % 4], x - 18, y - 18, 35, 35, 0);
			}
			SmallImage.drawSmallImage(g, ItemTemplates.get((short)id).iconID, x, y, 0, 3);
		}
		if (mSystem.currentTimeMillis() - last > 20)
		{
			count++;
			last = mSystem.currentTimeMillis();
		}
		if (selectedItem != null)
		{
			paintItemInfo(g);
		}
	}

	public static void paintListItem(mGraphics g, List<ItemTamBao> listItemz)
	{
		g.drawImageScale(isTamBaoVip ? txtVip : txtThuong, GameCanvas.hw - 25, 37, 50, 20, 0);
		int startX = GameCanvas.w / 2 - 192;
		int itemIndex = 0;
		for (int row = 0; row < 6; row++)
		{
			if (itemIndex >= listItemz.Count)
			{
				break;
			}
			int y = 70 + row * 35;
			for (int col = 0; col < 11; col++)
			{
				if (itemIndex >= listItemz.Count)
				{
					break;
				}
				int x = startX + col * 35;
				drawItem(g, listItemz[itemIndex], x, y, itemIndex);
				itemIndex++;
			}
		}
		if (selectedItem != null)
		{
			paintItemInfo(g);
		}
		updateAnimation();
	}

	private static void drawItem(mGraphics g, ItemTamBao item, int x, int y, int itemIndex)
	{
		if (selectedItem == null)
		{
			int id = item.id;
			int type = ItemTemplates.get((short)id).type;
			if (GameCanvas.isPointerHoldIn(x - 20, y - 20, 40, 40))
			{
				g.setColor(16777215, 0.3f);
				g.fillRect(x - 20, y - 20, 40, 40);
			}
			int colorIndex = getColorIndex(type);
			g.drawImageScale(bg[colorIndex], x - bg_i_x, y - bg_i_y, bg_i_W, bg_i_H, 0);
			drawEffects(g, type, item.color, x, y);
			if (itemIndex == select && isQuay)
			{
				g.drawImageScale(imgSelect, x - 20, y - 20, 40, 40, 0);
			}
			SmallImage.drawSmallImage(g, ItemTemplates.get((short)id).iconID, x, y, 0, 3);
			if (GameCanvas.isPointerHoldIn(x - 20, y - 20, 40, 40) && GameCanvas.isPointerJustRelease)
			{
				selectedItem = item;
			}
		}
	}

	private static int getColorIndex(int type)
	{
		return type switch
		{
			21 => 4, 
			93 => 1, 
			5 => 3, 
			11 => 2, 
			_ => 0, 
		};
	}

	private static void drawEffects(mGraphics g, int type, int color, int x, int y)
	{
		if (type == 21 || type == 5)
		{
			g.drawImageScale(effr[count % 4], x - 18, y - 18, 35, 35, 0);
		}
		else if (color % 5 == 3)
		{
			g.drawImageScale(effy[count % 4], x - 18, y - 18, 35, 35, 0);
		}
		else if (color % 5 == 4)
		{
			g.drawImageScale(effr[count % 4], x - 18, y - 18, 35, 35, 0);
		}
	}

	private static void updateAnimation()
	{
		if (mSystem.currentTimeMillis() - last > 20)
		{
			count++;
			last = mSystem.currentTimeMillis();
		}
		if (isQuay)
		{
			long elapsedTime = mSystem.currentTimeMillis() - lastQuay;
			if (elapsedTime < SPIN_DURATION - SLOW_DOWN_TIME)
			{
				speed = MIN_SPIN_SPEED;
			}
			else if (elapsedTime < SPIN_DURATION)
			{
				float slowDownProgress = (float)(elapsedTime - (SPIN_DURATION - SLOW_DOWN_TIME)) / (float)SLOW_DOWN_TIME;
				speed = (int)((float)MIN_SPIN_SPEED + (float)(MAX_SPIN_SPEED - MIN_SPIN_SPEED) * slowDownProgress);
			}
			else
			{
				isQuay = false;
				isNhan = true;
				select = ((listNhan.Count > 0) ? (listNhan.Count - 1) : 0);
			}
			if (mSystem.currentTimeMillis() - lastSelect > speed)
			{
				select++;
				lastSelect = mSystem.currentTimeMillis();
			}
		}
		else if (mSystem.currentTimeMillis() - lastSelect > SELECT_MOVE_SPEED)
		{
			select++;
			lastSelect = mSystem.currentTimeMillis();
		}
	}

	public static void doTamBao()
	{
		if (ValidateInput())
		{
			HandleExitButton();
			HandleTabButtons();
			HandleActionButtons();
		}
	}

	private static bool ValidateInput()
	{
		if (!isClickInTamBaoArea(GameCanvas.px, GameCanvas.py) || selectedItem != null)
		{
			GameCanvas.clearAllPointerEvent();
			return false;
		}
		return true;
	}

	private static void HandleExitButton()
	{
		if (!isQuay && GameCanvas.isPointerHoldIn(GameCanvas.w - 30, 10, 20, 20) && GameCanvas.isPointerJustRelease)
		{
			CloseUI();
		}
	}

	private static void HandleTabButtons()
	{
		if (!isQuay)
		{
			if (GameCanvas.isPointerHoldIn(GameCanvas.hw - 70, 15, 58, 24) && GameCanvas.isPointerJustRelease)
			{
				isTamBaoVip = false;
				sendDataTamBao();
				GameCanvas.clearAllPointerEvent();
			}
			if (GameCanvas.isPointerHoldIn(GameCanvas.hw + 10, 15, 58, 24) && GameCanvas.isPointerJustRelease)
			{
				isTamBaoVip = true;
				sendDataTamBao();
				GameCanvas.clearAllPointerEvent();
			}
		}
	}

	private static void HandleActionButtons()
	{
		if (isQuay)
		{
			return;
		}
		int baseY = GameCanvas.h - 45;
		int x = GameCanvas.w / 2 - 120;
		int rightBtnX = GameCanvas.w / 2 + 10;
		if (GameCanvas.isPointerHoldIn(x, baseY, ROLL_BUTTON_WIDTH, ROLL_BUTTON_HEIGHT) && GameCanvas.isPointerJustRelease)
		{
			if (isNhan)
			{
				isNhan = false;
			}
			else if (!isTamBaoVip)
			{
				handleRoll(1);
			}
			else if (currentNo < MAX_NO)
			{
				GameCanvas.startOKDlg("Bạn cần tích đủ " + MAX_NO + " điểm nộ để quay VIP!");
			}
			GameCanvas.clearAllPointerEvent();
		}
		if (GameCanvas.isPointerHoldIn(rightBtnX, baseY, ROLL_BUTTON_WIDTH, ROLL_BUTTON_HEIGHT) && GameCanvas.isPointerJustRelease)
		{
			if (isNhan)
			{
				isNhan = false;
				sendDataTamBao();
			}
			else if (!isTamBaoVip)
			{
				handleRoll(10);
			}
			else if (currentNo < MAX_NO)
			{
				GameCanvas.startOKDlg("Bạn cần tích đủ " + MAX_NO + " điểm nộ để quay VIP!");
			}
			GameCanvas.clearAllPointerEvent();
		}
	}

	private static void CloseUI()
	{
		isTamBao = false;
		listItem.Clear();
		listNhan.Clear();
		GameCanvas.clearAllPointerEvent();
	}

	public static void receiveMsg(Message msg)
	{
		try
		{
			int type = msg.reader().readByte();
			if (!HandleSpecialCases(type))
			{
				switch (type)
				{
				case 0:
				case 3:
					HandleItemList(msg, type);
					break;
				case 1:
					HandleRollResult(msg);
					break;
				case 2:
					break;
				}
			}
		}
		catch (Exception)
		{
			showMaintenanceMessage();
		}
	}

	private static bool HandleSpecialCases(int type)
	{
		switch (type)
		{
		case -1:
			showMaintenanceMessage();
			return true;
		case -2:
			if (isTamBao)
			{
				sendDataTamBao();
			}
			return true;
		default:
			return false;
		}
	}

	private static void HandleItemList(Message msg, int type)
	{
		try
		{
			isTamBaoVip = type == 3;
			listItem.Clear();
			ReadItemList(msg);
			ReadServerInfo(msg);
		}
		catch (Exception)
		{
			showMaintenanceMessage();
		}
	}

	private static void ReadItemList(Message msg)
	{
		int itemCount = msg.reader().readInt();
		for (int i = 0; i < itemCount; i++)
		{
			ItemTamBao item = ReadItemData(msg);
			listItem.Add(item);
		}
	}

	private static ItemTamBao ReadItemData(Message msg)
	{
		short id = msg.reader().readShort();
		int color = msg.reader().readByte();
		int rate = msg.reader().readInt();
		ItemTamBao item = new ItemTamBao(id, color, rate);
		ReadItemOptions(msg, item);
		return item;
	}

	private static void ReadItemOptions(Message msg, ItemTamBao item)
	{
		sbyte optSize = msg.reader().readByte();
		for (int j = 0; j < optSize; j++)
		{
			short optId = msg.reader().readShort();
			int param = msg.reader().readInt();
			item.options.Add(new ItemOption(optId, param));
		}
	}

	private static void ReadServerInfo(Message msg)
	{
		try
		{
			ReadIconInfo(msg);
			ReadRollCosts(msg);
			ReadNoInfo(msg);
		}
		catch (Exception)
		{
		}
	}

	private static void ReadIconInfo(Message msg)
	{
		int tempIconVIP = msg.reader().readInt();
		int tempIconThuong = msg.reader().readInt();
		if (tempIconVIP > 0 && tempIconThuong > 0)
		{
			iconVIP = tempIconVIP;
			iconTHUONG = tempIconThuong;
		}
	}

	private static void ReadRollCosts(Message msg)
	{
		if (isTamBaoVip)
		{
			phiQuayx1 = msg.reader().readInt();
			phiQuayx10 = msg.reader().readInt();
		}
		else
		{
			phiQuayx1 = msg.reader().readInt();
			phiQuayx10 = msg.reader().readInt();
		}
	}

	private static void ReadNoInfo(Message msg)
	{
		try
		{
			currentNo = msg.reader().readInt();
			msg.reader().readInt();
		}
		catch (Exception)
		{
			currentNo = 0;
		}
	}

	private static void HandleRollResult(Message msg)
	{
		isQuay = true;
		lastQuay = mSystem.currentTimeMillis();
		speed = MIN_SPIN_SPEED;
		listNhan = new List<ItemTamBao>();
		int rewardCount = msg.reader().readInt();
		for (int i = 0; i < rewardCount; i++)
		{
			short id = msg.reader().readShort();
			int color = msg.reader().readByte();
			int rate = msg.reader().readInt();
			listNhan.Add(new ItemTamBao(id, color, rate));
		}
		UpdateNoAfterRoll();
	}

	private static void UpdateNoAfterRoll()
	{
		if (!isTamBaoVip)
		{
			handleNoAfterRoll(soLuot);
		}
		else
		{
			currentNo = 0;
		}
	}

	private static void handleNoAfterRoll(int soLanQuay)
	{
		currentNo += soLanQuay;
		if (currentNo > MAX_NO)
		{
			currentNo = MAX_NO;
		}
	}

	private static void handleRoll(int soLanQuay)
	{
		speed = MIN_SPIN_SPEED;
		lastQuay = mSystem.currentTimeMillis();
		soLuot = soLanQuay;
		sendTamBao(soLanQuay);
	}

	private static void showMaintenanceMessage()
	{
		GameCanvas.startOKDlg("Hệ thống tầm bảo đang bảo trì. Vui lòng quay lại sau!");
		isTamBao = false;
	}

	private static void paintItemInfo(mGraphics g)
	{
		if (selectedItem != null)
		{
			g.setColor(0, 0.7f);
			g.fillRect(0, 0, GameCanvas.w, GameCanvas.h);
			int boxWidth = 200;
			int boxHeight = 180;
			int x = (GameCanvas.w - boxWidth) / 2;
			int y = (GameCanvas.h - boxHeight) / 2;
			g.setColor(12887172);
			g.fillRect(x - 2, y - 2, boxWidth + 4, boxHeight + 4, 20);
			g.setColor(1842204);
			g.fillRect(x, y, boxWidth, boxHeight, 20);
			int closeSize = 20;
			int closeX = x + boxWidth - closeSize - 5;
			int closeY = y + 5;
			g.setColor(9830424);
			g.fillRect(closeX, closeY, closeSize, closeSize, 10);
			mFont.tahoma_7b_white.drawString(g, "X", closeX + 8, closeY + 4, 0);
			if (GameCanvas.isPointerJustRelease && GameCanvas.isPointerHoldIn(closeX, closeY, closeSize, closeSize))
			{
				selectedItem = null;
				GameCanvas.clearAllPointerEvent();
			}
			else
			{
				selectedItem.paintInfo(g, x + 15, y + 15);
			}
		}
	}

	private static void paintNoBar(mGraphics g)
	{
		int x = 10;
		int y = GameCanvas.h / 2 - NO_BAR_HEIGHT / 2;
		g.setColor(0);
		g.fillRect(x - 1, y - 1, NO_BAR_WIDTH + 2, NO_BAR_HEIGHT + 2);
		g.setColor(2236962);
		g.fillRect(x, y, NO_BAR_WIDTH, NO_BAR_HEIGHT);
		int currentHeight = (int)((float)currentNo / (float)MAX_NO * (float)NO_BAR_HEIGHT);
		if (currentHeight > 0)
		{
			for (int i = 0; i < currentHeight; i += 2)
			{
				float ratio = (float)i / (float)NO_BAR_HEIGHT;
				int color = ((!((float)currentNo > (float)MAX_NO * 0.8f)) ? ((!(ratio < 0.3f)) ? ((!(ratio < 0.6f)) ? 16746496 : 16729088) : 16711680) : 16777011);
				g.setColor(color);
				g.fillRect(x, y + NO_BAR_HEIGHT - i, NO_BAR_WIDTH, 2);
			}
			if (currentHeight > 0)
			{
				g.setColor(16777215);
				g.fillRect(x, y + NO_BAR_HEIGHT - currentHeight, NO_BAR_WIDTH, 1);
			}
		}
		g.setColor(16777215);
		mFont.tahoma_7b_dark.drawString(g, currentNo + "/" + MAX_NO, x + NO_BAR_WIDTH / 2 + 10, y - 14, mFont.CENTER);
		if ((float)currentNo > (float)MAX_NO * 0.8f)
		{
			g.setColor(16776960);
		}
		mFont.tahoma_7b_white.drawString(g, currentNo + "/" + MAX_NO, x + NO_BAR_WIDTH / 2 + 11, y - 15, mFont.CENTER);
	}

	private static bool isClickInTamBaoArea(int x, int y)
	{
		int tamBaoX = 0;
		int tamBaoY = 0;
		int tamBaoW = GameCanvas.w;
		int tamBaoH = GameCanvas.h;
		if (x < tamBaoX || x > tamBaoX + tamBaoW || y < tamBaoY || y > tamBaoY + tamBaoH)
		{
			return false;
		}
		if (selectedItem != null)
		{
			int infoX = GameCanvas.w / 2 - 100;
			int infoY = GameCanvas.h / 2 - 100;
			int infoW = 200;
			int infoH = 200;
			if (x >= infoX && x <= infoX + infoW && y >= infoY)
			{
				return y <= infoY + infoH;
			}
			return false;
		}
		return true;
	}
}
