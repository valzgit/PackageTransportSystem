package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import rs.etf.sab.operations.CourierOperations;

public class vn170094_CourierOperations implements CourierOperations {

	@Override
	public boolean deleteCourier(String courierUserName) {
		if(courierUserName==null) {
			System.out.println("Neuspesno brisanje kurira jer je courierUserName null, returned false");
			return false;
		}
		System.out.println("Usao u deleteCourier " + courierUserName);
		String sql = "SELECT k.idK as 'idK' FROM Korisnik k, Kurir kur WHERE k.idK=kur.idKurir AND k.username = ?";
		Connection connection = DB.getInstance().getConnection();
		try (PreparedStatement ps = connection.prepareStatement(sql);) {
			ps.setString(1, courierUserName);
			try (ResultSet rs = ps.executeQuery();) {
				if (rs.next()) {
					int id = rs.getInt(1);
					try (Statement s = connection.createStatement()) {
						s.execute("DELETE FROM Vozi WHERE idKurir = " + id); // ovo mozda ne mora!
						s.execute("DELETE FROM Kurir WHERE idKurir = " + id);						
						System.out.println("Uspesno obrisan kurir");
						return true;
					}
				} else {
					System.out.println("Ne postoji dati korisnik");
					return false;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Nije uspeo da obrise kurira");
		return false;
	}

	@Override
	public List<String> getAllCouriers() {
		System.out.println("Usao u getAllCouriers");
		String sql = "SELECT k.username as 'username' FROM Korisnik k, Kurir kur WHERE k.idK=kur.idKurir ORDER BY Profit DESC";
		List<String> allCouriers = new LinkedList<String>();
		Connection connection = DB.getInstance().getConnection();
		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql);) {
			while (resultSet.next()) {
				allCouriers.add(resultSet.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
//		for (int i = 0; i < allCouriers.size(); ++i) {
//			System.out.println(allCouriers.get(i));
//		}
		return allCouriers;
	}

	@Override
	public BigDecimal getAverageCourierProfit(int numberOfDeliveries) {
		System.out.println("Usao u getAverageCourierProfit num of deliveries = " + numberOfDeliveries);
		String brojKuriraQuery = "SELECT AVG(Profit) FROM Kurir WHERE BrojIspPaketa>= "+numberOfDeliveries;
		Connection connection = DB.getInstance().getConnection();
		BigDecimal retNum = null;
		try(Statement s = connection.createStatement(); ResultSet rs = s.executeQuery(brojKuriraQuery);){
			if(rs.next()) {
				retNum = rs.getBigDecimal(1);
				System.out.println("Average courier profit is " + retNum);
				return retNum;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Error!");
		return retNum;
	}

	@Override
	public List<String> getCouriersWithStatus(int statusOfCourier) {
		System.out.println("Usao u getCouriersWithStatus " + statusOfCourier);
		String sql = "SELECT k.username as 'username' FROM Korisnik k, Kurir kur WHERE k.idK=kur.idKurir AND kur.Status = "
				+ statusOfCourier;
		List<String> allCouriers = new LinkedList<String>();
		Connection connection = DB.getInstance().getConnection();
		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql);) {
			while (resultSet.next()) {
				allCouriers.add(resultSet.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
//		for (int i = 0; i < allCouriers.size(); ++i) {
//			System.out.println(allCouriers.get(i));
//		}
		return allCouriers;
	}

	@Override
	public boolean insertCourier(String courierUserName, String licencePlateNumber) {
		if(courierUserName==null || licencePlateNumber==null) {
			System.out.println("Neka od prosledjenih vrednosti je null, vraca false");
			return false;
		}
		System.out.println("Usao u insertCourier " + courierUserName + " " + licencePlateNumber);
		Connection connection = DB.getInstance().getConnection();
		try {
			ArrayList<Integer> broj = new ArrayList<Integer>();
			ArrayList<Integer> brojVozila = new ArrayList<Integer>();
			if (vn170094_VehicleOperations.vehicleWithLicenseplateExists(licencePlateNumber, brojVozila)
					&& vn170094_UserOperations.userWithUsernameExists(courierUserName, broj)) {
				String alreadyCourier = "SELECT idKurir FROM Kurir WHERE idKurir = " + broj.get(0)+" OR idV = "+ brojVozila.get(0);
				try (Statement s = connection.createStatement(); ResultSet rs = s.executeQuery(alreadyCourier)) {
					if (rs.next()) {
						System.out.println("Vec je kruir ili vec neki kurir vozi to vozilo");
						return false;
					} else {
						String insertCourier = "INSERT INTO Kurir VALUES (" + broj.get(0) + "," + brojVozila.get(0) + ",0,0,0)";
						s.execute(insertCourier);
						System.out.println("Uspesno dodao kurira");
						return true;
					}
				}
			} else {
				System.out.println("Ne postoji korisnik ili auto");
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println("Nije uspeo da doda kurira");
		return false;
	}

}
