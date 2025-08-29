using System;
using UnityEngine;

public class ItemOption
{
	public int param;

	public sbyte active;

	public sbyte activeCard;

	public ItemOptionTemplate optionTemplate;

	public ItemOption()
	{
	}

	public bool IsValidOption()
	{
		if (this != null && optionTemplate != null && optionTemplate.id != 21 && optionTemplate.id != 200 && optionTemplate.id != 72 && optionTemplate.id != 57 && optionTemplate.id != 58 && optionTemplate.id != 34 && optionTemplate.id != 35 && optionTemplate.id != 36 && optionTemplate.id != 102 && optionTemplate.id != 107)
		{
			return true;
		}
		return false;
	}

	public bool IsValidOptionSPL()
	{
		if (optionTemplate.id == 50
		|| optionTemplate.id == 77
		|| optionTemplate.id == 80
		|| optionTemplate.id == 81
		|| optionTemplate.id == 94
		|| optionTemplate.id == 103
		|| optionTemplate.id == 108
		|| optionTemplate.id == 95
		|| optionTemplate.id == 96
		|| optionTemplate.id == 97
		|| optionTemplate.id == 98
		|| optionTemplate.id == 99
		|| optionTemplate.id == 100
		|| optionTemplate.id == 101
		|| optionTemplate.id == 160
		)
		{
			return true;
		}
		return false;
	}

	public ItemOption(int optionTemplateId, int param)
	{
		if (optionTemplateId == 22)
		{
			optionTemplateId = 6;
			param *= 1000;
		}
		if (optionTemplateId == 23)
		{
			optionTemplateId = 7;
			param *= 1000;
		}
		this.param = param;
		optionTemplate = GameScr.gI().iOptionTemplates[optionTemplateId];
	}

	public string getOptionString()
	{
		string str = string.Empty;
		if (optionTemplate.id == 6 || optionTemplate.id == 7 || optionTemplate.id == 28 || optionTemplate.id == 48)
		{
			str = Res.formatNumber2((long)param);
		}
		else
		{
			str = param.ToString();
		}
		
		return NinjaUtil.Replace(optionTemplate.name, "#", Res.formatNumber2((long)param) + string.Empty);
	}
	
	public string getOptionStringSPL()
	{
		string str = null;
		if (optionTemplate.id == 6 || optionTemplate.id == 7 || optionTemplate.id == 28 || optionTemplate.id == 48)
		{
			str = Res.formatNumber2((long)param);
		}
		else
		{
			str = param.ToString();
		}

		string optNm = getOptName(optionTemplate.id);

		if (optNm.Length > 0)
		{
			return NinjaUtil.Replace(optNm, "#", str + string.Empty);
		}
		return NinjaUtil.Replace(optionTemplate.name, "#", str + string.Empty);
	}

    private string getOptName(int id)
	{
		string optNm = string.Empty;

		switch (id)
		{
			case 50:
				optNm = "SĐ +#%";
				break;
			case 94:
				optNm = "Giảm #% ST";
				break;
			case 95:
				optNm = "Hút #% HP";
				break;
			case 96:
				optNm = "Hút #% KI";
				break;
			case 97:
				optNm = "Phản #% ST";
				break;
			case 98:
				optNm = "XG #%";
				break;
			case 99:
				optNm = "XG #%";
				break;
			case 101:
				optNm = "+#% TNSM";
				break;
			case 108:
				optNm = "+#% Né";
				break;
			default:
				break;
		}

		return optNm;
	}

	public string getOptiongColor()
	{
		return NinjaUtil.Replace(optionTemplate.name, "$", string.Empty);
	}
}
