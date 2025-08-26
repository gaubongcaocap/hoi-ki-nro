using System.Collections.Generic;

namespace Mod.XMAP
{
	public struct GroupMap
	{
		public string NameGroup;

		public List<int> IdMaps;

		public GroupMap(string nameGroup, List<int> idMaps)
		{
			NameGroup = nameGroup;
			IdMaps = idMaps;
		}
	}
}
