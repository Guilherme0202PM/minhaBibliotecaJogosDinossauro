import java.awt.Image;
import java.awt.Graphics;
import javax.swing.ImageIcon;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Sprite {
    private Image imagemObjeto; // Imagem original
    private Image imagemAtual;  // Imagem que está sendo exibida atualmente
    private Image imagemNova;   // Nova imagem para trocar
    private String baseName;    // Base para animação (ex: "voa")
    private int numFrames;      // Número de frames na animação
    private int frameDelay;     // Delay entre frames em milissegundos
    private long startTime;     // Tempo de início da animação

    public Sprite(String nomeImagemObjeto) {
        this.imagemObjeto = carregarImagem(nomeImagemObjeto);
        this.imagemAtual = imagemObjeto;
        this.imagemNova = imagemObjeto;
    }

    // Carrega uma imagem a partir do caminho especificado
    private Image carregarImagem(String nomeImagem) {
        try {
            File arquivoImagem = new File(new File("").getAbsolutePath() + "/img/" + nomeImagem);
            if (arquivoImagem.exists()) {
                return ImageIO.read(arquivoImagem);
            } else {
                throw new IOException("Arquivo de imagem não encontrado: " + nomeImagem);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Retorna null em caso de erro
        }
    }

    // Método para trocar a imagem atual por uma nova imagem
    public void mudaSprite(String novaImagem) {
        this.imagemNova = carregarImagem(novaImagem);
        if (this.imagemNova != null) {
            this.imagemAtual = this.imagemNova;
        }
    }

    // Método para resetar para a imagem original
    public void resetSprite() {
        this.imagemAtual = this.imagemObjeto;
    }

    // Método para iniciar uma animação
    public void iniciarAnimacao(String baseName, int numFrames, int frameDelay) {
        this.baseName = baseName;
        this.numFrames = numFrames;
        this.frameDelay = frameDelay;
        this.startTime = System.currentTimeMillis();
    }

    // Método para gerenciar a animação de sprites
    public void animacaoSprite(Graphics g, int x, int y, int largura, int altura) {
        if (baseName == null || numFrames == 0) {
            // Sem animação definida, desenha a imagem atual
            if (imagemAtual != null) {
                g.drawImage(imagemAtual, x, y, largura, altura, null);
            }
            return;
        }

        long elapsed = System.currentTimeMillis() - startTime;
        int frame = (int) ((elapsed / frameDelay) % numFrames) + 1;
        String imageName = baseName + frame + ".png"; // Ex: voa1.png, voa2.png, etc.

        Image animImage = carregarImagem(imageName);
        if (animImage != null) {
            g.drawImage(animImage, x, y, largura, altura, null);
        } else {
            // Se a imagem de animação não for encontrada, desenha a imagem atual
            if (imagemAtual != null) {
                g.drawImage(imagemAtual, x, y, largura, altura, null);
            }
        }
    }

    // Retorna a imagem atual
    public Image getImagemAtual() {
        return imagemAtual;
    }

    // Atualiza o tempo de início da animação
    public void atualizarTempoAnimacao() {
        this.startTime = System.currentTimeMillis();
    }
}
