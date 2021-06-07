package rs.etf.sab.student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import rs.etf.sab.operations.UserOperations;

public class vn170094_UserOperations implements UserOperations {

	@Override
	public int declareAdmin(String userName) {
		if(userName==null) {
			System.out.println("userName je null, vratio 2");
			return 2;
		}
		System.out.println("Usao u declareAdmin za " + userName);
		String userExists = "SELECT idK FROM Korisnik WHERE username = ?";
		Connection connection = DB.getInstance().getConnection();
		try (PreparedStatement ps = connection.prepareStatement(userExists);) {
			ps.setString(1, userName);
			try (ResultSet rs = ps.executeQuery();) {
				if (rs.next()) {
					int idK = rs.getInt(1);
					try (Statement st = connection.createStatement();
							ResultSet resSet = st
									.executeQuery("SELECT idAdmin FROM Administrator WHERE idAdmin = " + idK);) {
						if (resSet.next()) {
							System.out.println("Vec je admin");
							return 1;
						} else {
							st.execute("INSERT INTO Administrator VALUES(" + idK + ")");
							System.out.println("Uspesno dodao admina");
							return 0;
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Korisnik ne postoji");
		return 2;
	}

	public static boolean userWithUsernameExists(String name, List<Integer> broj) throws SQLException {
		System.out.println("Usao u userWithUsernameExists");
		Connection connection = DB.getInstance().getConnection();
		String querry = "SELECT IdK FROM Korisnik WHERE Username = ?";
		try (Statement statement = connection.createStatement();
				PreparedStatement ps = connection.prepareStatement(querry);) {
			ps.setString(1, name);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					if (broj != null) {
						broj.add(rs.getInt(1));
					}
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int deleteUsers(String... userNames) {
		System.out.println("Usao u deleteCity multiple names");
		Connection connection = DB.getInstance().getConnection();
		String query = "DELETE FROM Korisnik WHERE Username = ?";
		if (userNames==null || userNames.length < 1) {
			System.out.println("UserNames je null ili nema elemenata, vratio 0");
			return 0;
		}
		int deletedUsers = 0;
		for (int i = 0; i < userNames.length; ++i) {
			try (PreparedStatement ps = connection.prepareStatement(query);) {
				ps.setString(1, userNames[i]);
				deletedUsers += ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Deleted users = " + deletedUsers);
		return deletedUsers;
	}

	@Override
	public List<String> getAllUsers() {
		System.out.println("Usao u getAllUsers");
		String query = "SELECT username FROM Korisnik";
		Connection connection = DB.getInstance().getConnection();
		List<String> allUsers = new LinkedList<String>();
		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
			while (resultSet.next()) {
				allUsers.add(resultSet.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
//		System.out.println("USAO");
//		for (int i = 0; i < allUsers.size(); ++i) {
//			System.out.println(allUsers.get(i));
//		}
		return allUsers;
	}

	@Override
	public Integer getSentPackages(String... userNames) {
		System.out.println("Usao u getSentPackages");
		Connection connection = DB.getInstance().getConnection();
		Integer number = null;
		if(userNames==null || userNames.length<1) {
			System.out.println("Nije prosledjena dobar niz imena (mozda je null), vratio null");return null;
		}
		try (PreparedStatement ps = connection
				.prepareStatement("SELECT BrojPoslatihPaketa FROM Korisnik WHERE username = ?");) {
			for (int i = 0; i < userNames.length; ++i) {
				ps.setString(1, userNames[i]);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						if (number == null) {
							number = rs.getInt(1);
						} else {
							number += rs.getInt(1);
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Number returned is " + (number == null ? "null" : number.toString()));
		return number;
	}

	@Override
	public boolean insertUser(String username, String firstName, String lastName, String password) {
		System.out.println("Usao u insertUser " + username + " " + firstName + " " + lastName + " " + password);
		if (username==null|| password==null || firstName == null || lastName == null || firstName.length() == 0 || lastName.length() == 0) {
			System.out.println("Nije uneto ime ili prezime");
			return false;
		}
		if (!(firstName.charAt(0) >= 'A' && firstName.charAt(0) <= 'Z')
				|| !(lastName.charAt(0) >= 'A' && lastName.charAt(0) <= 'Z')) {
			System.out.println("Ime ili prezime ne pocinje velikim slovom!");
			return false;
		}
		String pattern = "^(?=.*[0-9])(?=.*[a-zA-Z]).{8,}$"; // treba 9 al negde u testu ima neko sa 8
		if (!password.matches(pattern)) {
			System.out.println("Sifra nije u dobrom formatu!");
			return false;
		}
		String query = "SELECT IdK FROM Korisnik WHERE Username = ?";
		Connection connection = DB.getInstance().getConnection();
		try (PreparedStatement ps = connection.prepareStatement(query)) {
			ps.setString(1, username);
			try (ResultSet rs = ps.executeQuery();) {
				if (rs.next()) {
					System.out.println("Postoji vec korisnik sa datim username-om");
					return false;
				} else {
					try (PreparedStatement ps2 = connection
							.prepareStatement("INSERT INTO Korisnik VALUES(?,?,?,0,?)");) {
						ps2.setString(1, firstName);
						ps2.setString(2, lastName);
						ps2.setString(3, password);
						ps2.setString(4, username);
						ps2.execute();
						System.out.println("Uspesno dodao korisnika");
						return true;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Neuspesno dodavanje korisnika");
		return false;
	}

}
