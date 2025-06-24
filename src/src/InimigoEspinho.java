public class InimigoEspinho extends Inimigo {
    private Sprite sprite; // Instância da classe Sprite

    private boolean modoAtaque; // variável para controlar o modo de ataque
    private String imagemModoAtaque; // Caminho para a imagem do modo de ataque
    private String imagemModoNeutro; // Caminho para a imagem do modo neutro

    public InimigoEspinho(int x, int y, int largura, int altura, String nomeImagem, int velocidadeX, int velocidadeY, Movimento movimento, Sensores sensores, GameWindow janela) {
        super(x, y, largura, altura, nomeImagem, velocidadeX, velocidadeY, movimento, sensores, janela);
        this.modoAtaque = false; // Inicializa o modoAtaque como falso (neutro)
        this.imagemModoNeutro = nomeImagem; // Guarda a imagem neutra inicial
        this.sprite = new Sprite(nomeImagem); // Inicializa o Sprite com a imagem original
    }

    // Metodo para mudar a imagem para o modo de ataque
    public void imagemModoAtaque(String caminhoImagem) {
        if (sprite != null) { // Evita NullPointerException
            sprite.mudaSprite(caminhoImagem); // Muda a imagem para a de ataque
        }
    }

    // Metodo para verificar o estado do espaço apertado e alterar o modo de ataque
    public void verificarEspacoApertado() {
        this.modoAtaque = true; // Ativa o modo de ataque
        imagemModoAtaque("pterodáctilo_0.png"); // Muda a imagem para o modo de ataque
        //sprite.iniciarAnimacao("dino andandoo_andando_", 1, 100);

    }
}
