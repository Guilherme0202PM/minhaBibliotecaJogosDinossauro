import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Fundo {
    private int largura, altura;
    private Image imagem;

    public Fundo(int largura, int altura, String nomeImagem) {
        this.largura = largura;
        this.altura = altura;

        // Tenta carregar a imagem de fundo
        try {
            File arquivoImagem = new File(new File("").getAbsolutePath() + "/img/" + nomeImagem);
            if (arquivoImagem.exists()) {
                imagem = ImageIO.read(arquivoImagem);
            } else {
                throw new IOException("Arquivo de imagem não encontrado");
            }
        } catch (IOException e) {
            imagem = null; // Caso a imagem não seja encontrada, pode usar uma cor sólida ou tratar de outra forma
        }
    }

    public void desenhar(Graphics g) {
        if (imagem != null) {
            g.drawImage(imagem, 0, 0, largura, altura, null); // Desenha a imagem no fundo
        } else {
            g.setColor(java.awt.Color.CYAN); // Caso não tenha imagem, preenche o fundo com uma cor
            g.fillRect(0, 0, largura, altura);
        }
    }

    public int getLargura() {
        return largura;
    }

    public int getAltura() {
        return altura;
    }
}