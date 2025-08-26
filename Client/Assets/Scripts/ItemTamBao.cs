using System.Collections.Generic;

public class ItemTamBao
{
	public int id;

	public int color;

	public int rate;

	public List<ItemOption> options;

	public ItemTamBao(int id, int color, int rate)
	{
		this.id = id;
		this.color = color;
		this.rate = rate;
		options = new List<ItemOption>();
	}

	private int getItemNameColor()
	{
		return color switch
		{
			0 => 16777215, 
			1 => 65280, 
			2 => 255, 
			3 => 16711935, 
			4 => 16776960, 
			_ => 16777215, 
		};
	}

	public void paintInfo(mGraphics g, int x, int y)
	{
		ItemTemplate template = ItemTemplates.get((short)id);
		SmallImage.drawSmallImage(g, template.iconID, x + 15, y + 15, 0, 3);
		g.setColor(getItemNameColor());
		mFont.tahoma_7b_white.drawString(g, template.name, x + 40, y + 10, 0);
		y += 40;
		foreach (ItemOption opt in options)
		{
			if (opt.IsValidOption())
			{
				if (opt.optionTemplate.id >= 127 && opt.optionTemplate.id <= 135)
				{
					g.setColor(16776960);
				}
				else if (opt.optionTemplate.id >= 73 && opt.optionTemplate.id <= 77)
				{
					g.setColor(65280);
				}
				else if (opt.optionTemplate.id >= 94 && opt.optionTemplate.id <= 108)
				{
					g.setColor(16711935);
				}
				else
				{
					g.setColor(16777215);
				}
				mFont.tahoma_7_white.drawString(g, opt.getOptionString(), x, y, 0);
				y += 20;
			}
		}
	}
}
