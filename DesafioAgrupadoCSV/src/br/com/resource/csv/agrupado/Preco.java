package br.com.resource.csv.agrupado;
import java.math.BigDecimal;

public class Preco {

	private Integer id_preco;
	private Integer nu_prazo_dias_corridos;
	private BigDecimal vl_preco;
	
	
	
	
	public Integer getId_preco() {
		return id_preco;
	}
	public void setId_preco(Integer id_preco) {
		this.id_preco = id_preco;
	}
	
	public Integer getNu_prazo_dias_corridos() {
		return nu_prazo_dias_corridos;
	}
	public void setNu_prazo_dias_corridos(Integer nu_prazo_dias_corridos) {
		this.nu_prazo_dias_corridos = nu_prazo_dias_corridos;
	}
	public BigDecimal getVl_preco() {
		return vl_preco;
	}
	public void setVl_preco(BigDecimal vl_preco) {
		this.vl_preco = vl_preco;
	}
	
	
}
