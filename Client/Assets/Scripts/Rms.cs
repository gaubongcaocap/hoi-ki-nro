using System;
using System.IO;
using System.Threading;
using UnityEngine;

public class Rms
{
	public static int status;

	public static sbyte[] data;

	public static string filename;

	public static void saveRMS(string filename, sbyte[] data)
	{
		if (Thread.CurrentThread.Name == Main.mainThreadName)
		{
			__saveRMS(filename, data);
		}
		else
		{
			_saveRMS(filename, data);
		}
	}

	public static sbyte[] loadRMS(string filename)
	{
		if (Thread.CurrentThread.Name == Main.mainThreadName)
		{
			return __loadRMS(filename);
		}
		return _loadRMS(filename);
	}

	public static string loadRMSString(string fileName)
	{
		sbyte[] array = loadRMS(fileName);
		if (array == null)
		{
			return null;
		}
		DataInputStream dataInputStream = new DataInputStream(array);
		try
		{
			string result = dataInputStream.readUTF();
			dataInputStream.close();
			return result;
		}
		catch (Exception ex)
		{
			Cout.println(ex.StackTrace);
		}
		return null;
	}

	public static void saveRMSString(string filename, string data)
	{
		DataOutputStream dataOutputStream = new DataOutputStream();
		try
		{
			dataOutputStream.writeUTF(data);
			saveRMS(filename, dataOutputStream.toByteArray());
			dataOutputStream.close();
		}
		catch (Exception ex)
		{
			Cout.println(ex.StackTrace);
		}
	}

	private static void _saveRMS(string filename, sbyte[] data)
	{
		if (status != 0)
		{
			Debug.LogError("Cannot save RMS " + filename + " because current is saving " + Rms.filename);
			return;
		}
		Rms.filename = filename;
		Rms.data = data;
		status = 2;
		int i;
		for (i = 0; i < 500; i++)
		{
			Thread.Sleep(5);
			if (status == 0)
			{
				break;
			}
		}
		if (i == 500)
		{
			Debug.LogError("TOO LONG TO SAVE RMS " + filename);
		}
	}

	private static sbyte[] _loadRMS(string filename)
	{
		if (status != 0)
		{
			Debug.LogError("Cannot load RMS " + filename + " because current is loading " + Rms.filename);
			return null;
		}
		Rms.filename = filename;
		data = null;
		status = 3;
		int i;
		for (i = 0; i < 500; i++)
		{
			Thread.Sleep(5);
			if (status == 0)
			{
				break;
			}
		}
		if (i == 500)
		{
			Debug.LogError("TOO LONG TO LOAD RMS " + filename);
		}
		return data;
	}

	public static void update()
	{
		if (status == 2)
		{
			status = 1;
			__saveRMS(filename, data);
			status = 0;
		}
		else if (status == 3)
		{
			status = 1;
			data = __loadRMS(filename);
			status = 0;
		}
	}

	public static int loadRMSInt(string file)
	{
		sbyte[] array = loadRMS(file);
		if (array == null)
		{
			return -1;
		}
		return array[0];
	}

	public static void saveRMSInt(string file, int x)
	{
		// nếu chỉ là flag 0/1 thì ép rõ ràng:
		sbyte v = (sbyte)((x != 0) ? 1 : 0);
		saveRMS(file, new sbyte[]{ v });
	}

	public static string GetiPhoneDocumentsPath()
	{
		return Application.persistentDataPath;
	}

	private static void __saveRMS(string filename, sbyte[] data)
	{
		var path = Path.Combine(GetiPhoneDocumentsPath(), filename);
		Directory.CreateDirectory(Path.GetDirectoryName(path));
		using (var fs = new FileStream(path, FileMode.Create, FileAccess.Write, FileShare.None))
		{
			var bytes = ArrayCast.cast(data);
			fs.Write(bytes, 0, bytes.Length);
			fs.Flush(true);
		}
		Main.setBackupIcloud(path);
	}

	private static sbyte[] __loadRMS(string filename)
	{
		try
		{
			var path = Path.Combine(GetiPhoneDocumentsPath(), filename);
			if (!File.Exists(path)) return null;
			byte[] bytes;
			using (var fs = new FileStream(path, FileMode.Open, FileAccess.Read, FileShare.Read))
			{
				bytes = new byte[fs.Length];
				fs.Read(bytes, 0, bytes.Length);
			}
			return ArrayCast.cast(bytes);
		}
		catch
		{
			return null;
		}
	}

	public static void clearAll()
	{
		Cout.LogError3("clean rms");
		FileInfo[] files = new DirectoryInfo(GetiPhoneDocumentsPath() + "/").GetFiles();
		for (int i = 0; i < files.Length; i++)
		{
			files[i].Delete();
		}
	}

	public static void DeleteStorage(string path)
	{
		try
		{
			File.Delete(GetiPhoneDocumentsPath() + "/" + path);
		}
		catch (Exception)
		{
		}
	}
}
