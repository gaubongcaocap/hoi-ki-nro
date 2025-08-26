using System;

namespace Mod
{
	public class ShowBoss
	{
		private static int SHOW_TIME = 20000;

		private int startX;

		private int targetX;

		private int currentX;

		private long startShowTime;

		private bool isShowing;

		private bool isDone;

		private static int VERTICAL_SPACING = 10;

		private int yPos;

		private static object lockObject = new object();

		public string nameBoss;

		public string mapName;

		public string playerKill;

		public int mapID;

		public long time;

		public DateTime AppearTime;

		public ShowBoss(string a)
		{
			if (a.Contains("tiêu diệt"))
			{
				a = a.Replace(" vừa tiêu diệt được", "|");
				a = a.Replace(" mọi người đều ngưỡng mộ", "|");
				a = a.Replace(" -> ", "|");
				a = a.Replace("(Đạo Tôn)", "|");
				a = a.Replace(" Kill Liên Sát ", "|");
				string[] array = a.Split('|');
				playerKill = array[0].Trim();
				nameBoss = array[1].Trim();
				mapName = "";
				mapID = -1;
				AppearTime = DateTime.Now;
				time = (long)(AppearTime - new DateTime(1970, 1, 1)).TotalSeconds;
				startShowTime = mSystem.currentTimeMillis();
				startX = -GameCanvas.w;
				targetX = 100;
				currentX = startX;
				isShowing = true;
				isDone = false;
				lock (lockObject)
				{
					yPos = 65 + ModFunc.killedBossNotif.size() * VERTICAL_SPACING;
					ModFunc.killedBossNotif.addElement(this);
					return;
				}
			}
			a = a.Replace(a.Substring(0, 5), "|");
			a = a.Replace(" vừa xuất hiện tại", "|");
			a = a.Replace(" khu vực", "|");
			string[] array2 = a.Split('|');
			nameBoss = array2[1].Trim();
			mapName = array2[2].Trim();
			mapID = ModFunc.GI().GetMapID(mapName);
			AppearTime = DateTime.Now;
			time = (long)(AppearTime - new DateTime(1970, 1, 1)).TotalSeconds;
		}

		public void PaintBoss(mGraphics g, int x, int y, int align)
		{
			if (!string.IsNullOrEmpty(playerKill))
			{
				if (ModFunc.notifKillBoss)
				{
					PaintKilledBoss(g);
				}
			}
			else
			{
				PaintActiveBoss(g, x, y, align);
			}
		}

		private void PaintKilledBoss(mGraphics g)
		{
			if (!isShowing)
			{
				return;
			}
			long deltaTime = mSystem.currentTimeMillis() - startShowTime;
			if (deltaTime < SHOW_TIME)
			{
				if (currentX < targetX)
				{
					currentX += 10 * mGraphics.zoomLevel;
					if (currentX > targetX)
					{
						currentX = targetX;
					}
				}
			}
			else if (deltaTime >= SHOW_TIME)
			{
				currentX -= 10 * mGraphics.zoomLevel;
				if (currentX < -GameCanvas.w)
				{
					isShowing = false;
					isDone = true;
					RemoveNotification(this);
					lock (lockObject)
					{
						for (int i = ModFunc.killedBossNotif.indexOf(this); i < ModFunc.killedBossNotif.size(); i++)
						{
							((ShowBoss)ModFunc.killedBossNotif.elementAt(i)).yPos = 70 + i * VERTICAL_SPACING;
						}
					}
				}
			}
			if (isShowing)
			{
				TimeSpan timeSpan = DateTime.Now.Subtract(AppearTime);
				GetTimeString(timeSpan);
				string notifText = "[" + nameBoss + "]  đã bị [" + playerKill + "]  hạ gục";
				int width = mFont.tahoma_7_yellow.getWidth(notifText);
				int maxWidth = GameCanvas.w / 2 - 100;
				if (width > maxWidth)
				{
					string line1 = "[" + nameBoss + "]  đã bị ";
					string line2 = "[" + playerKill + "]  hạ gục";
					mFont.tahoma_7_yellow.drawStringBorder(g, line1, currentX - 97, yPos + 8, mFont.LEFT, mFont.tahoma_7_grey);
					mFont.tahoma_7_yellow.drawStringBorder(g, line2, currentX - 97, yPos + 16, mFont.LEFT, mFont.tahoma_7_grey);
				}
				else
				{
					mFont.tahoma_7_yellow.drawStringBorder(g, notifText, currentX - 97, yPos + 12, mFont.LEFT, mFont.tahoma_7_grey);
				}
			}
		}

		private void PaintActiveBoss(mGraphics g, int x, int y, int align)
		{
			g.fillRect(GameCanvas.w - 20, y + 3, 20, 10, 2721889, 90);
			TimeSpan timeSpan = DateTime.Now.Subtract(AppearTime);
			string timeAppear = GetTimeString(timeSpan);
			mFont mFont = mFont.tahoma_7_yellow;
			if (TileMap.mapName.Trim().ToLower() == mapName.Trim().ToLower())
			{
				mFont = mFont.tahoma_7_red;
				for (int i = 0; i < GameScr.vCharInMap.size(); i++)
				{
					if (((Char)GameScr.vCharInMap.elementAt(i)).cName == nameBoss)
					{
						mFont = mFont.tahoma_7b_red;
						break;
					}
				}
			}
			mFont.drawStringBorder(g, nameBoss + " - " + mapName + " - " + timeAppear, x, y, align, mFont.tahoma_7_grey);
		}

		private string GetTimeString(TimeSpan timeSpan)
		{
			string timeAppear = "";
			int hours = (int)System.Math.Floor((decimal)timeSpan.TotalHours);
			if (hours > 0)
			{
				timeAppear += $"{hours}h";
			}
			if (timeSpan.Minutes > 0)
			{
				timeAppear += $"{timeSpan.Minutes}m";
			}
			return timeAppear + $"{timeSpan.Seconds}s";
		}

		private static void RemoveNotification(ShowBoss notification)
		{
			lock (lockObject)
			{
				if (!string.IsNullOrEmpty(notification.playerKill))
				{
					int num = ModFunc.killedBossNotif.indexOf(notification);
					ModFunc.killedBossNotif.removeElement(notification);
					for (int i = num; i < ModFunc.killedBossNotif.size(); i++)
					{
						((ShowBoss)ModFunc.killedBossNotif.elementAt(i)).yPos = 65 + i * VERTICAL_SPACING;
					}
				}
				else
				{
					ModFunc.activeBossNotif.removeElement(notification);
				}
			}
		}

		public static void UpdateNotifications()
		{
			for (int i = ModFunc.killedBossNotif.size() - 1; i >= 0; i--)
			{
				ShowBoss notification = (ShowBoss)ModFunc.killedBossNotif.elementAt(i);
				if (notification.isDone)
				{
					RemoveNotification(notification);
					for (int j = i; j < ModFunc.killedBossNotif.size(); j++)
					{
						((ShowBoss)ModFunc.killedBossNotif.elementAt(j)).yPos = 65 + j * VERTICAL_SPACING;
					}
				}
			}
			for (int i2 = ModFunc.activeBossNotif.size() - 1; i2 >= 0; i2--)
			{
				ShowBoss notification2 = (ShowBoss)ModFunc.activeBossNotif.elementAt(i2);
				if (notification2.isDone)
				{
					RemoveNotification(notification2);
				}
			}
			if (ModFunc.killedBossNotif.size() <= 5)
			{
				return;
			}
			lock (lockObject)
			{
				_ = (ShowBoss)ModFunc.killedBossNotif.elementAt(0);
				ModFunc.killedBossNotif.removeElementAt(0);
				for (int k = 0; k < ModFunc.killedBossNotif.size(); k++)
				{
					((ShowBoss)ModFunc.killedBossNotif.elementAt(k)).yPos = 65 + k * VERTICAL_SPACING;
				}
			}
		}

		public static void HandleChatVip(string chatVip)
		{
			string chatLower = chatVip.Trim().ToLower();
			ShowBoss notification = new ShowBoss(chatVip);
			if (chatLower.Contains("boss") && chatLower.Contains("xuất hiện"))
			{
				lock (lockObject)
				{
					ModFunc.activeBossNotif.addElement(notification);
					if (ModFunc.activeBossNotif.size() > 5)
					{
						ModFunc.activeBossNotif.removeElementAt(0);
					}
					return;
				}
			}
			if (!chatLower.Contains("tiêu diệt"))
			{
				return;
			}
			lock (lockObject)
			{
				if (ModFunc.killedBossNotif.size() >= 5)
				{
					ModFunc.killedBossNotif.removeElementAt(0);
					for (int i = 0; i < ModFunc.killedBossNotif.size(); i++)
					{
						((ShowBoss)ModFunc.killedBossNotif.elementAt(i)).yPos = 65 + i * VERTICAL_SPACING;
					}
				}
				notification.yPos = 65 + ModFunc.killedBossNotif.size() * VERTICAL_SPACING;
				ModFunc.killedBossNotif.addElement(notification);
			}
		}
	}
}
