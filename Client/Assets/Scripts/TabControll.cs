using System;
using System.Linq;
using UnityEngine;
using UnityEngine.SceneManagement;

// Quản lý nút “Tab” và dãy 6 tab
public class TabControll : mScreen
{
    private static TabControll _Instance;
    public static TabControll Instance => _Instance ?? (_Instance = new TabControll());

    private static bool _selectTab;
    public static bool selectTab
    {
        get => _selectTab;
        set => _selectTab = value;
    }

    private static bool _isShow = true;
    public static bool isShow
    {
        get => _isShow;
        set => _isShow = value;
    }

    private static sbyte tabIndex = 0;

    // nút “Tab” bên góc phải
    private static TabCommand firstCommand = new TabCommand("Tab", () => showTabSelect());

    // dãy nút số 1..6
    private static TabCommand[] TransferTab = Enumerable.Range(0, 6)
          .Select(i => new TabCommand((i + 1).ToString(),
                      () => TransferTabIndex((sbyte)(i - 1))))
          .ToArray();

    // tên các scene tương ứng với từng tab
    private static string[] SceneNames = new string[]
    {
        "NROL",
        "NRO2",
        "NRO3",
        "NRO4",
        "NRO5",
        "NRO6"
    };

    // enum TabType tương ứng
    private static TabType[] tabTypes = new TabType[]
    {
        TabType.Tab1, TabType.Tab2,
        TabType.Tab3, TabType.Tab4,
        TabType.Tab5, TabType.Tab6
    };

    public TabControll()
    {
        initCommand();
    }

    // Xác định vị trí nút Tab và các nút số dựa vào kích thước màn hình
    private static void initCommand()
    {
        firstCommand.x = GameCanvas.w - 60;
        firstCommand.y = 0;
        for (int i = 0; i < TransferTab.Length; i++)
        {
            TransferTab[i].x = GameCanvas.w - 210 + i * 25;
            TransferTab[i].y = 0;
        }
    }

    // Vẽ nút “Tab” và hiện dãy số nếu cần
    public override void paint(mGraphics g)
    {
        if (!isShow) return;

        firstCommand.paint(g);
        paintTab(g);

        // hiển thị số tab hiện tại (ví dụ “Tab 1”)
        int currentTabIndex = -1;
        string currentScene = SceneManager.GetActiveScene().name;
        for (int i = 0; i < SceneNames.Length; i++)
        {
            if (SceneNames[i] == currentScene)
            {
                currentTabIndex = i;
                break;
            }
        }
        mFont.tahoma_7b_red.drawString(g,
            "Tab " + (currentTabIndex + 1),
            firstCommand.x + 10,
            firstCommand.y + 25,
            3);
        base.paint(g);
    }

    // Vẽ dãy số 1..6 khi đã bật selectTab
    private void paintTab(mGraphics g)
    {
        if (!selectTab) return;
        foreach (var cmd in TransferTab)
        {
            cmd.paint(g);
        }
    }

    // Chuyển sang tab (scene) khác
    private static void TransferTabIndex(sbyte index)
    {
        // Ẩn scene hiện tại
        Scene current = SceneManager.GetActiveScene();
        // Option: bạn có thể Unload scene hiện tại nếu muốn giải phóng RAM, nhưng sẽ mất state
        // SceneManager.UnloadSceneAsync(current);

        // Tải scene mới theo chế độ additive (không huỷ scene cũ)
        string targetScene = SceneNames[index + 1];
        if (!SceneManager.GetSceneByName(targetScene).isLoaded)
        {
            SceneManager.LoadScene(targetScene, LoadSceneMode.Additive);
        }

        // Kích hoạt scene mới và vô hiệu hóa các scene khác
        for (int i = 0; i < SceneNames.Length; i++)
        {
            Scene scene = SceneManager.GetSceneByName(SceneNames[i]);
            if (scene.isLoaded)
            {
                bool isTarget = (SceneNames[i] == targetScene);
                SceneManager.SetActiveScene(scene);
                foreach (GameObject root in scene.GetRootGameObjects())
                {
                    root.SetActive(isTarget);
                }
            }
        }

        TabManagement.tab = tabTypes[index + 1];
        _selectTab = false;
    }

    // Bật/tắt hiển thị dãy số (khi click vào “Tab”)
    private static void showTabSelect()
    {
        _selectTab = !_selectTab;
    }

    // Kiểm tra pointer (chuột hoặc cảm ứng) có nằm trong khu vực tab hay không,
    // đồng thời gọi action nếu cần
    public bool isPointerHoldInTab()
    {
        if (!isShow) return false;

        if (firstCommand.isPointerInside())
        {
            firstCommand.Invoke();
            return true;
        }

        if (selectTab)
        {
            foreach (var cmd in TransferTab)
            {
                if (cmd.isPointerInside())
                {
                    cmd.Invoke();
                    return true;
                }
            }
        }
        return false;
    }
}
