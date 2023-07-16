
import java.io.*;
import java.util.*;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class Buscador {

    // seleciona o diretório raiz
    public static File selecionaDiretorioRaiz() {
        JFileChooser janelaSelecao = new JFileChooser(".");
        janelaSelecao.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int acao = janelaSelecao.showOpenDialog(null);

        if (acao == JFileChooser.APPROVE_OPTION) {
            return janelaSelecao.getSelectedFile();
        } else {
            return null;
        }
    }

    public static void main(String[] args) {

        File pastaInicial = selecionaDiretorioRaiz();

        // verifica se o diretório é válido
        if (pastaInicial == null) {
            System.out.println("Você deve selecionar uma pasta para o processamento.");
        } else {
            // cria a hashtable
            LinkedList<Palavra>[] hashtable = new LinkedList[calcularTamanhoHashtable(pastaInicial)];

            percorrerDiretorio(pastaInicial, hashtable);

            exibirPalavras(hashtable);

            String palavraChave = lerPalavraChave();

            exibirResultado(hashtable, palavraChave);

        }
    }

    // calcula o tamanho da hashtable com base no número de arquivos
    public static int calcularTamanhoHashtable(File pasta) {
        int numArquivos = contarArquivos(pasta);
        // retorna um número maior ou igual ao número de arquivos
        return Math.max(numArquivos, 10); // valor mínimo 10 para evitar uma hashtable pequena
    }

    // conta o número de arquivos na pasta
    public static int contarArquivos(File pasta) {
        int count = 0;
        if (pasta.isDirectory()) {
            File[] arquivos = pasta.listFiles();
            if (arquivos != null) {
                for (File arquivo : arquivos) {
                    if (arquivo.isFile()) {
                        count++;
                    } else if (arquivo.isDirectory()) {
                        count += contarArquivos(arquivo);
                    }
                }
            }
        }
        return count;
    }

    // percorre os arquivos e insere os elementos na hashtable
    public static void percorrerDiretorio(File pasta, LinkedList<Palavra>[] hashtable) {
        if (pasta.isDirectory()) {
            // pega a lista de arquivos na pasta
            File[] arquivos = pasta.listFiles();

            if (arquivos != null) {
                for (File arquivo : arquivos) {
                    if (arquivo.isDirectory()) {
                        percorrerDiretorio(arquivo, hashtable);
                    } else {
                        inserirElementos(arquivo, hashtable);
                    }
                }
            }
        }
    }

    public static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "de", "da", "do", "para", "com", "em", "um", "uma", "os", "as", "e", "ou", "que", "no", "às",
            "nos", "o", "a", "das", "dos", "nas", "como", "seu", "sua", "é", "à", "ao", "se", "mas", "mais",
            "na"
    ));

    // lê os arquivos e insere as palavras na hashtable
    public static void inserirElementos(File arquivo, LinkedList<Palavra>[] hashtable) {
        try (Scanner scanner = new Scanner(arquivo)) {
            while (scanner.hasNextLine()) {
                String linha = scanner.nextLine();
                // define a expressão regular para encontrar pontuação e espaços extras
                String regex = "[,.:;!?()]|\\s{2,}";
                // divide a linha em palavras separadas por espaços em branco, removendo a pontuação e convertendo para minúsculas
                String[] palavras = linha.replaceAll(regex, "").toLowerCase().split("\\s+");

                for (String palavra : palavras) {
                    // verifica se a palavra é uma "stop word" e a ignora
                    if (STOP_WORDS.contains(palavra.toLowerCase())) {
                        continue;
                    }

                    // calcula o valor de hash para a palavra
                    int valorHash = funcaoHash(palavra, hashtable.length);

                    if (hashtable[valorHash] == null) {
                        // se estiver vazia, cria uma nova lista encadeada nessa posição
                        hashtable[valorHash] = new LinkedList<>();
                    }

                    Palavra palavraObjeto = buscarPalavra(hashtable[valorHash], palavra);

                    if (palavraObjeto == null) {
                        // se não foi encontrada, cria um novo objeto Palavra com a palavra
                        palavraObjeto = new Palavra(palavra);

                        // adiciona o objeto Palavra à lista encadeada
                        hashtable[valorHash].add(palavraObjeto);
                    }

                    // adiciona a ocorrência do arquivo à palavra encontrada
                    palavraObjeto.adicionarOcorrencia(arquivo.getName());
                }
            }
        } catch (IOException e) {
        }
    }

    // busca uma palavra na lista encadeada de palavras da hashtable
    public static Palavra buscarPalavra(LinkedList<Palavra> lista, String palavraChave) {
        for (Palavra palavra : lista) {
            if (palavra.getPalavraChave().toLowerCase().equals(palavraChave.toLowerCase())) {
                return palavra;
            }
        }
        return null;
    }

    // calcula o valor de hash para uma palavra
    public static int funcaoHash(String palavra, int tamanhoHashtable) {
        return Math.abs(palavra.hashCode()) % tamanhoHashtable;
    }

    // lê a entrada da palavra-chave do usuário
    public static String lerPalavraChave() {
        return JOptionPane.showInputDialog(null, "Digite a palavra-chave que deseja buscar:");
    }

    // função para exibir a lista de todas as palavras encontradas
    public static void exibirPalavras(LinkedList<Palavra>[] hashtable) {
        // reúne todas as palavras únicas encontradas na hashtable
        Set<String> palavrasEncontradas = new HashSet<>();
        for (LinkedList<Palavra> lista : hashtable) {
            if (lista != null) {
                for (Palavra palavra : lista) {
                    palavrasEncontradas.add(palavra.getPalavraChave());
                }
            }
        }

        // constrói a mensagem com todas as palavras encontradas
        StringBuilder mensagem = new StringBuilder("Palavras encontradas:\n\n");
        for (String palavra : palavrasEncontradas) {
            mensagem.append(palavra).append("\n");
        }

        System.out.println(mensagem.toString());
    }

    public static void exibirResultado(LinkedList<Palavra>[] hashtable, String palavraChave) {
        // busca a palavra-chave na hashtable
        Palavra palavra = null;
        int valorHash = funcaoHash(palavraChave, hashtable.length);
        LinkedList<Palavra> lista = hashtable[valorHash];
        if (lista != null) {
            palavra = buscarPalavra(lista, palavraChave);
        }

        // verifica se a palavra-chave foi encontrada
        if (palavra == null) {
            JOptionPane.showMessageDialog(null, "A palavra-chave \"" + palavraChave + "\" não foi encontrada.");
        } else {
            StringBuilder mensagem = new StringBuilder();
            mensagem.append("Palavra \"").append(palavra.getPalavraChave()).append("\":\n\n");

            // ordena o número de ocorrências
            List<Ocorrencia> ocorrencias = palavra.getOcorrencias();
            ocorrencias.sort((o1, o2) -> Integer.compare(o2.getNumeroOcorrencias(), o1.getNumeroOcorrencias()));

            for (Ocorrencia ocorrencia : ocorrencias) {
                mensagem.append("Arquivo: ").append(ocorrencia.getNomeArquivo()).append(" - Ocorrências: ").append(ocorrencia.getNumeroOcorrencias()).append("\n");
            }

            JOptionPane.showMessageDialog(null, mensagem.toString());
        }
    }
}
