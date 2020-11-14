package pojo;

import android.graphics.Bitmap;

public class Msg {
    public static final int Type_Recived = 0;
    public static final int Type_Send = 1;
    private String content;
    private int type;
    private Bitmap receive_pic;
    private Bitmap send_pic;

    public Msg(String content, int type, Bitmap receive_pic, Bitmap send_pic) {
        this.content = content;
        this.type = type;
        this.send_pic = send_pic;
        this.receive_pic = receive_pic;
    }

    public Bitmap getReceive_pic() {
        return receive_pic;
    }

    public Bitmap getSend_pic() {
        return send_pic;
    }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }

}
