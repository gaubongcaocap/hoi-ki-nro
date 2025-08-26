using System;
using UnityEngine;
using UnityEngine.Video;
using UnityEngine.SceneManagement;
// Token: 0x020000F1 RID: 241
public class VideoScript : MonoBehaviour
{
    // Token: 0x17000006 RID: 6
    // (get) Token: 0x06000C38 RID: 3128 RVA: 0x000AA9F0 File Offset: 0x000A8BF0
    // (set) Token: 0x06000C37 RID: 3127 RVA: 0x000AA9E8 File Offset: 0x000A8BE8
    public static VideoScript instance { get; private set; }
    private Renderer videoRenderer;
    // Token: 0x06000C39 RID: 3129 RVA: 0x000AA9F7 File Offset: 0x000A8BF7
    private void Awake()
    {
        if (VideoScript.instance == null)
        {
            VideoScript.instance = this;
        }
        else
        {
            Destroy(gameObject); // Hủy bỏ nếu đã có một instance
        }
    }


    // Token: 0x06000C3A RID: 3130 RVA: 0x000AAA0C File Offset: 0x000A8C0C
    private void Start()
    {
        if (video == null)
        {
            video = Camera.main?.GetComponent<VideoPlayer>(); // Dấu ? để tránh lỗi null
        }

        if (video != null)
        {
            video.Prepare(); // Chuẩn bị video trước khi phát
            video.loopPointReached += OnVideoEnd;
            video.Play();
            videoRenderer = GetComponent<Renderer>();
        }
        else
        {
            Debug.LogError("Không tìm thấy VideoPlayer trên Main Camera!");
        }
    }

    // Token: 0x06000C3B RID: 3131 RVA: 0x000AAA38 File Offset: 0x000A8C38
    public void paint()
    {
        if (video != null && video.texture != null)
        {
            GUI.DrawTexture(new Rect(0f, 0f, Screen.width, Screen.height), video.texture);
        }
    }

    private void OnVideoEnd(VideoPlayer vp)
    {
        Debug.Log("Video đã kết thúc!");

        vp.Stop(); // Dừng video
        vp.targetTexture = null; // Xóa texture để tránh giữ frame cuối

        if (videoRenderer != null)
        {
            videoRenderer.enabled = false; // Ẩn Renderer nếu có
        }

        gameObject.SetActive(false); // Ẩn toàn bộ GameObject chứa VideoPlayer
    }
    private void OnGUI()
    {
        if (video != null)
        {
            paint();
        }
    }

    // Token: 0x04001546 RID: 5446
    public VideoPlayer video;

    // Token: 0x04001547 RID: 5447
    public Texture2D videoFrames;
}
