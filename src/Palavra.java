
import java.util.LinkedList;
import java.util.List;

public class Palavra {

    private String palavraChave;
    private List<Ocorrencia> ocorrencias;

    public Palavra(String palavraChave) {
        this.palavraChave = palavraChave;
        this.ocorrencias = new LinkedList<>();
    }

    public String getPalavraChave() {
        return palavraChave;
    }

    public List<Ocorrencia> getOcorrencias() {
        return ocorrencias;
    }

    public void adicionarOcorrencia(String nomeArquivo) {
        // percorre todas as ocorrências da palavra
        for (Ocorrencia ocorrencia : ocorrencias) {
            // verifica se a ocorrência está associada ao mesmo nome de arquivo
            if (ocorrencia.getNomeArquivo().equals(nomeArquivo)) {
                ocorrencia.incrementarNumeroOcorrencias(); 
                return; 
            }
        }

        // se não encontrou nenhuma ocorrência associada ao nome de arquivo, cria uma nova
        Ocorrencia novaOcorrencia = new Ocorrencia(nomeArquivo);
        ocorrencias.add(novaOcorrencia); // adiciona à lista de ocorrências
    }
}
