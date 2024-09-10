import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Player extends CriaObjeto {
    private Movimento movimento;
    private Sensores sensores;
    private Som som;
    private GameWindow janela;

    public Player(int x, int y, int largura, int altura, String nomeImagem, Movimento movimento, Sensores sensores, Som som, GameWindow janela) {
        super(x, y, largura, altura, nomeImagem);
        this.movimento = movimento;
        this.sensores = sensores;
        this.som = som;
        this.janela = janela;
    }

    public void adicionarListener() {
        janela.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                int velocidade = 5; // velocidade do movimento

                if (e.getKeyCode() == KeyEvent.VK_A) {
                    movimento.movimentoX(Player.this, -velocidade);
                } else if (e.getKeyCode() == KeyEvent.VK_D) {
                    movimento.movimentoX(Player.this, velocidade);
                } else if (e.getKeyCode() == KeyEvent.VK_W) {
                    movimento.movimentoY(Player.this, -velocidade);
                } else if (e.getKeyCode() == KeyEvent.VK_S) {
                    movimento.movimentoY(Player.this, velocidade);
                }

                if (e.getKeyCode() == KeyEvent.VK_Z) { // quando clico em Z vou para posição aleatória
                    movimento.goPosicaoAleatoria(Player.this, janela);
                }

                if (e.getKeyCode() == KeyEvent.VK_J) {
                    som.tocarSom(); // reutiliza a instância existente
                }

                if (e.getKeyCode() == KeyEvent.VK_X) {
                    movimento.goPosicao(Player.this, 50, 50);
                }

                // Adiciona salto com a tecla de espaço
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    movimento.iniciarSalto(Player.this); // Especificamente para o Player
                }

                // Verifica se o Player está tocando na borda
                if (sensores.tocandoBorda(Player.this)) {
                    System.out.println("O Player está tocando na borda da tela!");
                    som.tocarSom();

                    // Ajusta a posição do Player para manter dentro dos limites da tela
                    Rectangle tela = new Rectangle(0, 0, janela.getContentPane().getWidth(), janela.getContentPane().getHeight());
                    Rectangle playerRect = Player.this.getRect();

                    // Corrige a posição se estiver fora da borda
                    if (playerRect.getX() < tela.getX()) {
                        Player.this.setX(0);
                    } else if (playerRect.getMaxX() > tela.getMaxX()) {
                        Player.this.setX((int) (tela.getMaxX() - playerRect.getWidth()));
                    }

                    if (playerRect.getY() < tela.getY()) {
                        Player.this.setY(0);
                    } else if (playerRect.getMaxY() > tela.getMaxY()) {
                        Player.this.setY((int) (tela.getMaxY() - playerRect.getHeight()));
                    }
                }

                // Verifica se o Player está tocando no ponteiro do mouse
                if (sensores.tocandoPonteiroMouse(Player.this.getRect())) {
                    System.out.println("O Player está tocando no ponteiro do mouse!");
                    som.tocarSom();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        });
    }
}
