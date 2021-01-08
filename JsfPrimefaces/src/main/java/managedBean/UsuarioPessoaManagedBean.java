package managedBean;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

import com.google.gson.Gson;

import dao.DaoEmail;
import dao.DaoTelefones;
import dao.DaoUsuario;
import model.EmailUser;
import model.TelefoneUser;
import model.UsuarioPessoa;

@ManagedBean(name = "usuarioPessoaManagedBean")
@ViewScoped
public class UsuarioPessoaManagedBean {

	private UsuarioPessoa usuarioPessoa = new UsuarioPessoa();
	private List<UsuarioPessoa> list = new ArrayList<UsuarioPessoa>();
	private EmailUser emailUser = new EmailUser();
	private List<EmailUser> emailList = new ArrayList<EmailUser>();
	private DaoUsuario<UsuarioPessoa> daoGeneric = new DaoUsuario<UsuarioPessoa>();
	private DaoTelefones<TelefoneUser> daoTelefone = new DaoTelefones<TelefoneUser>();
	private BarChartModel barChartModel = new BarChartModel();
	private DaoEmail<EmailUser> daoEmail = new DaoEmail<EmailUser>();
	
	@PostConstruct
	public void init() {
		list = daoGeneric.listar(UsuarioPessoa.class);
		
		ChartSeries userSalario = new ChartSeries(); // Grupo de funcionários
		userSalario.setLabel("Users");
		for (UsuarioPessoa usuarioPessoa : list) { // add salário para o grupo
			userSalario.set(usuarioPessoa.getNome(), usuarioPessoa.getSalario()); // add salários
		}
		barChartModel.addSeries(userSalario); // Adiciona o grupo
		barChartModel.setTitle("Salários dos Usuários");
	}
	
	public BarChartModel getBarChartModel() {
		return barChartModel;
	}
	
	public void pesquisarCep(AjaxBehaviorEvent event) {
		try {
		URL url = new URL("https://viacep.com.br/ws/"+ usuarioPessoa.getCep() +"/json/");
		URLConnection connection = url.openConnection();
		InputStream is = connection.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		
		String cep = "";
		StringBuilder jsonCep = new StringBuilder();
		
		while((cep = br.readLine()) != null) {
			jsonCep.append(cep);
		}
		
		UsuarioPessoa userCepPessoa = new Gson().fromJson(jsonCep.toString(), UsuarioPessoa.class);
		
		usuarioPessoa.setCep(userCepPessoa.getCep());
		usuarioPessoa.setLogradouro(userCepPessoa.getLogradouro());
		usuarioPessoa.setBairro(userCepPessoa.getBairro());
		usuarioPessoa.setLocalidade(userCepPessoa.getLocalidade());
		usuarioPessoa.setUf(userCepPessoa.getUf());
		
		System.out.println(jsonCep);
	}catch(Exception e) {
		e.printStackTrace();
		}
	}
	
	public String salvar() {
		daoGeneric.salvar(usuarioPessoa);
		list.add(usuarioPessoa);
		FacesContext.getCurrentInstance().addMessage
		(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"","Informação salva"));
		return "";
		
	}
	
	public List<UsuarioPessoa> getList() {
		list = daoGeneric.listar(UsuarioPessoa.class);
		return list;
	}
	
	public List<EmailUser> getEmailList() {
		//list = daoGeneric.listar(EmailUser.class);
		return emailList;
	}
	
	
	public String novo() {
		usuarioPessoa = new UsuarioPessoa();
		FacesContext.getCurrentInstance().addMessage
		(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"","Pronto para inserção"));
		return "";
	}
	
	public String remover() {
		if(usuarioPessoa.getId() == 4) {
			FacesContext.getCurrentInstance().addMessage
			(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"","O ADMINISTRADOR NÃO PODE SER DELETADO "
					+ "\n CONTACTE O ADMINISTRADOR DE DADOS!"));
			return "";
		}
		daoGeneric.removerUsuario(usuarioPessoa);
		daoGeneric.deletarPorId(usuarioPessoa);
		list.remove(usuarioPessoa);
		usuarioPessoa = new UsuarioPessoa();
		FacesContext.getCurrentInstance().addMessage
		(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"","Removido"));
		return "";
	}
	
	public UsuarioPessoa getUsuarioPessoa() {
		return usuarioPessoa;
	}
	public void setUsuarioPessoa(UsuarioPessoa usuarioPessoa) {
		this.usuarioPessoa = usuarioPessoa;
	}
	
	public DaoTelefones<TelefoneUser> getDaoTelefone() {
		return daoTelefone;
	}
	
	public void setDaoTelefone(DaoTelefones<TelefoneUser> daoTelefone) {
		this.daoTelefone = daoTelefone;
	}
	
	public EmailUser getEmailUser() {
		return emailUser;
	}
	
	public void setEmailUser(EmailUser emailUser) {
		this.emailUser = emailUser;
	}
	
	public void addEmail() {
		System.out.println(emailUser);
		emailUser.setUsuarioPessoa(usuarioPessoa);
		emailUser = daoEmail.updateMerge(emailUser);
		usuarioPessoa.getEmails().add(emailUser);
		emailUser = new EmailUser();
		
		FacesContext.getCurrentInstance().addMessage
		(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Email", " adicionado!"));
	}
	
	public void removerEmails() {
		String codigoEmail = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.get("codigoEmail");
		EmailUser remover = new EmailUser();
		remover.setId(Long.parseLong(codigoEmail));
		daoEmail.deletarPorId(remover);
		usuarioPessoa.getEmails().remove(remover);
		
		FacesContext.getCurrentInstance().addMessage
		(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Email", " removido!"));
	}
	
	
}
