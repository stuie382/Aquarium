import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Aquarium extends Frame implements Runnable {

    private static final long serialVersionUID = 6613901281694354973L;
    private Image             aquariumImage;
    private Image             memoryImage;
    private Image[]           fishImages       = new Image[2];
    private Thread            thread;
    private MediaTracker      tracker;
    private Graphics          memoryGraphics;
    private int               numberFish       = 12;
    private int               sleepTime        = 60;
    private Fish[]            fishes           = new Fish[numberFish];
    private boolean           runOK            = true;
    private Clip              tankSounds;

    public static void main(String[] args) throws Exception {
        new Aquarium();
    }

    public Aquarium() throws IOException, UnsupportedAudioFileException,
            LineUnavailableException {
        setTitle("The Aquarium");
        setup();
        runAquarium();

    }

    private void runAquarium() {
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

    private void setup() throws IOException, UnsupportedAudioFileException,
            LineUnavailableException {
        startSounds();
        tracker = new MediaTracker(this);

        fishImages[0] = ImageIO.read(getClass()
                .getResourceAsStream("fish1.gif"));
        tracker.addImage(fishImages[0], 0);

        fishImages[1] = ImageIO.read(getClass()
                .getResourceAsStream("fish2.gif"));
        tracker.addImage(fishImages[1], 0);

        aquariumImage = ImageIO
                .read(getClass().getResourceAsStream("tank.png"));
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
    }

    private void startSounds() throws UnsupportedAudioFileException,
            IOException, LineUnavailableException {
        InputStream in = getClass().getResourceAsStream("tankSounds.wav");

        AudioInputStream audioIn = AudioSystem.getAudioInputStream(in);
        DataLine.Info clipInfo = new DataLine.Info(Clip.class,
                audioIn.getFormat());
        tankSounds = (Clip) AudioSystem.getLine(clipInfo);
        tankSounds.open(audioIn);
        tankSounds.loop(Clip.LOOP_CONTINUOUSLY);
        tankSounds.start();
    }

    @Override
    public void run() {
        Rectangle edges = new Rectangle(0 + getInsets().left,
                0 + getInsets().top, getSize().width
                        - (getInsets().left + getInsets().right),
                getSize().height - (getInsets().top + getInsets().bottom));
        for (int loopIndex = 0; loopIndex < numberFish; loopIndex++) {
            fishes[loopIndex] = new Fish(fishImages[0], fishImages[1], edges,
                    this);
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
