package rs.etf.sab.student;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import rs.etf.sab.operations.GeneralOperations;

public class vn170094_GeneralOperations implements GeneralOperations {

	@Override
	public void eraseAll() {
		System.out.println("\n DELETE ALL \n");
		Connection connection = DB.getInstance().getConnection();
		String emptyDBQuerry = 
				"DELETE FROM ZahtevPostaneKurir;\n"+
				"DELETE FROM Ponuda;\n"+
				"DELETE FROM Paket;\n"+
				"DELETE FROM Vozi;\n"+
				"DELETE FROM ZahtevZaPrevozom;\n"+
				"DELETE FROM Kurir;\n"+
				"DELETE FROM Vozilo;\n"+
				"DELETE FROM Opstina;\n"+
				"DELETE FROM Grad;\n"+
				"DELETE FROM Administrator;\n"+
				"DELETE FROM Korisnik;\n";
		try(Statement statement = connection.createStatement();){
			statement.execute(emptyDBQuerry);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

}
