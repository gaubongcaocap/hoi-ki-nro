using System.Collections.Generic;

public class ItemNapTuan
{
	public int id;

	public List<ItemOption> options;

	public List<string> listPlayerNhan;

	public bool isReceived;

	public int iconID => ((int?)ItemTemplates.get((short)id)?.iconID) ?? id;

	public ItemNapTuan(int id)
	{
		this.id = id;
		options = new List<ItemOption>();
		listPlayerNhan = new List<string>();
		isReceived = false;
	}

	public void addOption(int id, int param)
	{
		ItemOption option = new ItemOption(id, param);
		options.Add(option);
	}

	public void addPlayer(string playerName)
	{
		if (!listPlayerNhan.Contains(playerName))
		{
			listPlayerNhan.Add(playerName);
			if (playerName == Char.myCharz().cName)
			{
				isReceived = true;
			}
		}
	}

	public void paintInfo(mGraphics g, int x, int y)
	{
		SmallImage.drawSmallImage(g, iconID, x + 15, y + 15, 0, 3);
		string itemName = ItemTemplates.get((short)id).name;
		mFont.tahoma_7b_white.drawString(g, itemName, x + 35, y + 7, 0);
		int dy = y + 35;
		foreach (ItemOption option in options)
		{
			string optionString = option.getOptionString();
			if (option.optionTemplate.id != -1)
			{
				mFont.tahoma_7_white.drawString(g, optionString, x + 5, dy, 0);
				dy += 12;
			}
		}
		if (listPlayerNhan.Count <= 0)
		{
			return;
		}
		dy += 10;
		mFont.tahoma_7b_white.drawString(g, "Người đã nhận:", x + 5, dy, 0);
		dy += 12;
		foreach (string playerName in listPlayerNhan)
		{
			mFont.tahoma_7_grey.drawString(g, "- " + playerName, x + 15, dy, 0);
			dy += 12;
		}
	}

	public void paintItem(mGraphics g, int x, int y)
	{
		SmallImage.drawSmallImage(g, iconID, x, y, 0, 3);
		if (isReceived)
		{
			g.setColor(0, 0.5f);
			g.fillRect(x - 18, y - 17, 31, 31, 5);
			mFont.tahoma_7b_green.drawString(g, "✓", x, y - 6, mFont.CENTER);
		}
	}
}
