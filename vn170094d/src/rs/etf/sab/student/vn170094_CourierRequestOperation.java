package rs.etf.sab.student;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import rs.etf.sab.operations.CourierRequestOperation;

public class vn170094_CourierRequestOperation implements CourierRequestOperation {

	@Override
	public boolean changeVehicleInCourierRequest(String userName, String licencePlateNumber) {
		if(userName==null || licencePlateNumber==null) {
			System.out.println("userName ili licencePlateNumber su null, returned false");
			return false;
		}
		System.out.println("Usao u changeVehicleInCourierRequest " + userName + " " + licencePlateNumber);
		Connection connection = DB.getInstance().getConnection();
		ArrayList<Integer> idK = new ArrayList<Integer>();
		ArrayList<Integer> idV = new ArrayList<Integer>();
		try {
			if (vn170094_UserOperations.userWithUsernameExists(userName, idK)
					&& vn170094_VehicleOperations.vehicleWithLicenseplateExists(licencePlateNumber, idV)) {
				String vehicleHasOwner = "SELECT idKurir FROM Kurir WHERE idV = " + idV.get(0);
				try(Statement s1 = connection.createStatement();ResultSet veh = s1.executeQuery(vehicleHasOwner)){
					if(veh.next()) {
						System.out.println("Vozilo vec vozi neki kurir, returned false");
						return false;
					}
				}
				String sql = "UPDATE ZahtevPostaneKurir SET idV = " + idV.get(0) + " WHERE idK = " + idK.get(0);
				try (Statement s = connection.createStatement();) {
					int changedRows = s.executeUpdate(sql);
					if (changedRows > 0) {
						System.out.println("Uspeo da updejtuje");
						return true;
					} else {
						System.out.println("Nije uspeo da updejtuje jer nije nasao zahtev");
						return false;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Nije uspeo da updejtuje");
		return false;
	}

	@Override
	public boolean deleteCourierRequest(String userName) {
		if(userName==null) {
			System.out.println("Username is null, returned false");
			return false;
		}
		System.out.println("Usao u deleteCourierRequest " + userName);
		Connection connection = DB.getInstance().getConnection();
		ArrayList<Integer> idK = new ArrayList<Integer>();
		try {
			if (vn170094_UserOperations.userWithUsernameExists(userName, idK)) {
				String sql = "DELETE FROM ZahtevPostaneKurir WHERE idK = " + idK.get(0);
				try (Statement s = connection.createStatement();) {
					int numDeleted = s.executeUpdate(sql);
					if (numDeleted > 0) {
						System.out.println("Uspesno obrisan zahtev");
						return true;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Neuspesno obrisan zahtev");
		return false;
	}

	@Override
	public List<String> getAllCourierRequests() {
		System.out.println("Usao u getAllCourierRequests");
		String query = "SELECT username FROM Kurir WHERE idK IN (SELECT idK FROM ZahtevPostaneKurir)";
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
	public boolean grantRequest(String username) {
		if(username==null) {
			System.out.println("Prosledjeni username je null! returned false");
			return false;
		}
		System.out.println("Usao u grantRequest za korisnika " + username);
		String sql = "{ call OdobrenjeZahteva(?,?) }";
		Connection connection = DB.getInstance().getConnection();
		try (CallableStatement cs = connection.prepareCall(sql);) {
			cs.setString(1, username);
			cs.registerOutParameter(2, java.sql.Types.INTEGER);
			cs.execute();
			int broj = cs.getInt(2);
			System.out.print("Broj je = " + broj + " ");
			if (broj == 1) {
				System.out.println("Uspeo da odobri zahtev");
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Nije uspeo da odobri zahtev");
		return false;
	}

	@Override
	public boolean insertCourierRequest(String userName, String licencePlateNumber) {
		if(userName==null || licencePlateNumber==null) {
			System.out.println("Neka od prosledjenih vrednosti [userName,licencePlateNumber] je null");
			return false;
		}
		System.out.println("Usao u insertCourierRequest " + userName + " " + licencePlateNumber);
		Connection connection = DB.getInstance().getConnection();
		ArrayList<Integer> idK = new ArrayList<Integer>();
		ArrayList<Integer> idV = new ArrayList<Integer>();
		try {
			if (vn170094_UserOperations.userWithUsernameExists(userName, idK)
					&& vn170094_VehicleOperations.vehicleWithLicenseplateExists(licencePlateNumber, idV)) {
				String alreadyCourier = "SELECT idKurir FROM Kurir WHERE idKurir = " + idK.get(0)+" OR idV = "+idV.get(0);
				try (Statement s = connection.createStatement(); ResultSet rs = s.executeQuery(alreadyCourier)) {
					if (rs.next()) {
						System.out.println("Korisnik je vec kurir ili vozilo vec neko vozi");
						return false;
					} else {
						String alreadyHasRequest = "SELECT idK FROM ZahtevPostaneKurir WHERE idK = " + idK.get(0);
						try (ResultSet rs2 = s.executeQuery(alreadyHasRequest);) {
							if (rs2.next()) {
								System.out.println("Korisnik je vec poslao zahtev");
								return false;
							} else {
								// TODO Ovde mozda treba i admina naci kome se salje zahtev
								s.execute(
										"INSERT INTO ZahtevPostaneKurir VALUES(" + idK.get(0) + "," + idV.get(0) + ")");
								System.out.println("Zahtev je napravljen");
								return true;
							}
						}

					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Neuspesno dodao zahtev");
		return false;
	}

}
