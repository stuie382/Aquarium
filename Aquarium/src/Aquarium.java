import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Aquarium extends Frame implements Runnable {

   private static final long serialVersionUID = 6613901281694354973L;
   private Image             aquariumImage, memoryImage;
   private Image[]           fishImages       = new Image[2];
   private Thread            thread;
   private MediaTracker      tracker;
   private Graphics          memoryGraphics;
   private int               numberFish       = 12;
   private int               sleepTime        = 60;
   private Fish[]            fishes           = new Fish[numberFish];
   private boolean           runOK            = true;

   public static void main(String[] args) throws IOException {
      new Aquarium();
   }

   public Aquarium() throws IOException {
      setTitle("The Aquarium");

      tracker = new MediaTracker(this);

      fishImages[0] = ImageIO.read(getClass().getResourceAsStream("fish1.gif"));
      tracker.addImage(fishImages[0], 0);

      fishImages[1] = ImageIO.read(getClass().getResourceAsStream("fish2.gif"));
      tracker.addImage(fishImages[1], 0);

      aquariumImage = ImageIO.read(getClass().getResourceAsStream("tank.png"));
      tracker.addImage(aquariumImage, 0);

      try {
         tracker.waitForID(0);
      } catch (Exception ex) {
         System.out.println(ex.getMessage());
      }

      setSize(aquariumImage.getWidth(this), aquariumImage.getHeight(this));

      setResizable(true);
      setVisible(true);

      memoryImage = createImage(getSize().width, getSize().height);
      memoryGraphics = memoryImage.getGraphics();

      thread = new Thread(this);
      thread.start();

      this.addWindowListener(new WindowAdapter() {

         @Override
         public void windowClosing(WindowEvent windowEvent) {
            runOK = false;
            System.exit(0);
         }
      });
   }

   @Override
   public void run() {
      Rectangle edges = new Rectangle(0 + getInsets().left,
                                      0 + getInsets().top,
                                      getSize().width - (getInsets().left + getInsets().right),
                                      getSize().height - (getInsets().top + getInsets().bottom));
      for (int loopIndex = 0; loopIndex < numberFish; loopIndex++) {
         fishes[loopIndex] = new Fish(fishImages[0], fishImages[1], edges, this);
         try {
            Thread.sleep(20);
         } catch (Exception exp) {
            System.out.println(exp.getMessage());
         }
      }
      Fish fish;
      while (runOK) {
         for (int loopIndex = 0; loopIndex < numberFish; loopIndex++) {
            fish = fishes[loopIndex];
            fish.swim();
         }
         try {
            Thread.sleep(sleepTime);
         } catch (Exception exp) {
            System.out.println(exp.getMessage());
         }
         repaint();
      }
   }

   @Override
   public void update(Graphics g) {
      memoryGraphics.drawImage(aquariumImage, 0, 0, this);

      for (int loopIndex = 0; loopIndex < numberFish; loopIndex++) {
         fishes[loopIndex].drawFishImage(memoryGraphics);
      }
      g.drawImage(memoryImage, 0, 0, this);
   }
}
