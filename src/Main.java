import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        GameWindow janela = new GameWindow();
        Som som = new Som("Meow.wav");

        Movimento movimento = new Movimento();
        Sensores sensores = new Sensores(janela);

        // Define o tamanho máximo do vetor e pontuação alvo
        int pontuacaoAlvo = 10;
        int pontuacao = 0; // Inicializa a pontuação

        // Cria uma instância de Player
        Player player = new Player(50, 50, 50, 50, "Personagem1.png", movimento, sensores, som, janela);

        // Adiciona o Player à janela
        janela.adicionarObjeto(player);

        // Configura o listener para o Player
        player.adicionarListener();

        // Define o tamanho máximo do vetor
        int maxInimigos = pontuacaoAlvo;
        Inimigo[] inimigos = new Inimigo[maxInimigos];
        Random random = new Random();
        for (int i = 0; i < maxInimigos; i++) {
            int tipoInimigo = random.nextInt(2); // 0 ou 1
            if (tipoInimigo == 0) {
                inimigos[i] = new Inimigo(1000, 380, 20, 20, "Monstro.png", -5, 0, movimento, sensores, janela);
            } else {
                inimigos[i] = new InimigoVoador(1000, 300, 20, 20, "teste2.png", -5, 0, movimento, sensores, janela);
            }
            janela.adicionarObjeto(inimigos[i]);
        }

        // Cria um chão (Plataforma do tipo Chao)
        Chao chao = new Chao(0, 400, 600, 50); // Chão que cobre a largura da janela

        // Adiciona o chão à janela
        janela.adicionarObjeto(chao);

        // Cria um JLabel para exibir a pontuação
        JLabel pontuacaoLabel = new JLabel("Pontuacao: 0");
        pontuacaoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        pontuacaoLabel.setForeground(Color.BLACK);
        pontuacaoLabel.setBounds(30, 30, 200, 30);
        janela.addComponentToGamePanel(pontuacaoLabel);

        long startTime = System.currentTimeMillis();

        // Loop infinito para manter a janela aberta
        while (true) {
            // Atualiza todos os inimigos com intervalo
            for (int i = 0; i < maxInimigos; i++) {
                Inimigo inimigo = inimigos[i];
                if (inimigo != null) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - startTime >= i * 2000) { // 2000 ms = 2 segundos por inimigo
                        inimigo.atualizar();
                    }
                }
            }

            // Atualiza a posição do player e aplica gravidade
            movimento.aplicarGravidade(player, chao);
            movimento.controlarSalto(player);

            // Atualiza a pontuação
            pontuacaoLabel.setText("Pontuacao: " + pontuacao);

            janela.repaint();
            try {
                Thread.sleep(16); // 60 FPS
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
