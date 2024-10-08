import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Utility {

    public static Image readImage(String fileName) {
        Image img = null;

        try {
            img = ImageIO.read(new File(fileName));
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        return img;
    }

}
