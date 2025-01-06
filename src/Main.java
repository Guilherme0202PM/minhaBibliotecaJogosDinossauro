import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        GameWindow janela = new GameWindow();
        Som som = new Som("Meow.wav");

        Movimento movimento = new Movimento();
        Sensores sensores = new Sensores(janela);

        Fundo fundo = new Fundo(600, 600, "Fundo1.png");
        janela.setFundo(fundo); // Define o fundo no GamePanel

        int pontuacaoAlvo = 100;
        int pontuacao = 0;

        Player player = new Player(50, 50, 50, 50, "dino andandoo_andando_0.png", movimento, sensores, som, janela);
        janela.adicionarObjeto(player);
        player.adicionarListener();


        PlayerIA player2 = new PlayerIA(200, 50, 50, 50, "dinoIA andandoo_andando_0.png", movimento, sensores, som, janela);
        janela.adicionarObjeto(player2);
        player2.adicionarListener();

        /*
        // Criação de um vetor para armazenar multiplos player2
        int numPlayers = 5; // Número de PlayerIA
        PlayerIA[] player2Array = new PlayerIA[numPlayers];

        for (int i = 0; i < numPlayers; i++) {
            int posX = 200 + i * 60; // Posicione-os com um espaçamento entre si
            player2Array[i] = new PlayerIA(posX, 50, 50, 50, "dinoIA andandoo_andando_0.png", movimento, sensores, som, janela);
            janela.adicionarObjeto(player2Array[i]); // Adiciona o PlayerIA à janela
            player2Array[i].adicionarListener();
        }

         */

        int maxInimigos = pontuacaoAlvo;
        Inimigo[] inimigos = new Inimigo[maxInimigos];
        Random random = new Random();
        for (int i = 0; i < maxInimigos; i++) {
            int tipoInimigo = random.nextInt(2);
            if (tipoInimigo == 0) {
                inimigos[i] = new Inimigo(1000, 350, 50, 50, "dinoInimigo.png", -5, 0, movimento, sensores, janela);
            } else {
                inimigos[i] = new InimigoVoador(1000, 320, 70, 50, "pitero.png", -5, 0, movimento, sensores, janela);
            }
            janela.adicionarObjeto(inimigos[i]);
        }

        // Geração de múltiplos blocos de chão
        int larguraChao = 500; // Largura do chão
        int alturaChao = 50;   // Altura do chão
        int numeroDeChao = 3;  // Número de blocos de chão
        Chao[] chaoBlocos = new Chao[numeroDeChao];

        for (int i = 0; i < numeroDeChao; i++) {
            chaoBlocos[i] = new Chao(i * larguraChao, 400, larguraChao, alturaChao); // Posição inicial dos blocos
            janela.adicionarObjeto(chaoBlocos[i]);
        }

        JLabel pontuacaoLabel = new JLabel("Pontuacao: 0");
        pontuacaoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        pontuacaoLabel.setForeground(Color.BLACK);
        pontuacaoLabel.setBounds(30, 30, 200, 30);
        janela.addComponentToGamePanel(pontuacaoLabel);

        long startTime = System.currentTimeMillis();

        while (true) {
            for (int i = 0; i < maxInimigos; i++) {
                Inimigo inimigo = inimigos[i];
                if (inimigo != null) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - startTime >= i * 2000) {
                        inimigo.atualizar();
                    }

                    if (sensores.verificarColisao(player, inimigo)) {
                        janela.removerObjeto(inimigo);
                        inimigos[i] = null;
                        System.out.println("Colisão detectada! Inimigo removido.");
                    } else if (inimigo.getRect().x < player.getRect().x) {
                        pontuacao++;
                        System.out.println("Pontuação: " + pontuacao);
                        pontuacaoLabel.setText("Pontuacao: " + pontuacao);

                        if (inimigo.getRect().x < -inimigo.getRect().width) {
                            janela.removerObjeto(inimigo);
                            inimigos[i] = null;
                        }
                    }

                    if (sensores.verificarColisao(player2, inimigo)) {
                        janela.removerObjeto(player2);
                        System.out.println("Colisão detectada! Inimigo removido.");
                    }



                }
            }

            // Atualiza a posição do chão
            for (Chao chao : chaoBlocos) {
                chao.setX(chao.getX() - 5); // Move o chão para a esquerda
                if (chao.getX() < -larguraChao) {
                    chao.setX(larguraChao * (numeroDeChao - 1)); // Reposiciona à direita
                }
            }

            // Atualiza a posição do player e aplica gravidade

            movimento.aplicarGravidade(player, chaoBlocos[0]); // Use o primeiro bloco como referência para gravidade
            movimento.controlarSalto(player);

            movimento.aplicarGravidade(player2, chaoBlocos[0]); // Use o primeiro bloco como referência para gravidade
            movimento.controlarSalto(player2);

            pontuacaoLabel.setText("Pontuacao: " + pontuacao);
            janela.repaint();
            try {
                Thread.sleep(16); // Aproximadamente 60 FPS
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}