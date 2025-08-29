using System;
// Nút hiển thị số 1–6 và nút “Tab”
public class TabCommand
{
    private static Image menu, menu1;
    public int x, y, w, h;
    public string caption;
    public Action action;
    private bool isFocus;

    public TabCommand(string caption, Action action)
    {
        this.caption = caption;
        this.action = action;
        this.w = 20;
        this.h = 20;
    }

    // Nạp ảnh nền cho nút
    public static void loadBG()
    {
        // hai ảnh này bạn phải copy từ client 1 hoặc tự thiết kế (kích thước ~20x20).
        // đặt ảnh vào thư mục res/mainImage (hoặc nơi bạn đang lưu tài nguyên) của client 2.
        menu = GameCanvas.loadImage("/mainImage/img1.png");
        menu1 = GameCanvas.loadImage("/mainImage/img2.png");
    }

    // Vẽ nút: nếu đang focus thì vẽ ảnh menu1, ngược lại vẽ menu
    public void paint(mGraphics g)
    {
        g.drawImage(isFocus ? menu1 : menu, x, y);
        mFont.tahoma_7b_dark.drawString(g, caption, x + w / 2 + 1,
                                        y + h / 2 - mFont.tahoma_7b_dark.getHeight() / 2, 3);
    }

    // Kiểm tra con trỏ chuột / chạm có nằm trong vùng nút không
    public bool isPointerInside()
    {
        isFocus = false;
        if (GameCanvas.isPointerHoldIn(x, y, w, h))
        {
            if (GameCanvas.isPointerDown)
            {
                isFocus = true;
            }
            if (GameCanvas.isPointerClick && GameCanvas.isPointerJustRelease)
            {
                return true;
            }
        }
        return false;
    }

    // Gọi hàm action, đồng thời xóa sự kiện trỏ hiện tại
    public void Invoke()
    {
        GameCanvas.clearAllPointerEvent();
        action?.Invoke();
    }
}
