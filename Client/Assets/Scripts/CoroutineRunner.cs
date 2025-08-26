using System.Collections;
using UnityEngine;

public class CoroutineRunner : MonoBehaviour
{
	private static CoroutineRunner _instance;

	public static CoroutineRunner Instance
	{
		get
		{
			if (_instance == null)
			{
				GameObject gameObject = new GameObject("CoroutineRunner");
				_instance = gameObject.AddComponent<CoroutineRunner>();
				Object.DontDestroyOnLoad(gameObject);
			}
			return _instance;
		}
	}

	public void RunCoroutine(IEnumerator coroutine)
	{
		StartCoroutine(coroutine);
	}
}
