import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class PlayerIA extends CriaObjeto {
    private Movimento movimento;
    private Sensores sensores;
    private Som som;
    private GameWindow janela;
    private Sprite sprite; // Instância da classe Sprite
    private int metadealtura = altura/2;
    private int ajuste = y-metadealtura;
    private double velocidadeVertical = 0;
    private boolean noAr = true;
    private boolean saltando = false;


    public PlayerIA(int x, int y, int largura, int altura, String nomeImagem, Movimento movimento, Sensores sensores, Som som, GameWindow janela) {
        super(x, y, largura, altura, nomeImagem);
        this.movimento = movimento;
        this.sensores = sensores;
        this.som = som;
        this.janela = janela;
        this.sprite = new Sprite(nomeImagem); // Inicializa o Sprite com a imagem original
    }


    @Override
    public void desenhar(Graphics g) {
        sprite.animacaoSprite(g, x, y, largura, altura); // Exibe a animaçao

        // Chama o metodo de area de identificacao para desenhar o contorno
        //sensores.CriaAreaIdentificacao(g, PlayerIA.this);

        Rectangle areaIdentificacao = getRect(); // Obter o retângulo do objeto

        int ajuste = 4; // Variável para ajustar o tamanho do retângulo
        int novaLargura = areaIdentificacao.width * ajuste;
        int novaAltura = areaIdentificacao.height * ajuste;
        int ajusteCentralizacao = 25 * (ajuste - 1);

        Rectangle areaExpandida = new Rectangle(
                areaIdentificacao.x - ajusteCentralizacao,
                areaIdentificacao.y - ajusteCentralizacao,
                novaLargura,
                novaAltura
        );

        g.setColor(Color.BLUE); // Define a cor do contorno
        g.drawRect(areaExpandida.x, areaExpandida.y, areaExpandida.width, areaExpandida.height);

    }

    public void adicionarListener() {
        janela.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                int velocidade = 20; // Velocidade do movimento

                if (e.getKeyCode() == KeyEvent.VK_A) {
                    movimento.movimentoX(PlayerIA.this, -velocidade);
                    sprite.iniciarAnimacao("dinoIA andandoo_andando_", 3, 100);
                } else if (e.getKeyCode() == KeyEvent.VK_D) {
                    movimento.movimentoX(PlayerIA.this, velocidade);
                    sprite.iniciarAnimacao("dinoIA andandoo_andando_", 3, 100);
                } else if (e.getKeyCode() == KeyEvent.VK_W) {
                    movimento.movimentoY(PlayerIA.this, -velocidade);
                } else if (e.getKeyCode() == KeyEvent.VK_S) {
                    movimento.movimentoY(PlayerIA.this, velocidade);
                    //sprite.iniciarAnimacao("dino rebaixadoo_rebaixado_", 2, 100);
                    //sprite.mudaSprite("dino rebaixadoo_rebaixado_0.png"); // Muda para a imagem abaixada

                    altura = metadealtura; // Reduz a altura pela metade
                    movimento.movimentoY(PlayerIA.this, ajuste);
                }

                if (e.getKeyCode() == KeyEvent.VK_Z) { // Quando clica em Z, vai para posição aleatória
                    movimento.goPosicaoAleatoria(PlayerIA.this, janela);
                }

                if (e.getKeyCode() == KeyEvent.VK_J) {
                    som.tocarSom(); // Reutiliza a instância existente
                }

                if (e.getKeyCode() == KeyEvent.VK_X) {
                    movimento.goPosicao(PlayerIA.this, 50, 50);
                }

                // Adiciona salto com a tecla de espaço
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    movimento.iniciarSalto(PlayerIA.this); // Especificamente para o PlayerIA
                    // Exemplo de animação de salto
                    sprite.iniciarAnimacao("dinoIA andandoo_andando_", 5, 100);
                    //sprite.mudaSprite("dino pulandoo_pulando_3.png"); // Muda para a imagem abaixada
                }

                if (e.getKeyCode() == KeyEvent.VK_F) {
                    sprite.aplicarFiltroPretoBranco();
                    //sprite.aplicarFiltroColorido();
                }

                // Verifica se o PlayerIA está tocando na borda
                if (sensores.tocandoBorda(PlayerIA.this)) {
                    System.out.println("O PlayerIA está tocando na borda da tela!");
                    som.tocarSom();

                    // Ajusta a posição do PlayerIA para manter dentro dos limites da tela
                    Rectangle tela = new Rectangle(0, 0, janela.getContentPane().getWidth(), janela.getContentPane().getHeight());
                    Rectangle PlayerIARect = PlayerIA.this.getRect();

                    // Corrige a posição se estiver fora da borda
                    if (PlayerIARect.getX() < tela.getX()) {
                        PlayerIA.this.setX(0);
                    } else if (PlayerIARect.getMaxX() > tela.getMaxX()) {
                        PlayerIA.this.setX((int) (tela.getMaxX() - PlayerIARect.getWidth()));
                    }

                    if (PlayerIARect.getY() < tela.getY()) {
                        PlayerIA.this.setY(0);
                    } else if (PlayerIARect.getMaxY() > tela.getMaxY()) {
                        PlayerIA.this.setY((int) (tela.getMaxY() - PlayerIARect.getHeight()));
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_D) {
                    //sprite.resetSprite(); // Retorna ao sprite original ao soltar a tecla de movimento
                }
                if (e.getKeyCode() == KeyEvent.VK_S) {
                    sprite.resetSprite(); // Retorna à imagem original ao soltar a tecla S
                    altura *= 2;
                }

                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    //sprite.resetSprite(); // Retorna ao sprite original ao soltar a tecla de movimento
                    sprite.iniciarAnimacao("dinoIA andandoo_andando_", 3, 100);
                }
            }
        });
    }

    // Getters e setters
    public double getVelocidadeVertical() {
        return velocidadeVertical;
    }

    public void setVelocidadeVertical(double velocidadeVertical) {
        this.velocidadeVertical = velocidadeVertical;
    }

    public boolean isNoAr() {
        return noAr;
    }

    public void setNoAr(boolean noAr) {
        this.noAr = noAr;
    }

    public boolean isSaltando() {
        return saltando;
    }

    public void setSaltando(boolean saltando) {
        this.saltando = saltando;
    }

    // Metodo para simular o pressionamento da tecla "Espaço" (pulo)
    public void apertarEspaco() {
        movimento.iniciarSalto(PlayerIA.this); // Inicia o salto do PlayerIA
        sprite.iniciarAnimacao("dinoIA andandoo_andando_", 5, 100); // Animação de pulo
    }

    // Metodo para simular o pressionamento da tecla "S" (agachar)
    public void apertarS() {
        movimento.movimentoY(PlayerIA.this, 20); // Ajuste de movimento para abaixar
        altura = metadealtura; // Reduz a altura pela metade (agachado)
        // sprite.iniciarAnimacao("dino rebaixadoo_rebaixado_", 2, 100); // (Caso tenha animação de agachar)
    }

    // Metodo para atualizar a animação do Sprite
    public void atualizarAnimacao(Graphics g) {
        // Verifica se uma animação está em andamento
        if (sprite != null) {
            sprite.animacaoSprite(g, x, y, largura, altura);
        }
    }
}