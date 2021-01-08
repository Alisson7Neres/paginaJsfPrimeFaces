package dao;

import model.UsuarioPessoa;

public class DaoUsuario<E> extends DaoGeneric<UsuarioPessoa>{

	public void removerUsuario(UsuarioPessoa pessoa) {
		getEntityManager().getTransaction().begin();
	
		getEntityManager().remove(pessoa);
		
		getEntityManager().getTransaction().commit();
		
	}
}
