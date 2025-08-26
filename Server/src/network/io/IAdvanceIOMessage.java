package network.io;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface IAdvanceIOMessage extends IMessage {
  BufferedImage readImage() throws IOException;
  
  void writeImage(BufferedImage paramBufferedImage, String paramString) throws IOException;
}


/* Location:              C:\Users\VoHoangKiet\Downloads\TEA_V5\lib\GirlkunNetwork.jar!\com\girlkun\network\io\IAdvanceIOMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */