using System;
using System.Collections.Generic;
using UnityEngine;

public class QuaNapTuan
{
	public static Image banner;

	public static Image btnExit;

	public static Image nhan;

	public static Image nhan2;

	public static List<ItemNapTuan> listItem;

	public static bool isNapTuan;

	public static bool isNhan = false;

	private static readonly int ITEM_BOX_SIZE = 40;

	private static readonly int ITEM_SPACING = 10;

	private static readonly int GRID_COLUMNS = 5;

	public static ItemNapTuan selectedItem;

	private static int currentReceivingIndex = -1;

	private static long lastReceiveTime;

	private static readonly int RECEIVE_DELAY = 500;

	public static void LoadImage()
	{
		try
		{
			LoadBasicImages();
		}
		catch (Exception ex)
		{
			Debug.LogError("Lỗi khi tải hình ảnh nạp tuần: " + ex.Message);
		}
	}

	private static void LoadBasicImages()
	{
		banner = GameCanvas.loadImage("/mainImage/BannerNap.png");
		btnExit = GameCanvas.loadImage("/mainImage/BtnExitNew.png");
		nhan = GameCanvas.loadImage("/mainImage/myTexture2dbtnl.png");
		nhan2 = GameCanvas.loadImage("/mainImage/myTexture2dbtnlf.png");
	}

	public static void sendOpenUI()
	{
		try
		{
			Message msg = new Message(71);
			msg.writer().writeByte(0);
			Session_ME.gI().sendMessage(msg);
			isNapTuan = true;
		}
		catch (Exception ex)
		{
			Debug.LogError("Lỗi gửi request mở UI: " + ex.Message);
		}
	}

	public static void receiveMsg(Message msg)
	{
		try
		{
			sbyte type = msg.reader().readByte();
			switch (type)
			{
			case -1:
				GameScr.info1.addInfo("Hệ thống quà nạp tuần đang bảo trì!", 0);
				isNapTuan = false;
				break;
			case 0:
			{
				int count = msg.reader().readInt();
				listItem = new List<ItemNapTuan>();
				for (int j = 0; j < count; j++)
				{
					ItemNapTuan item2 = new ItemNapTuan(msg.reader().readInt());
					sbyte optionCount = msg.reader().readByte();
					for (int k = 0; k < optionCount; k++)
					{
						int optionId = msg.reader().readShort();
						int param = msg.reader().readInt();
						item2.addOption(optionId, param);
					}
					int playerCount = msg.reader().readInt();
					for (int l = 0; l < playerCount; l++)
					{
						string playerName = msg.reader().readUTF();
						item2.addPlayer(playerName);
					}
					listItem.Add(item2);
				}
				msg.cleanup();
				break;
			}
			case 1:
			{
				int receivedItemId = msg.reader().readInt();
				bool num = msg.reader().readBoolean();
				string message = msg.reader().readUTF();
				if (num)
				{
					ItemNapTuan item = listItem.Find((ItemNapTuan i) => i.id == receivedItemId);
					if (item != null)
					{
						item.isReceived = true;
						GameScr.info1.addInfo("Nhận quà thành công!", 0);
					}
				}
				else
				{
					GameScr.info1.addInfo(message, 0);
				}
				break;
			}
			default:
				GameScr.info1.addInfo("Có lỗi xảy ra!", 0);
				msg.cleanup();
				break;
			}
		}
		catch (Exception ex)
		{
			Debug.LogError("Lỗi nhận message quà nạp tuần: " + ex.Message);
			GameScr.info1.addInfo("Có lỗi xảy ra khi nhận quà!", 0);
			msg?.cleanup();
		}
	}

	public static void paint(mGraphics g)
	{
		int bannerX = (GameCanvas.w - banner.getWidth()) / 2;
		int bannerY = (GameCanvas.h - banner.getHeight()) / 2;
		g.setColor(0, 0.5f);
		g.fillRect(0, 0, GameCanvas.w, GameCanvas.h);
		g.drawImage(banner, bannerX, bannerY);
		g.drawImage(btnExit, GameCanvas.w - 110, 60);
		g.drawImage(nhan, GameCanvas.w - 273, GameCanvas.h - 40);
		mFont.tahoma_7b_white.drawString(g, "Nhận", GameCanvas.w - 240, GameCanvas.h - 32, mFont.CENTER);
		if (listItem != null && listItem.Count > 0)
		{
			int adjustedY = bannerY + 170;
			foreach (ItemNapTuan item in listItem)
			{
				if (ItemTemplates.get((short)item.id).type < 5)
				{
					adjustedY = bannerY + 173;
					break;
				}
				if (ItemTemplates.get((short)item.id).type == 5)
				{
					adjustedY = bannerY + 175;
					break;
				}
			}
			paintItems(g, bannerX + 26, adjustedY);
		}
		if (selectedItem != null)
		{
			g.setColor(0, 0.7f);
			g.fillRect(0, 0, GameCanvas.w, GameCanvas.h);
			int boxWidth = 200;
			int boxHeight = 180;
			int infoX = (GameCanvas.w - boxWidth) / 2;
			int infoY = (GameCanvas.h - boxHeight) / 2;
			g.setColor(12887172);
			g.fillRect(infoX - 2, infoY - 2, boxWidth + 4, boxHeight + 4, 10);
			g.setColor(1842204);
			g.fillRect(infoX, infoY, boxWidth, boxHeight, 10);
			int closeSize = 20;
			g.setColor(9830424);
			g.fillRect(infoX + boxWidth - closeSize - 5, infoY + 5, closeSize, closeSize, 10);
			mFont.tahoma_7b_white.drawString(g, "X", infoX + boxWidth - closeSize + 4, infoY + 9, 0);
			selectedItem.paintInfo(g, infoX + 5, infoY + 5);
		}
	}

	private static void paintItems(mGraphics g, int startX, int startY)
	{
		int currentX = startX;
		int spacingX = 50;
		int maxItems = 6;
		for (int i = 0; i < listItem.Count && i < maxItems; i++)
		{
			listItem[i].paintItem(g, currentX, startY);
			currentX += spacingX;
		}
	}

	public static void update()
	{
		if (isNhan)
		{
			if (currentReceivingIndex == -1)
			{
				currentReceivingIndex = 0;
				lastReceiveTime = mSystem.currentTimeMillis();
			}
			else if (mSystem.currentTimeMillis() - lastReceiveTime >= RECEIVE_DELAY)
			{
				if (currentReceivingIndex < listItem.Count)
				{
					sendReceiveItem(listItem[currentReceivingIndex].id);
					lastReceiveTime = mSystem.currentTimeMillis();
					currentReceivingIndex++;
				}
				else
				{
					isNhan = false;
					currentReceivingIndex = -1;
				}
			}
		}
		if (!GameCanvas.isPointerClick || !GameCanvas.isPointerJustRelease)
		{
			return;
		}
		int startX = (GameCanvas.w - banner.getWidth()) / 2 + 26;
		int startY = (GameCanvas.h - banner.getHeight()) / 2 + 167;
		int spacingX = 50;
		for (int i = 0; i < listItem.Count && i < 6; i++)
		{
			if (GameCanvas.isPointerHoldIn(startX + i * spacingX - 20, startY - 20, 40, 40))
			{
				selectedItem = listItem[i];
				GameCanvas.clearAllPointerEvent();
				return;
			}
		}
		if (selectedItem != null)
		{
			int boxWidth = 200;
			int num = (GameCanvas.w - boxWidth) / 2;
			int infoY = (GameCanvas.h - 180) / 2;
			int closeSize = 20;
			if (GameCanvas.isPointerHoldIn(num + boxWidth - closeSize - 5, infoY + 5, closeSize, closeSize))
			{
				selectedItem = null;
				GameCanvas.clearAllPointerEvent();
				return;
			}
		}
		if (selectedItem != null && !isClickInInfoBox(GameCanvas.px, GameCanvas.py))
		{
			selectedItem = null;
			GameCanvas.clearAllPointerEvent();
		}
	}

	public static bool isClickInInfoBox(int x, int y)
	{
		if (selectedItem == null)
		{
			return false;
		}
		int boxX = (GameCanvas.w - 200) / 2;
		int boxY = (GameCanvas.h - 180) / 2;
		if (x >= boxX && x <= boxX + 200 && y >= boxY)
		{
			return y <= boxY + 180;
		}
		return false;
	}

	private static void sendReceiveItem(int itemId)
	{
		try
		{
			Message msg = new Message(71);
			msg.writer().writeByte(1);
			msg.writer().writeInt(itemId);
			Session_ME.gI().sendMessage(msg);
		}
		catch (Exception ex)
		{
			Debug.LogError("Lỗi gửi request nhận quà: " + ex.Message);
		}
	}
}
