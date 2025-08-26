using System;
using System.Threading;
using UnityEngine;

public class Sound
{
	public static int status;

	public static int postem;

	private static string filenametemp;

	private static float volumetem;

	public static bool isSound = true;

	public static bool isNotPlay;

	public static bool stopAll;

	public static AudioSource SoundRun;

	public static AudioSource SoundBGLoop;

	public static AudioClip[] music;

	public static GameObject[] player;

	public static sbyte MBClick = 1;

	public static sbyte MTone = 2;

	public static sbyte MSanzu = 3;

	public static sbyte MChakumi = 4;

	public static sbyte MChai = 5;

	public static sbyte MOshin = 6;

	public static sbyte MEchigo = 7;

	public static sbyte MKojin = 8;

	public static sbyte MHaruna = 9;

	public static sbyte MHirosaki = 10;

	public static sbyte MOokaza = 11;

	public static sbyte MGiotuyet = 12;

	public static sbyte MHangdong = 13;

	public static sbyte MDeKeu = 14;

	public static sbyte MChimKeu = 15;

	public static sbyte MBuocChan = 16;

	public static sbyte MNuocChay = 17;

	public static sbyte MBomMau = 18;

	public static sbyte MKiemGo = 19;

	public static sbyte MKiem = 20;

	public static sbyte MTieu = 21;

	public static sbyte MKunai = 22;

	public static sbyte MCung = 23;

	public static sbyte MDao = 24;

	public static sbyte MQuat = 25;

	public static sbyte MCung2 = 26;

	public static sbyte MTieu2 = 27;

	public static sbyte MTieu3 = 28;

	public static sbyte MKiem2 = 29;

	public static sbyte MKiem3 = 30;

	public static sbyte MDao2 = 31;

	public static sbyte MDao3 = 32;

	public static sbyte MCung3 = 33;

	public static int l1;

	public static void init()
	{
		GameObject gameObject = new GameObject();
		gameObject.name = "Audio Player";
		gameObject.transform.position = Vector3.zero;
		gameObject.AddComponent<AudioListener>();
		SoundBGLoop = gameObject.AddComponent<AudioSource>();
	}

	public static void init(int[] musicID, int[] sID)
	{
		if (player == null && music == null)
		{
			init();
			l1 = musicID.Length;
			player = new GameObject[musicID.Length + sID.Length];
			music = new AudioClip[musicID.Length + sID.Length];
			for (int i = 0; i < player.Length; i++)
			{
				getAssetSoundFile((i >= l1) ? ("/sound/" + (i - l1)) : ("/music/" + i), i);
			}
		}
	}

	public static void playSound(int id, float volume)
	{
		play(id + l1, volume);
	}

	public static void playSound1(int id, float volume)
	{
		play(id, volume);
	}

	public static void getAssetSoundFile(string fileName, int pos)
	{
		try
		{
			stop(pos);
			_ = string.Empty;
			load(Main.res + fileName, pos);
		}
		catch (Exception)
		{
		}
	}

	public static void stopAllz()
	{
		for (int i = 0; i < music.Length; i++)
		{
			stop(i);
		}
		sTopSoundBG();
	}

	public static void PlayMusic(int id)
	{
		if (GameCanvas.isPlaySound && !(SoundBGLoop == null))
		{
			if (id < 0 || id >= ModFunc.musics.Count)
			{
				id = 0;
			}
			if (isPlayingSoundBG(0) && !ModFunc.isPlayingMusic)
			{
				sTopSoundBG();
			}
			if (!isPlayingSoundBG(0))
			{
				SoundBGLoop.GetComponent<AudioSource>().loop = false;
				SoundBGLoop.GetComponent<AudioSource>().clip = ModFunc.musics[id];
				SoundBGLoop.GetComponent<AudioSource>().volume = 0.4f;
				SoundBGLoop.GetComponent<AudioSource>().Play();
				ModFunc.isPlayingMusic = true;
			}
			else
			{
				SoundBGLoop.GetComponent<AudioSource>().Stop();
				ModFunc.isPlayingMusic = false;
			}
		}
	}

	public static void play(int id, float volume)
	{
		if (!isNotPlay && GameCanvas.isPlaySound)
		{
			start(volume, id);
		}
	}

	public static bool isPlayingSound()
	{
		if (SoundRun == null)
		{
			return false;
		}
		return SoundRun.GetComponent<AudioSource>().isPlaying;
	}

	public static void playMus(int type, float vl, bool loop)
	{
		if (!isNotPlay)
		{
			vl -= 0.3f;
			if (vl <= 0f)
			{
				vl = 0.01f;
			}
			playSoundBGLoop(type, vl);
		}
	}

	public static void playSoundBGLoop(int id, float volume)
	{
		if (GameCanvas.isPlaySound)
		{
			if (id == SoundMn.AIR_SHIP)
			{
				playSound1(id, volume + 0.2f);
			}
			else if (!(SoundBGLoop == null) && !isPlayingSoundBG(id))
			{
				SoundBGLoop.GetComponent<AudioSource>().loop = true;
				SoundBGLoop.GetComponent<AudioSource>().clip = music[id];
				SoundBGLoop.GetComponent<AudioSource>().volume = volume;
				SoundBGLoop.GetComponent<AudioSource>().Play();
			}
		}
	}

	public static void sTopSoundBG()
	{
		SoundBGLoop.GetComponent<AudioSource>().Stop();
		if (ModFunc.isPlayingMusic)
		{
			PlayMusic(UnityEngine.Random.Range(0, 3));
		}
	}

	public static bool isPlayingSoundBG(int id)
	{
		if (SoundBGLoop == null)
		{
			return false;
		}
		return SoundBGLoop.GetComponent<AudioSource>().isPlaying;
	}

	public static void load(string filename, int pos)
	{
		if (Thread.CurrentThread.Name == Main.mainThreadName)
		{
			__load(filename, pos);
		}
		else
		{
			_load(filename, pos);
		}
	}

	private static void _load(string filename, int pos)
	{
		if (status != 0)
		{
			Cout.LogError("CANNOT LOAD AUDIO " + filename + " WHEN LOADING " + filenametemp);
			return;
		}
		filenametemp = filename;
		postem = pos;
		status = 2;
		int i;
		for (i = 0; i < 100; i++)
		{
			Thread.Sleep(5);
			if (status == 0)
			{
				break;
			}
		}
		if (i == 100)
		{
			Cout.LogError("TOO LONG FOR LOAD AUDIO " + filename);
			return;
		}
		Cout.Log("Load Audio " + filename + " done in " + i * 5 + "ms");
	}

	private static void __load(string filename, int pos)
	{
		music[pos] = (AudioClip)Resources.Load(filename, typeof(AudioClip));
		GameObject.Find("Main Camera").AddComponent<AudioSource>();
		player[pos] = GameObject.Find("Main Camera");
	}

	public static void start(float volume, int pos)
	{
		if (Thread.CurrentThread.Name == Main.mainThreadName)
		{
			__start(volume, pos);
		}
		else
		{
			_start(volume, pos);
		}
	}

	public static void _start(float volume, int pos)
	{
		if (status != 0)
		{
			return;
		}
		volumetem = volume;
		postem = pos;
		status = 3;
		for (int i = 0; i < 100; i++)
		{
			Thread.Sleep(5);
			if (status == 0)
			{
				break;
			}
		}
	}

	public static void __start(float volume, int pos)
	{
		if (!(player[pos] == null))
		{
			player[pos].GetComponent<AudioSource>().PlayOneShot(music[pos], volume);
		}
	}

	public static void stop(int pos)
	{
		if (Thread.CurrentThread.Name == Main.mainThreadName)
		{
			__stop(pos);
		}
		else
		{
			_stop(pos);
		}
	}

	public static void _stop(int pos)
	{
		if (status != 0)
		{
			Debug.LogError("CANNOT STOP AUDIO WHEN STOPPING");
			return;
		}
		postem = pos;
		status = 4;
		int i;
		for (i = 0; i < 100; i++)
		{
			Thread.Sleep(5);
			if (status == 0)
			{
				break;
			}
		}
		if (i == 100)
		{
			Debug.LogError("TOO LONG FOR STOP AUDIO");
		}
		else
		{
			Debug.Log("Stop Audio done in " + i * 5 + "ms");
		}
	}

	public static void __stop(int pos)
	{
		if (player[pos] != null)
		{
			player[pos].GetComponent<AudioSource>().Stop();
		}
	}
}
