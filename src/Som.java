import java.applet.AudioClip;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Som {
    private Clip clip;

    public Som(String arquivoSom) {
        try {
            File arquivo = new File(new File("").getAbsolutePath() + "/som/" + arquivoSom);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(arquivo));
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedInputStream);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
        } catch (Exception e) {
            System.out.println("Erro ao carregar som: " + e.getMessage());
        }
    }

    public void tocarSom() {
        if (clip!= null) {
            clip.setFramePosition(0); // volta ao inicio do som
            clip.start(); // toca o som
        }
    }
}