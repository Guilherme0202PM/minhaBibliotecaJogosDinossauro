import java.awt.*;

public class PlayerIA extends CriaObjeto {
    private Movimento movimento;
    private Sensores sensores;
    private Som som;
    private GameWindow janela;
    private Sprite sprite; // Instância da classe Sprite
    private int metadealtura = altura/2;
    private int alturaOriginal = altura;
    private int ajuste = y-metadealtura;
    private double velocidadeVertical = 0;
    private boolean noAr = true;
    private boolean saltando = false;
    private Color corContorno = Color.BLUE; // Cor padrão do contorno
    private boolean filtroAplicado = false; // Variável de controle





    public PlayerIA(int x, int y, int largura, int altura, String nomeImagem, Movimento movimento, Sensores sensores, Som som, GameWindow janela) {
        super(x, y, largura, altura, nomeImagem);
        this.movimento = movimento;
        this.sensores = sensores;
        this.som = som;
        this.janela = janela;
        this.sprite = new Sprite(nomeImagem); // Inicializa o Sprite com a imagem original
    }

    // Metodo para alterar a cor do contorno
    public void ameacaDetectada() {
        this.corContorno = Color.RED;
    }

    public void ameacaNaoDetectada() {
        this.corContorno = Color.BLUE;
    }

    @Override
    public void desenhar(Graphics g) {
        sprite.animacaoSprite(g, x, y, largura, altura); // Exibe a animaçao

        // Chama o metodo de area de identificacao para desenhar o contorno
        //sensores.CriaAreaIdentificacao(g, PlayerIA.this);

        Rectangle areaIdentificacao = getRect(); // Obter o retângulo do objeto

        int ajuste = 6; // Variável para ajustar o tamanho do retângulo
        int novaLargura = areaIdentificacao.width * ajuste;
        int novaAltura = areaIdentificacao.height * ajuste;
        int ajusteCentralizacao = 25 * (ajuste - 1);

        Rectangle areaExpandida = new Rectangle(
                areaIdentificacao.x - ajusteCentralizacao,
                areaIdentificacao.y - ajusteCentralizacao,
                novaLargura,
                novaAltura
        );

        //g.setColor(corContorno); // Define a cor do contorno
        //g.drawRect(areaExpandida.x, areaExpandida.y, areaExpandida.width, areaExpandida.height);

    }

    private int pontuacao;

    public void incrementarPontuacao(int valor) {
        pontuacao += valor;
    }

    public int getPontuacao() {
        return pontuacao;
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
    public void apertarSaltar() {
        altura = alturaOriginal; // Reduz a altura pela metade (agachado)
        movimento.iniciarSalto(PlayerIA.this); // Inicia o salto do PlayerIA
        //sprite.iniciarAnimacao("dinoIA andandoo_andando_", 5, 100); // Animação de pulo
        agaichado = false;
    }

    // Metodo para simular o pressionamento da tecla "S" (agachar)
    public void apertarAbaixar() {
        movimento.movimentoY(PlayerIA.this, 20); // Ajuste de movimento para abaixar
        altura = metadealtura; // Reduz a altura pela metade (agachado)
        agaichado = true;
    }

    public  void apertarDireita(){
        movimento.movimentoX(PlayerIA.this, 20);

        if (!sensores.tocandoBorda(PlayerIA.this)) return; // Se não estiver tocando, nem entra

        System.out.println("O Player está tocando na borda da tela!");
        som.tocarSom();

        Point novaPosicao = sensores.corrigirPosicao(PlayerIA.this);
        PlayerIA.this.setX(novaPosicao.x);
        PlayerIA.this.setY(novaPosicao.y);

    }

    public  void apertarEsquerda(){
        movimento.movimentoX(PlayerIA.this, -20);

        if (!sensores.tocandoBorda(PlayerIA.this)) return; // Se não estiver tocando, nem entra

        System.out.println("O Player está tocando na borda da tela!");
        som.tocarSom();

        Point novaPosicao = sensores.corrigirPosicao(PlayerIA.this);
        PlayerIA.this.setX(novaPosicao.x);
        PlayerIA.this.setY(novaPosicao.y);
    }

    public void levantar() {
        altura = alturaOriginal; // Restaura a altura original do personagem
    }


    private boolean agaichado;


    public void aplicarFiltro() {
        if (!filtroAplicado) { // Verifica se o filtro já foi aplicado
            sprite.aplicarFiltroColorido(); // Aplica o filtro
            filtroAplicado = true; // Marca o filtro como aplicado
        }
    }


    // Metodo para atualizar a animação do Sprite
    public void atualizarAnimacao(Graphics g) {
        // Verifica se uma animação está em andamento
        if (sprite != null) {
            sprite.animacaoSprite(g, x, y, largura, altura);
        }
    }

}