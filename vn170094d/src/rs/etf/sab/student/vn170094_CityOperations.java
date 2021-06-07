package rs.etf.sab.student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import rs.etf.sab.operations.CityOperations;

public class vn170094_CityOperations implements CityOperations {

	public static boolean cityWithNameExists(String name) throws SQLException {
		System.out.println("Usao u cityWithNameExists");
		if (name == null) {
			System.out.println("Name = null,returned false");
			return false;
		}
		Connection connection = DB.getInstance().getConnection();
		String querry = "SELECT Naziv FROM Grad WHERE Naziv = ?";
		try (Statement statement = connection.createStatement();
				PreparedStatement ps = connection.prepareStatement(querry);) {
			ps.setString(1, name);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					System.out.println("Pronasao grad sa datim imenom");
					return true;
				}
			}
		}

		System.out.println("Nije pronasao grad sa datim imenom");
		return false;
	}

	@Override
	public int deleteCity(String... names) {
		System.out.println("Usao u deleteCity multiple names");
		Connection connection = DB.getInstance().getConnection();
		String querry = "DELETE FROM Grad WHERE Naziv = ?";
		if (names == null || names.length < 1) {
			System.out.println("Nisu prosledjeni nazivi, vratio 0");
			return 0;
		}
		int deletedCities = 0;
		for (int i = 0; i < names.length; ++i) {
			try (PreparedStatement ps = connection.prepareStatement(querry);) {
				ps.setString(1, names[i]);
				deletedCities += ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return deletedCities;
	}

	@Override
	public boolean deleteCity(int idCity) {
		System.out.println("Usao u deleteCity idCity");
		Connection connection = DB.getInstance().getConnection();
		String query = "DELETE FROM Grad WHERE idG = " + idCity;
		try (Statement s = connection.createStatement();) {
			int deletedRows = s.executeUpdate(query);
			if (deletedRows > 0) {
				System.out.println("Uspesno obrisao grad");
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Neuspesno obrisao grad");
		return false;
	}

	@Override
	public List<Integer> getAllCities() {
		System.out.println("Usao u getAllCities");
		String query = "SELECT idG FROM Grad";
		Connection connection = DB.getInstance().getConnection();
		List<Integer> allCities = new LinkedList<Integer>();
		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
			while (resultSet.next()) {
				allCities.add(resultSet.getInt(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
//		System.out.println("USAO");
//		for (int i = 0; i < allCities.size(); ++i) {
//			System.out.println(allCities.get(i));
//		}
		return allCities;
	}

	@Override
	public int insertCity(String name, String postalCode) {
		if(name==null || postalCode==null) {
			System.out.println("Name ili postalCode su null, return -1");
			return -1;
		}
		System.out.println("Usao u insertCity " + name + " " + postalCode);
		String alreadyExists = "SELECT idG FROM Grad WHERE PostanskiBroj = ? OR Naziv = ?";
		String insert = "INSERT INTO Grad VALUES(?,?)";
		Connection connection = DB.getInstance().getConnection();
		int max = -1;
		try (PreparedStatement ps = connection.prepareStatement(alreadyExists)) {
			ps.setString(1, postalCode);
			ps.setString(2, name);
			try (ResultSet rs = ps.executeQuery();) {
				if (rs.next()) {
					System.out.println("Grad sa nazivom ili istim postanskim kodom vec postoji");
					return -1;
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			return -1;
		}
		try (PreparedStatement ps = connection.prepareStatement(insert, PreparedStatement.RETURN_GENERATED_KEYS);) {
			ps.setString(1, name);
			ps.setString(2, postalCode);
			ps.execute();
			try (ResultSet kljuc = ps.getGeneratedKeys();) {
				if (kljuc.next()) {
					max = kljuc.getInt(1);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Ubacio grad kome je id = " + max);
		return max;
	}

}
