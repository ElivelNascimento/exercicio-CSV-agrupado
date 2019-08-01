package br.com.resource.csv.agrupado;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LerCSV {
	public static void main(String[] args) throws Exception {

		String pathArquivo = "C:\\Users\\resource\\eclipse-workspace\\DesafioAgrupadoCSV\\Operacoes.csv";
		String pathArquivo2 = "C:\\Users\\resource\\eclipse-workspace\\DesafioAgrupadoCSV\\DadosMercado.csv";
		lerArquivoCSV(pathArquivo, pathArquivo2);
	}

	private static void lerArquivoCSV(String pathArquivo, String pathArquivo2) throws Exception {

		BufferedReader conteudoCSV = null;
		String linha = "";
		String csvSeparadorCampos = ";";
		List<Saida> saidas = new ArrayList<Saida>();

		try 
		{
			conteudoCSV = new BufferedReader(new FileReader(pathArquivo));
			List<Operacao> listaoperacao = new ArrayList<Operacao>();
			System.out.println("Iniciando leitura das operacoes!");
			while ((linha = conteudoCSV.readLine()) != null) {
				
				String[] arquivo = linha.split(csvSeparadorCampos);

				if (!arquivo[0].equals("CD_OPERACAO")) {
					Operacao item = new Operacao();

					  item.setCd_operacao(Double.parseDouble(arquivo[0]));
					  DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
					  LocalDate dateIni = LocalDate.parse(arquivo[1], dateformatter);					  
					  item.setDt_inicio(dateIni);
					  LocalDate dateFim = LocalDate.parse(arquivo[2], dateformatter);
					  item.setDt_fim(dateFim);
					  item.setNm_empresa(arquivo[3]);
					  item.setNm_mesa(arquivo[4]);
					  item.setNm_estrategia(arquivo[5]);
					  item.setNm_centralizador(arquivo[6]);
					  item.setNm_gestor(arquivo[7]); 
					  item.setNm_subgestor(arquivo[8]);
					  item.setNm_subproduto(arquivo[9]);
					  item.setNm_caracteristica(arquivo[10]);
					  item.setCd_ativo(arquivo[11]);
					  BigDecimal valor = new BigDecimal(arquivo[12].replace(',', '.'));
					  item.setQuantidade(valor);
					  item.setId_preco(Integer.parseInt(arquivo[13]));
					  listaoperacao.add(item);
  				} 
			}
			

			System.out.println("Iniciando leitura dos precos!");
			List<Preco> listapreco = new ArrayList<Preco>();
			conteudoCSV = new BufferedReader(new FileReader(pathArquivo2));
			while ((linha = conteudoCSV.readLine()) != null) 
			{ 

				String[] arquivo2 =	linha.split(csvSeparadorCampos); 
				if (!arquivo2[0].equals("ID_PRECO")) 
				{				
					  Preco preco = new Preco(); 
					  preco.setId_preco(Integer.parseInt(arquivo2[0]));
					  preco.setNu_prazo_dias_corridos(Integer.parseInt(arquivo2[1]));
					  BigDecimal valor = new BigDecimal(arquivo2[2].replace(',', '.'));
					  preco.setVl_preco(valor);
					  listapreco.add(preco);
					  
				} 
			 }
			
			for (Operacao item : listaoperacao) {

				Long dias = item.getDt_inicio().until(item.getDt_fim(), ChronoUnit.DAYS);
				

				Preco precoEncontrado = listapreco.stream()
					.filter(preco -> preco.getId_preco().equals(item.getId_preco()))
					.filter(preco -> preco.getNu_prazo_dias_corridos() == Integer.parseInt(dias.toString()))
					.findAny().orElse(null);
				
				  if(precoEncontrado == null) 
				  {
					  Saida saida = new Saida(); 
					  saida.setNm_subproduto(item.getNm_subproduto());
					  BigDecimal valor = new BigDecimal(0);
					  saida.setResultado(valor); 
					  saidas.add(saida);
					  precoEncontrado = null;
				  }
				  else
				  {
					  Saida saida = new Saida(); 
					  saida.setNm_subproduto(item.getNm_subproduto());
					  saida.setResultado(item.getQuantidade().multiply(precoEncontrado.getVl_preco())); 
					  saidas.add(saida);
					  precoEncontrado = null;
				  }		
			}
			System.out.println("Iniciando os agrupamentos!");
			List<Saida> saidasAgrupadas = saidas.stream()
	                .collect(Collectors.groupingBy(Saida::getNm_subproduto))
	                .entrySet()
	                .stream()
	                .map(e -> {
	                    Saida saidaAgrupada = new Saida();
	                    BigDecimal sum = e.getValue().stream()
	                            .map(Saida::getResultado)
	                            .reduce(BigDecimal.ZERO, BigDecimal::add);
	                    //System.out.println(e.getKey() + " - " + sum);
	                    saidaAgrupada.setNm_subproduto(e.getKey());
	                    if(sum.signum() == 0)
	                    {
	                    	BigDecimal valor = new BigDecimal(0);
	                    	saidaAgrupada.setResultado(valor);
	                    }
	                    else
	                    	saidaAgrupada.setResultado(sum);
	                    return saidaAgrupada;
	                }).collect(Collectors.toList());
			
		

			criarArquivoCSV("C:\\Users\\resource\\eclipse-workspace\\DesafioAgrupadoCSV\\Resultado_" + System.currentTimeMillis() + ".csv", saidasAgrupadas);

		} catch (FileNotFoundException e) {
			System.out.println("Arquivo não encontrado:  \n" + e.getMessage());
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("IndexOutOfBounds: \n" + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO Erro: \n " + e.getMessage());
		} finally {
			if (conteudoCSV != null) {
				try {
					conteudoCSV.close();
				} catch (Exception e) {
					System.out.println("IO Erro: \n" + e.getMessage());
				}
			}
		}
	}
	


	private static void criarArquivoCSV(String filePath, List<Saida> saidas) {
		
		System.out.println("Gerando arquivo CSV!");
		FileWriter fileWriter = null;
		try {
			 fileWriter = new FileWriter(filePath);
			
			fileWriter.append("nm_suproduto, Resultado \n");
			
			for(int i = 0; i < saidas.size();i++) {
				fileWriter.append(saidas.get(i).getNm_subproduto());
				fileWriter.append(",");
				fileWriter.append(saidas.get(i).getResultado().toString());				
				fileWriter.append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
				System.out.println("Arquivo gerado com sucesso!");
			} catch (Exception e2) {
				System.out.println("Erro ao gerar arquivo");
				e2.printStackTrace();
			}
		}
	}
}
