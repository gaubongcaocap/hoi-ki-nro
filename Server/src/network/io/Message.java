package network.io;



import consts.cn;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;


public class Message
        implements IAdvanceIOMessage {

    public byte command;
    private ByteArrayOutputStream os;
    private DataOutputStream dos;
    private ByteArrayInputStream is;
    private DataInputStream dis;

    public Message(int command) {
        /*  29 */ this((byte) command);
    }

    public Message(byte command) {
        /*  33 */ this.command = command;
        /*  34 */ this.os = new ByteArrayOutputStream();
        /*  35 */ this.dos = new DataOutputStream(this.os);
    }

    public Message(byte command, byte[] data) {
        /*  39 */ this.command = command;
        /*  40 */ this.is = new ByteArrayInputStream(data);
        /*  41 */ this.dis = new DataInputStream(this.is);
    }

    public DataOutputStream writer() {
        /*  46 */ return this.dos;
    }

    public DataInputStream reader() {
        /*  51 */ return this.dis;
    }

    public byte[] getData() {
        /*  56 */ return this.os.toByteArray();
    }

    public void transformData() {
    }

    public void cleanup() {
        try {
            /*  62 */ if (this.is != null) {
                /*  63 */ this.is.close();
            }
            /*  65 */ if (this.os != null) {
                /*  66 */ this.os.close();
            }
            /*  68 */ if (this.dis != null) {
                /*  69 */ this.dis.close();
            }
            /*  71 */ if (this.dos != null) {
                /*  72 */ this.dos.close();
            }
            /*  74 */        } catch (Exception exception) {
        }
    }

    public void dispose() {
        /*  80 */ cleanup();
        /*  81 */ this.dis = null;
        /*  82 */ this.is = null;
        /*  83 */ this.dos = null;
        /*  84 */ this.os = null;
    }

    public int read() throws IOException {
        /*  89 */ return reader().read();
    }

    public int read(byte[] b) throws IOException {
        /*  94 */ return reader().read(b);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        /*  99 */ return reader().read(b, off, len);
    }

    public boolean readBoolean() throws IOException {
        /* 104 */ return reader().readBoolean();
    }

    public byte readByte() throws IOException {
        /* 109 */ return reader().readByte();
    }

    public short readShort() throws IOException {
        /* 114 */ return reader().readShort();
    }

    public int readInt() throws IOException {
        /* 119 */ return reader().readInt();
    }

    public long readLong() throws IOException {
        /* 124 */ return reader().readLong();
    }

    public float readFloat() throws IOException {
        /* 129 */ return reader().readFloat();
    }

    public double readDouble() throws IOException {
        /* 134 */ return reader().readDouble();
    }

    public char readChar() throws IOException {
        /* 139 */ return reader().readChar();
    }

    public String readUTF() throws IOException {
        /* 144 */ return reader().readUTF();
    }

    public void readFully(byte[] b) throws IOException {
        /* 149 */ reader().readFully(b);
    }

    public void readFully(byte[] b, int off, int len) throws IOException {
        /* 154 */ reader().readFully(b, off, len);
    }

    public int readUnsignedByte() throws IOException {
        /* 159 */ return reader().readUnsignedByte();
    }

    public int readUnsignedShort() throws IOException {
        /* 164 */ return reader().readUnsignedShort();
    }

    public void write(byte[] b) throws IOException {
        /* 169 */ writer().write(b);
    }

    public void write(int b) throws IOException {
        /* 174 */ writer().write(b);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        /* 179 */ writer().write(b, off, len);
    }

    public void writeBoolean(boolean v) throws IOException {
        /* 184 */ writer().writeBoolean(v);
    }

    public void writeByte(int v) throws IOException {
        /* 189 */ writer().writeByte(v);
    }

    public void writeBytes(String s) throws IOException {
        /* 194 */ writer().writeBytes(s);
    }

    public void writeChar(int v) throws IOException {
        /* 199 */ writer().writeChar(v);
    }

    public void writeChars(String s) throws IOException {
        /* 204 */ writer().writeChars(s);
    }

    public void writeDouble(double v) throws IOException {
        /* 209 */ writer().writeDouble(v);
    }

    public void writeFloat(float v) throws IOException {
        /* 214 */ writer().writeFloat(v);
    }

    public void writeInt(int v) throws IOException {
        /* 219 */ writer().writeInt(v);
    }

    public void writeLong(long v) throws IOException {
        /* 224 */ writer().writeLong(v);
    }

    public void writeFix(long v) throws IOException {
        if (cn.readInt) {
            writeInt((int) v);
            return;
        }
        writeLong(v);
    }

    public void writeShort(int v) throws IOException {
        /* 229 */ writer().writeShort(v);
    }

    public void writeUTF(String str) throws IOException {
        /* 234 */ writer().writeUTF(str);
    }

    public BufferedImage readImage() throws IOException {
        /* 239 */ int size = readInt();
        /* 240 */ byte[] dataImage = new byte[size];
        /* 241 */ read(dataImage);

        /* 243 */ BufferedImage image = ImageIO.read(new ByteArrayInputStream(dataImage));
        /* 244 */ return image;
    }

    public void writeImage(BufferedImage image, String format) throws IOException {
        /* 249 */ ByteArrayOutputStream baos = new ByteArrayOutputStream();
        /* 250 */ ImageIO.write(image, format, baos);
        /* 251 */ byte[] dataImage = baos.toByteArray();
        /* 252 */ writeInt(dataImage.length);
        /* 253 */ write(dataImage);
    }
}


/* Location:              C:\Users\VoHoangKiet\Downloads\TEA_V5\lib\GirlkunNetwork.jar!\com\girlkun\network\io\Message.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
