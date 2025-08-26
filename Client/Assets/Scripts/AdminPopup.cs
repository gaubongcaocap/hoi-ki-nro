using System;
using UnityEngine;

public class AdminPopup : IActionListener
{
	private static AdminPopup instance;

	private int iconNPC;

	private int size;

	private int sizeOption;

	private string text;

	private int iconID;

	private int quantity;

	private int optionID;

	private int param;

	private string name;

	private bool isShow;

	private Scroll scr = new Scroll();

	private int x;

	private int y;

	private int w;

	private int h;

	public static AdminPopup gI()
	{
		if (instance == null)
		{
			instance = new AdminPopup();
		}
		return instance;
	}

	public AdminPopup()
	{
		w = 320;
		h = GameCanvas.h / 2;
		x = GameCanvas.w / 2 - w / 2;
		y = GameCanvas.h / 2 - h / 2 - 20;
	}

	public void readInfoPopup(Message msg)
	{
		try
		{
			iconNPC = msg.reader().readInt();
			Debug.LogError(iconNPC);
			size = msg.reader().readInt();
			text = msg.reader().readUTF();
			name = msg.reader().readUTF();
			iconID = msg.reader().readInt();
			quantity = msg.reader().readInt();
			sizeOption = msg.reader().readInt();
			optionID = msg.reader().readInt();
			param = msg.reader().readInt();
			addPopup(size, sizeOption);
		}
		catch (Exception exception)
		{
			Debug.LogException(exception);
		}
	}

	private void addPopup(int size, int sizeOption)
	{
		isShow = true;
		scr.setStyle(size, size * h, x, y, w, h, styleUPDOWN: true, 1);
	}

	public void paint(mGraphics g)
	{
		if (!isShow)
		{
			return;
		}
		try
		{
			PopUp.paintPopUp(g, x, y, w, h, 16777215, isButton: false);
			g.translate(0, -scr.cmy);
			for (int i = 0; i < size; i++)
			{
				SmallImage.drawSmallImage(g, iconID, x + 5, y + 20 * i, 0, 0);
			}
			g.translate(0, scr.cmy);
		}
		catch (Exception exception)
		{
			Debug.LogException(exception);
		}
	}

	public void perform(int idAction, object p)
	{
		throw new NotImplementedException();
	}
}
