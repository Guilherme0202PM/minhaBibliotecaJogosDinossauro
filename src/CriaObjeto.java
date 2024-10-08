import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Image;
import java.awt.geom.AffineTransform;

public abstract class CriaObjeto {
    protected int x, y;
    protected int largura, altura; // Adicionando largura e altura separadas
    protected Image imagem;
    protected double angulo; // Ângulo de rotação do objeto

    public CriaObjeto(int x, int y, int largura, int altura, String nomeImagem) {
        this.x = x;
        this.y = y;
        this.largura = largura;
        this.altura = altura;
        this.angulo = 90; // Inicialmente, o ângulo de rotação é 90

        // Tenta carregar a imagem, caso contrário, usa um círculo azul
        try {
            File arquivoImagem = new File(new File("").getAbsolutePath() + "/img/" + nomeImagem);
            if (arquivoImagem.exists()) {
                imagem = ImageIO.read(arquivoImagem);
            } else {
                throw new IOException("Arquivo não encontrado: " + nomeImagem);
            }
        } catch (IOException e) {
            // Em caso de erro, define a imagem como null
            imagem = null;
            System.err.println("Erro ao carregar imagem: " + e.getMessage());
        }
    }

    // Método para atualizar a imagem do objeto
    public void setImagem(Image novaImagem) {
        this.imagem = novaImagem;
    }

    public void desenhar(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform originalTransform = g2d.getTransform(); // Salva a transformação original

        if (imagem != null) {
            // Use a largura e altura do objeto em vez de calcular
            int larguraImagem = imagem.getWidth(null);
            int alturaImagem = imagem.getHeight(null);

            // Aplica a rotação no sentido horário
            AffineTransform transform = new AffineTransform();
            transform.rotate(Math.toRadians(angulo - 90), x + largura / 2.0, y + altura / 2.0);
            g2d.setTransform(transform);

            g2d.drawImage(imagem, x, y, largura, altura, null);
        } else {
            g2d.setColor(Color.BLUE); // Cor padrão caso não tenha imagem
            g2d.fillOval(x, y, largura, altura); // Desenha um círculo azul com largura e altura
        }

        g2d.setTransform(originalTransform); // Restaura a transformação original
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, largura, altura);
    }

    public int getLargura() {
        return largura;
    }

    public int getAltura() {
        return altura;
    }

    public void setLargura(int largura) {
        this.largura = largura;
    }

    public void setAltura(int altura) {
        this.altura = altura;
    }

    public void girarDireita(double graus) {
        angulo = (angulo + graus) % 360;
    }

    public void girarEsquerda(double graus) {
        angulo = (angulo - graus + 360) % 360;
    }

    public void apontarDirecao(double angulo) {
        this.angulo = angulo % 360;
    }

    public Point eixoObjeto() {
        int centerX = x + largura / 2;
        int centerY = y + altura / 2;
        return new Point(centerX, centerY);
    }

    public void exibirEixoObjeto(Graphics g, boolean showEixo) {
        if (showEixo) {
            Point eixo = eixoObjeto();
            g.setColor(Color.RED);
            g.fillOval(eixo.x - 1, eixo.y - 1, 20, 20); // Desenha o círculo vermelho
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Color getCor() {
        // Supondo que todos os objetos tenham uma cor associada (caso não tenha imagem)
        return imagem != null ? new Color(255, 255, 255) : Color.BLUE;
    }

    public Image getImagem() {
        return imagem;
    }
}
