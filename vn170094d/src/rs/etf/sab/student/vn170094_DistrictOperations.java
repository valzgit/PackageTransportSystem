package rs.etf.sab.student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import rs.etf.sab.operations.DistrictOperations;

public class vn170094_DistrictOperations implements DistrictOperations {

	public static boolean districtWithNameExists(String name) throws SQLException {
		System.out.println("Usao u districtWithNameExists");
		Connection connection = DB.getInstance().getConnection();
		String querry = "SELECT Naziv FROM Opstina WHERE Naziv = ?";
		if (name == null) {
			System.out.println("Name je null, vraca false");
			return false;
		}
		try (Statement statement = connection.createStatement();
				PreparedStatement ps = connection.prepareStatement(querry);) {
			ps.setString(1, name);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int deleteAllDistrictsFromCity(String nameOfTheCity) {
		System.out.println("Usao u deleteAllDistrictsFromCity");
		if (nameOfTheCity == null) {
			System.out.println("nameOfTheCity je null, vraca 0");
			return 0;
		}
		Connection connection = DB.getInstance().getConnection();
		String numberOfDistricts = "SELECT COUNT(*) FROM Opstina WHERE idG IN (SELECT idG FROM Grad WHERE Naziv = ?)";
		try (PreparedStatement ps = connection.prepareStatement(numberOfDistricts);) {
			ps.setString(1, nameOfTheCity);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				int number = rs.getInt(1);
				try (PreparedStatement st = connection
						.prepareStatement("DELETE FROM Opstina WHERE idG IN (SELECT idG FROM Grad WHERE Naziv = ?)");) {
					st.setString(1, nameOfTheCity);
					st.execute();
				}
				System.out.println("Vratio " + number);
				return number;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Vratio " + 0);
		return 0;
	}

	@Override
	public boolean deleteDistrict(int idDistrict) {
		System.out.println("Usao u deleteDistricts jedan district sa id:" + idDistrict);
		Connection connection = DB.getInstance().getConnection();
		String query = "DELETE FROM Opstina WHERE idO = " + idDistrict;
		try (Statement s = connection.createStatement();) {
			int brojObrisanih = s.executeUpdate(query);
			if (brojObrisanih > 0) {
				System.out.println("Vratio true");
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println("Vratio false");
		return false;
	}

	@Override
	public int deleteDistricts(String... names) {
		System.out.println("Usao u deleteDistricts");
		Connection connection = DB.getInstance().getConnection();
		String query = "DELETE FROM Opstina WHERE Naziv = ?";
		if (names == null || names.length < 1) {
			System.out.println("Names je null ili nema kompoente, vratio 0 ");
			return 0;
		}
		int deletedDistricts = 0;
		for (int i = 0; i < names.length; ++i) {
			try (PreparedStatement ps = connection.prepareStatement(query);) {
				ps.setString(1, names[i]);
				deletedDistricts += ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Broj obrisanih opstina = " + deletedDistricts);
		return deletedDistricts;
	}

	@Override
	public List<Integer> getAllDistricts() {
		System.out.println("Usao u getAllDistricts");
		String querry = "SELECT idO FROM Opstina";
		Connection connection = DB.getInstance().getConnection();
		List<Integer> allDistricts = new LinkedList<Integer>();
		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(querry)) {
			while (resultSet.next()) {
				allDistricts.add(resultSet.getInt("idO"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
//		System.out.println("USAO");
//		for (int i = 0; i < allDistricts.size(); ++i) {
//			System.out.println(allDistricts.get(i));
//		}
		return allDistricts;
	}

	@Override
	public List<Integer> getAllDistrictsFromCity(int idCity) {
		System.out.println("Usao u getAllDistrictsFromCity");
		String query = "SELECT idO FROM Opstina WHERE idG = " + idCity;
		Connection connection = DB.getInstance().getConnection();
		List<Integer> allDistricts = new LinkedList<Integer>();
		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query);) {
			while (resultSet.next()) {
				allDistricts.add(resultSet.getInt(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
//		System.out.println("USAO");
//		for (int i = 0; i < allDistricts.size(); ++i) {
//			System.out.println(allDistricts.get(i));
//		}
		return allDistricts;
	}

	@Override
	public int insertDistrict(String name, int cityId, int xCord, int yCord) {
		System.out.println("Usao u insertDistrict");
		Connection connection = DB.getInstance().getConnection();
		String cityExists = "SELECT idG FROM Grad WHERE idG=" + cityId;
		if(name==null) {
			System.out.println("Name je uneto kao null, returned -1");
			return -1;
		}
		int max = -1;
		try (Statement s = connection.createStatement(); ResultSet rs = s.executeQuery(cityExists);) {
			if (rs.next() && !districtWithNameExists(name)) {
				try (PreparedStatement ps = connection.prepareStatement(
						"INSERT INTO Opstina VALUES(" + xCord + "," + yCord + "," + cityId + ",?)",
						PreparedStatement.RETURN_GENERATED_KEYS);) {
					ps.setString(1, name);
					ps.execute();
					try (ResultSet keys = ps.getGeneratedKeys()) {
						if (keys.next()) {
							max = keys.getInt(1);
						}
					}
					System.out.println("Novi ID je " + max);
					return max;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Doslo je do greske pri dodavanju Opstine");
		return -1;
	}

}
