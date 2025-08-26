using System;

namespace Assets.Assets.Scripts.Assembly_CSharp
{
	[Serializable]
	public class Random
	{
		private ulong seed;

		public Random(ulong seed)
		{
			this.seed = (seed ^ 0x5DEECE66DL) & 0xFFFFFFFFFFFFL;
		}

		public int NextInt(int n)
		{
			if (n <= 0)
			{
				throw new ArgumentException("n must be positive");
			}
			if ((n & -n) == n)
			{
				return (int)((long)n * (long)Next(31) >> 31);
			}
			ulong bits;
			ulong val;
			do
			{
				bits = Next(31);
				val = bits % (uint)n;
			}
			while (bits - val + (uint)(n - 1) < 0);
			return (int)val;
		}

		protected ulong Next(int bits)
		{
			seed = (seed * 25214903917L + 11) & 0xFFFFFFFFFFFFL;
			return seed >> 48 - bits;
		}
	}
}
