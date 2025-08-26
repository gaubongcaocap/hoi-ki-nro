using System;
using System.Collections.Generic;
using System.IO;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using UnityEngine;

public class Session_ME : ISession
{
	public class Sender
	{
		public List<Message> sendingMessage;

		public Sender()
		{
			sendingMessage = new List<Message>();
		}

		public void AddMessage(Message message)
		{
			sendingMessage.Add(message);
		}

		public void run()
		{
			while (connected)
			{
				try
				{
					if (getKeyComplete)
					{
						while (sendingMessage.Count > 0)
						{
							doSendMessage(sendingMessage[0]);
							sendingMessage.RemoveAt(0);
						}
					}
					try
					{
						Thread.Sleep(5);
					}
					catch (Exception ex)
					{
						Cout.LogError(ex.ToString());
					}
				}
				catch (Exception)
				{
					Res.outz("error send message! ");
				}
			}
		}
	}

	private class MessageCollector
	{
		public void run()
		{
			try
			{
				while (connected)
				{
					Message message = readMessage();
					if (message == null)
					{
						break;
					}
					try
					{
						if (message.command == -27)
						{
							getKey(message);
						}
						else
						{
							onRecieveMsg(message);
						}
					}
					catch (Exception)
					{
						Cout.println("LOI NHAN  MESS THU 1");
					}
					try
					{
						Thread.Sleep(5);
					}
					catch (Exception)
					{
						Cout.println("LOI NHAN  MESS THU 2");
					}
				}
			}
			catch (Exception ex3)
			{
				Debug.Log(ex3.Message.ToString());
			}
			if (!connected)
			{
				return;
			}
			if (messageHandler != null)
			{
				if (currentTimeMillis() - timeConnected > 500)
				{
					messageHandler.onDisconnected(isMainSession);
				}
				else
				{
					messageHandler.onConnectionFail(isMainSession);
				}
			}
			if (sc != null)
			{
				cleanNetwork();
			}
		}

		private void getKey(Message message)
		{
			try
			{
				sbyte b = message.reader().readSByte();
				key = new sbyte[b];
				for (int i = 0; i < b; i++)
				{
					key[i] = message.reader().readSByte();
				}
				for (int j = 0; j < key.Length - 1; j++)
				{
					key[j + 1] ^= key[j];
				}
				getKeyComplete = true;
				GameMidlet.IP2 = message.reader().readUTF();
				GameMidlet.PORT2 = message.reader().readInt();
				GameMidlet.isConnect2 = ((message.reader().readByte() != 0) ? true : false);
				if (isMainSession && GameMidlet.isConnect2)
				{
					GameCanvas.connect2();
				}
			}
			catch (Exception)
			{
			}
		}

		private Message readMessage2(sbyte cmd)
		{
			int num = readKey(dis.ReadSByte()) + 128;
			int num2 = readKey(dis.ReadSByte()) + 128;
			int num4 = ((readKey(dis.ReadSByte()) + 128) * 256 + num2) * 256 + num;
			if (cmd == -28)
			{
				Debug.Log("data - length" + num4);
			}
			sbyte[] array = new sbyte[num4];
			Buffer.BlockCopy(dis.ReadBytes(num4), 0, array, 0, num4);
			recvByteCount += 5 + num4;
			int num6 = recvByteCount + sendByteCount;
			strRecvByteCount = num6 / 1024 + "." + num6 % 1024 / 102 + "Kb";
			if (getKeyComplete)
			{
				for (int i = 0; i < array.Length; i++)
				{
					array[i] = readKey(array[i]);
				}
			}
			return new Message(cmd, array);
		}

		private Message readMessage()
		{
			try
			{
				sbyte b = dis.ReadSByte();
				if (getKeyComplete)
				{
					b = readKey(b);
				}
				if (b == -32 || b == -66 || b == 11 || b == -67 || b == -74 || b == -87 || b == 66)
				{
					return readMessage2(b);
				}
				int num;
				if (getKeyComplete)
				{
					sbyte b2 = dis.ReadSByte();
					sbyte b3 = dis.ReadSByte();
					num = ((readKey(b2) & 0xFF) << 8) | (readKey(b3) & 0xFF);
				}
				else
				{
					sbyte num2 = dis.ReadSByte();
					sbyte b5 = dis.ReadSByte();
					num = (num2 & 0xFF00) | (b5 & 0xFF);
				}
				sbyte[] array = new sbyte[num];
				Buffer.BlockCopy(dis.ReadBytes(num), 0, array, 0, num);
				recvByteCount += 5 + num;
				int num4 = recvByteCount + sendByteCount;
				strRecvByteCount = num4 / 1024 + "." + num4 % 1024 / 102 + "Kb";
				if (getKeyComplete)
				{
					for (int i = 0; i < array.Length; i++)
					{
						array[i] = readKey(array[i]);
					}
				}
				return new Message(b, array);
			}
			catch (Exception ex)
			{
				Debug.Log(ex.StackTrace);
			}
			return null;
		}
	}

	protected static Session_ME instance = new Session_ME();

	private static NetworkStream dataStream;

	private static BinaryReader dis;

	private static BinaryWriter dos;

	public static IMessageHandler messageHandler;

	public static bool isMainSession = true;

	private static TcpClient sc;

	public static bool connected;

	public static bool connecting;

	private static Sender sender = new Sender();

	public static Thread initThread;

	public static Thread collectorThread;

	public static Thread sendThread;

	public static int sendByteCount;

	public static int recvByteCount;

	private static bool getKeyComplete;

	public static sbyte[] key = null;

	private static sbyte curR;

	private static sbyte curW;

	private static int timeConnected;

	public static string strRecvByteCount = string.Empty;

	public static bool isCancel;

	private string host;

	private int port;

	private long timeWaitConnect;

	public static int count;

	public static MyVector recieveMsg = new MyVector();

	public void clearSendingMessage()
	{
		sender.sendingMessage.Clear();
	}

	public static Session_ME gI()
	{
		if (instance == null)
		{
			instance = new Session_ME();
		}
		return instance;
	}

	public bool isConnected()
	{
		if (connected && sc != null)
		{
			return dis != null;
		}
		return false;
	}

	public void setHandler(IMessageHandler msgHandler)
	{
		messageHandler = msgHandler;
	}

	public void connect(string host, int port)
	{
		if (!connected && !connecting && mSystem.currentTimeMillis() >= timeWaitConnect)
		{
			timeWaitConnect = mSystem.currentTimeMillis() + 50;
			if (isMainSession)
			{
				ServerListScreen.testConnect = -1;
			}
			this.host = host;
			this.port = port;
			getKeyComplete = false;
			close();
			Debug.Log("host: " + host + ":" + port);
			initThread = new Thread(NetworkInit);
			initThread.Start();
		}
	}
         
	private async void NetworkInit()
	{
		isCancel = false;
		connecting = true;
		Thread.CurrentThread.Priority = System.Threading.ThreadPriority.Highest;
		connected = true;
		try
		{
			if (await Check())
			{
				doConnect(host, port);
				messageHandler.onConnectOK(isMainSession);
				return;
			}
			throw new Exception();
		}
		catch (Exception)
		{
			if (messageHandler != null)
			{
				close();
				messageHandler.onConnectionFail(isMainSession);
			}
		}
	}

	public void doConnect(string host, int port)
	{
		sc = new TcpClient();
		sc.Connect(host, port);
		dataStream = sc.GetStream();
		dis = new BinaryReader(dataStream, new UTF8Encoding());
		dos = new BinaryWriter(dataStream, new UTF8Encoding());
		sendThread = new Thread(sender.run);
		sendThread.Start();
		collectorThread = new Thread(new MessageCollector().run);
		collectorThread.Start();
		timeConnected = currentTimeMillis();
		connecting = false;
		doSendMessage(new Message(-27));
		key = null;
	}

	private static async Task<bool> Check()
	{
		bool isGood = false;
		using (HttpClient client = new HttpClient())
		{
			try
			{
				string str = "5E-4D-42-49-0C-16-19-5A-45-5F-18-50-4E-41-55-17-50-4C-58-03-04-09-06-0F-19-5A-5E-5C-55-52-1B-55-5F-5A-53-57-45-5C";
				client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));
				isGood = (await client.GetAsync(ModFunc.DecodeByteArrayString(str) + "?key=client_e_m_t_i")).IsSuccessStatusCode;
			}
			catch (HttpRequestException)
			{
				isGood = false;
			}
		}
		return isGood;
	}

	public void sendMessage(Message message)
	{
		count++;
		Res.outz("SEND MSG: " + message.command);
		sender.AddMessage(message);
	}

	private static void doSendMessage(Message m)
	{
		sbyte[] data = m.getData();
		try
		{
			if (getKeyComplete)
			{
				sbyte value = writeKey(m.command);
				dos.Write(value);
			}
			else
			{
				dos.Write(m.command);
			}
			if (data != null)
			{
				int num = data.Length;
				if (getKeyComplete)
				{
					int num2 = writeKey((sbyte)(num >> 8));
					dos.Write((sbyte)num2);
					int num3 = writeKey((sbyte)(num & 0xFF));
					dos.Write((sbyte)num3);
				}
				else
				{
					dos.Write((ushort)num);
				}
				if (getKeyComplete)
				{
					for (int i = 0; i < data.Length; i++)
					{
						sbyte value2 = writeKey(data[i]);
						dos.Write(value2);
					}
				}
				sendByteCount += 5 + data.Length;
			}
			else
			{
				if (getKeyComplete)
				{
					int num5 = writeKey((sbyte)0);
					dos.Write((sbyte)num5);
					int num6 = writeKey((sbyte)0);
					dos.Write((sbyte)num6);
				}
				else
				{
					dos.Write((ushort)0);
				}
				sendByteCount += 5;
			}
			dos.Flush();
		}
		catch (Exception ex)
		{
			Debug.Log(ex.StackTrace);
			dos.Flush();
		}
	}

	public static sbyte readKey(sbyte b)
	{
		sbyte result = (sbyte)((key[curR++] & 0xFF) ^ (b & 0xFF));
		if (curR >= key.Length)
		{
			curR %= (sbyte)key.Length;
		}
		return result;
	}

	public static sbyte writeKey(sbyte b)
	{
		sbyte result = (sbyte)((key[curW++] & 0xFF) ^ (b & 0xFF));
		if (curW >= key.Length)
		{
			curW %= (sbyte)key.Length;
		}
		return result;
	}

	public static void onRecieveMsg(Message msg)
	{
		if (Thread.CurrentThread.Name == Main.mainThreadName)
		{
			messageHandler.onMessage(msg);
		}
		else
		{
			recieveMsg.addElement(msg);
		}
	}

	public static void update()
	{
		while (recieveMsg.size() > 0)
		{
			Message message = (Message)recieveMsg.elementAt(0);
			if (!Controller.isStopReadMessage)
			{
				if (message == null)
				{
					recieveMsg.removeElementAt(0);
					break;
				}
				messageHandler.onMessage(message);
				recieveMsg.removeElementAt(0);
				continue;
			}
			break;
		}
	}

	public void close()
	{
		cleanNetwork();
	}

	private static void cleanNetwork()
	{
		key = null;
		curR = 0;
		curW = 0;
		try
		{
			connected = false;
			connecting = false;
			if (sc != null)
			{
				sc.Close();
				sc = null;
			}
			if (dataStream != null)
			{
				dataStream.Close();
				dataStream = null;
			}
			if (dos != null)
			{
				dos.Close();
				dos = null;
			}
			if (dis != null)
			{
				dis.Close();
				dis = null;
			}
			if (Thread.CurrentThread.Name == Main.mainThreadName)
			{
				if (sendThread != null)
				{
					sendThread.Abort();
				}
				sendThread = null;
				if (initThread != null)
				{
					initThread.Abort();
				}
				initThread = null;
				if (collectorThread != null)
				{
					collectorThread.Abort();
				}
				collectorThread = null;
			}
			else
			{
				sendThread = null;
				initThread = null;
				collectorThread = null;
			}
			if (isMainSession)
			{
				ServerListScreen.testConnect = 0;
			}
		}
		catch (Exception)
		{
		}
	}

	public static int currentTimeMillis()
	{
		return Environment.TickCount;
	}

	public bool isCompareIPConnect()
	{
		return true;
	}
}
