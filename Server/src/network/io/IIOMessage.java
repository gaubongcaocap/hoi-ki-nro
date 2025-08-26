package network.io;

import java.io.IOException;

public interface IIOMessage {
  int read() throws IOException;
  
  int read(byte[] paramArrayOfbyte) throws IOException;
  
  int read(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException;
  
  boolean readBoolean() throws IOException;
  
  byte readByte() throws IOException;
  
  short readShort() throws IOException;
  
  int readInt() throws IOException;
  
  long readLong() throws IOException;
  
  float readFloat() throws IOException;
  
  double readDouble() throws IOException;
  
  char readChar() throws IOException;
  
  String readUTF() throws IOException;
  
  void readFully(byte[] paramArrayOfbyte) throws IOException;
  
  void readFully(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException;
  
  int readUnsignedByte() throws IOException;
  
  int readUnsignedShort() throws IOException;
  
  void write(byte[] paramArrayOfbyte) throws IOException;
  
  void write(int paramInt) throws IOException;
  
  void write(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException;
  
  void writeBoolean(boolean paramBoolean) throws IOException;
  
  void writeByte(int paramInt) throws IOException;
  
  void writeBytes(String paramString) throws IOException;
  
  void writeChar(int paramInt) throws IOException;
  
  void writeChars(String paramString) throws IOException;
  
  void writeDouble(double paramDouble) throws IOException;
  
  void writeFloat(float paramFloat) throws IOException;
  
  void writeInt(int paramInt) throws IOException;
  
  void writeLong(long paramLong) throws IOException;
  
  void writeShort(int paramInt) throws IOException;
  
  void writeUTF(String paramString) throws IOException;
}


/* Location:              C:\Users\VoHoangKiet\Downloads\TEA_V5\lib\GirlkunNetwork.jar!\com\girlkun\network\io\IIOMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */