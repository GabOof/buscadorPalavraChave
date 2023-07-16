
class Ocorrencia {

    private String nomeArquivo;
    private int numeroOcorrencias;

    public Ocorrencia(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
        this.numeroOcorrencias = 1;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public int getNumeroOcorrencias() {
        return numeroOcorrencias;
    }

    public void incrementarNumeroOcorrencias() {
        numeroOcorrencias++;
    }
}
