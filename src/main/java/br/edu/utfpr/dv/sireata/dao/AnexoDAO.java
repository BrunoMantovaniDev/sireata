package br.edu.utfpr.dv.sireata.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.sireata.model.Anexo;

public class AnexoDAO {
	
	public Anexo buscarPorId(int id) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT anexos.* FROM anexos " +
				"WHERE idAnexo = ?");
		
			stmt.setInt(1, id);
			
			rs = stmt.executeQuery();
			
			if(rs.next()){
				return this.carregarObjeto(rs);
			}else{
				return null;
			}
		}finally{
                    
                    // criar close conection para reduzir os IFs
			ConnectionDAO.closeConnection(conn, stmt, rs);
		}
	}
	
	public List<Anexo> listarPorAta(int idAta) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("SELECT anexos.* FROM anexos " +
				"WHERE idAta=" + String.valueOf(idAta) + " ORDER BY anexos.ordem");
		
			rs = stmt.executeQuery();
		
			List<Anexo> list = new ArrayList<Anexo>();
			
			while(rs.next()){
				list.add(this.carregarObjeto(rs));
			}
			
			return list;
		}finally{
			ConnectionDAO.closeConnection(conn, stmt, rs);
		}
	}
	
	public int salvar(Anexo anexo) throws SQLException{
		boolean insert = (anexo.getIdAnexo() == 0);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
		
			if(insert){
				stmt = conn.prepareStatement("INSERT INTO anexos(idAta, ordem, descricao, arquivo) VALUES(?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				stmt = conn.prepareStatement("UPDATE anexos SET idAta=?, ordem=?, descricao=?, arquivo=? WHERE idAnexo=?");
			}
			
			stmt.setInt(1, anexo.getAta().getIdAta());
			stmt.setInt(2, anexo.getOrdem());
			stmt.setString(3, anexo.getDescricao());
			stmt.setBytes(4, anexo.getArquivo());
			
			if(!insert){
				stmt.setInt(5, anexo.getIdAnexo());
			}
			
			stmt.execute();
			
			if(insert){
				rs = stmt.getGeneratedKeys();
				
				if(rs.next()){
					anexo.setIdAnexo(rs.getInt(1));
				}
			}
			
			return anexo.getIdAnexo();
		}finally{
			ConnectionDAO.closeConnection(conn, stmt, rs);
		}
	}
	
	public void excluir(int id) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try{
			conn = ConnectionDAO.getInstance().getConnection();
			stmt = conn.prepareStatement("DELETE FROM anexos WHERE idanexo=" + String.valueOf(id));
		
			stmt.execute();
		}finally{
			ConnectionDAO.closeConnection(conn, stmt);
		}
	}
	
	private Anexo carregarObjeto(ResultSet rs) throws SQLException{
		Anexo anexo = new Anexo();
		
		anexo.setIdAnexo(rs.getInt("idAnexo"));
		anexo.getAta().setIdAta(rs.getInt("idAta"));
		anexo.setDescricao(rs.getString("descricao"));
		anexo.setOrdem(rs.getInt("ordem"));
		anexo.setArquivo(rs.getBytes("arquivo"));
		
		return anexo;
	}

}
