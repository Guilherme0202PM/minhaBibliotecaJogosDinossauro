import java.awt.*;
import java.awt.image.BufferedImage;
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

    private BufferedImage imagemFiltrada; // Imagem com filtro aplicado
    private boolean filtroAtivo = false;

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

    // Metodo para trocar a imagem atual por uma nova imagem
    public void mudaSprite(String novaImagem) {
        this.imagemNova = carregarImagem(novaImagem);
        if (this.imagemNova != null) {
            this.imagemAtual = this.imagemNova;
        }
    }

    // Metodo para resetar para a imagem original
    public void resetSprite() {
        this.imagemAtual = this.imagemObjeto;
    }

    // Metodo para iniciar uma animação
    public void iniciarAnimacao(String baseName, int numFrames, int frameDelay) {
        this.baseName = baseName;
        this.numFrames = numFrames;
        this.frameDelay = frameDelay;
        this.startTime = System.currentTimeMillis();
    }

    // Metodo para gerenciar a animação de sprites
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

    // Metodo para aplicar o filtro de preto e branco
    public void aplicarFiltroPretoBranco() {
        if (imagemObjeto instanceof BufferedImage) {
            BufferedImage original = (BufferedImage) imagemObjeto;
            int largura = original.getWidth();
            int altura = original.getHeight();
            imagemFiltrada = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_ARGB);

            for (int x = 0; x < largura; x++) {
                for (int y = 0; y < altura; y++) {
                    int corOriginal = original.getRGB(x, y);
                    Color cor = new Color(corOriginal, true);

                    // Calcula o tom de cinza
                    int cinza = (int) (cor.getRed() * 0.3 + cor.getGreen() * 0.59 + cor.getBlue() * 0.11);
                    Color novaCor = new Color(cinza, cinza, cinza, cor.getAlpha());

                    imagemFiltrada.setRGB(x, y, novaCor.getRGB());
                }
            }
            imagemAtual = imagemFiltrada;
            filtroAtivo = true;
        }
    }

    // Metodo para remover o filtro
    public void removerFiltro() {
        imagemAtual = imagemObjeto;
        filtroAtivo = false;
    }

    // Metodo para desenhar a imagem atual
    public void desenhar(Graphics g, int x, int y, int largura, int altura) {
        g.drawImage(imagemAtual, x, y, largura, altura, null);
    }

    // Retorna o estado do filtro
    public boolean isFiltroAtivo() {
        return filtroAtivo;
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
