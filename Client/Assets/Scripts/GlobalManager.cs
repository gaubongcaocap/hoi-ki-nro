using System.Collections;
using UnityEngine;

public class GlobalManager : MonoBehaviour
{
    private static GlobalManager instance;
    public static GlobalManager Instance => instance;

    private TabState[] tabStates = new TabState[6];

    void Awake()
    {
        if (instance != null && instance != this)
        {
            Destroy(gameObject);
            return;
        }
        instance = this;
        DontDestroyOnLoad(gameObject);
    }

    public TabState GetTabState(int index)
    {
        if (tabStates[index] == null)
            tabStates[index] = new TabState();
        return tabStates[index];
    }
}

[System.Serializable]
public class TabState
{
    public int playerHP;
    public Vector3 playerPosition;
    public int[] inventory;
}

