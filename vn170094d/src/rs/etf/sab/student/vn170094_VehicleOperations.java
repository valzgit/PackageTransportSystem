package rs.etf.sab.student;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import rs.etf.sab.operations.VehicleOperations;

public class vn170094_VehicleOperations implements VehicleOperations {

	public static boolean vehicleWithLicenseplateExists(String licensePlateNumber, List<Integer> brojVozila)
			throws SQLException {
		System.out.println("Usao u vehicleWithLicenseplateExists " + licensePlateNumber);
		Connection connection = DB.getInstance().getConnection();
		String querry = "SELECT idV FROM Vozilo WHERE RegBroj = ?";
		try (Statement statement = connection.createStatement();
				PreparedStatement ps = connection.prepareStatement(querry);) {
			ps.setString(1, licensePlateNumber);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					if (brojVozila != null)
						brojVozila.add(rs.getInt(1));
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean changeConsumption(String licensePlateNumber, BigDecimal fuelConsumption) {	
		if(licensePlateNumber==null || fuelConsumption==null) {
			System.out.println("Neka od prosledjenih vrednosti je null!");
			return false;
		}
		System.out.println("Usao u changeConsumption za " + licensePlateNumber);
		fuelConsumption = fuelConsumption.setScale(3, RoundingMode.HALF_EVEN);
		Connection connection = DB.getInstance().getConnection();
		try (PreparedStatement ps = connection
				.prepareStatement("UPDATE Vozilo SET Potrosnja = " + fuelConsumption + " WHERE RegBroj = ?");) {
			ps.setString(1, licensePlateNumber);
			int brojUpdejtovanih = ps.executeUpdate();
			if (brojUpdejtovanih > 0) {
				System.out.println("Uspesno promenio potrosnju vozila");
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Neuspesno promenio potrosnju vozila");
		return false;
	}

	@Override
	public boolean changeFuelType(String licensePlateNumber, int fuelType) {
		if(licensePlateNumber==null) {
			System.out.println("Neka od prosledjenih vrednosti je null!");
			return false;
		}
		System.out.println("Usao u changeFuelType za " + licensePlateNumber);
		Connection connection = DB.getInstance().getConnection();
		try (PreparedStatement ps = connection
				.prepareStatement("UPDATE Vozilo SET TipGoriva = " + fuelType + " WHERE RegBroj = ?");) {
			ps.setString(1, licensePlateNumber);
			int brojUpdejtovanih = ps.executeUpdate();
			if (brojUpdejtovanih > 0) {
				System.out.println("Uspesno promenio tip goriva");
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Neuspesno promenio tip goriva");
		return false;
	}

	@Override
	public int deleteVehicles(String... licencePlateNumbers) {
		System.out.println("Usao u deleteVehicles multiple licence plates");
		Connection connection = DB.getInstance().getConnection();
		String query = "DELETE FROM Vozilo WHERE RegBroj = ?";
		if (licencePlateNumbers==null || licencePlateNumbers.length < 1) {
			System.out.println("Niz je null ili ima 0 elemenata, vratio 0");
			return 0;
		}			
		int deletedVehicles = 0;
		for (int i = 0; i < licencePlateNumbers.length; ++i) {
			try (PreparedStatement ps = connection.prepareStatement(query);) {
				ps.setString(1, licencePlateNumbers[i]);
				deletedVehicles += ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return deletedVehicles;
	}

	@Override
	public List<String> getAllVehichles() {
		System.out.println("Usao u getAllVehichles");
		String query = "SELECT RegBroj FROM Vozilo";
		Connection connection = DB.getInstance().getConnection();
		List<String> allVehicles = new LinkedList<String>();
		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
			while (resultSet.next()) {
				allVehicles.add(resultSet.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
//		System.out.println("USAO");
//		for (int i = 0; i < allVehicles.size(); ++i) {
//			System.out.println(allVehicles.get(i));
//		}
		return allVehicles;
	}

	@Override
	public boolean insertVehicle(String licencePlateNumber, int fuelType, BigDecimal fuelConsumtion) {
		if(licencePlateNumber==null || fuelConsumtion==null) {
			System.out.println("Neka od prosledjenih vrednosti je null");
			return false;
		}
		fuelConsumtion = fuelConsumtion.setScale(3, RoundingMode.HALF_EVEN);
		System.out.println("Usao u insertVehicle ID:" + licencePlateNumber + " tip goriva: " + fuelType + " gorivo: "
				+ fuelConsumtion);
		Connection connection = DB.getInstance().getConnection();
		String insertVehicle = "INSERT INTO Vozilo VALUES(?," + fuelType + "," + fuelConsumtion + ")";
		try {
			if (vehicleWithLicenseplateExists(licencePlateNumber, null)) {
				System.out.println("Vozilo vec postoji");
				return false;
			} else {
				try (PreparedStatement ps = connection.prepareStatement(insertVehicle);) {
					ps.setString(1, licencePlateNumber);
					ps.execute();
					System.out.println("Uspesno dodao vozilo");
					return true;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Neuspesno dodao vozilo");
		return false;
	}

}
