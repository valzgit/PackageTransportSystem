package rs.etf.sab.student;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import rs.etf.sab.operations.PackageOperations;

public class vn170094_PackageOperations implements PackageOperations {
	static int[] pocetnaCena = { 10, 25, 75 };
	static int[] tezinskiFaktor = { 0, 1, 2 };
	static int[] cenaPoKG = { 0, 100, 300 };
	static int[] tipGorivaCene = { 15, 32, 36 };

	@Override
	public boolean acceptAnOffer(int offerId) {
		System.out.println("Usao u acceptAnOffer " + offerId);
		String sql = "SELECT idKurir,Procenat,idZahteva FROM Ponuda WHERE idPonuda = " + offerId;
		Connection connection = DB.getInstance().getConnection();
		try (Statement s = connection.createStatement(); ResultSet rs = s.executeQuery(sql);) {
			if (rs.next()) {
				int idKurir = rs.getInt(1);
				BigDecimal procenat = rs.getBigDecimal(2);
				int idZahteva = rs.getInt(3);
				String sredi = "UPDATE Ponuda SET Accepted = 1 WHERE idPonuda = " + offerId;
				s.execute(sredi);
				String cenaPaketa = "SELECT Cena FROM Paket WHERE idP = " + idZahteva;
				try (ResultSet rs2 = s.executeQuery(cenaPaketa)) {
					if (rs2.next()) {
						BigDecimal cena = rs2.getBigDecimal(1);
						double zarada = (procenat.doubleValue() + 100.0) / 100.0 * cena.doubleValue();
						Timestamp timestamp = new Timestamp(System.currentTimeMillis());
						String srediPaket = "UPDATE Paket SET Status = 1, idKurir = " + idKurir
								+ ",VremePrihvatanja = ? , Cena = " + zarada + " WHERE idP = " + idZahteva;
						// ovde mozda treba ukloniti novu cenu
						try (PreparedStatement ps = connection.prepareStatement(srediPaket)) {
							ps.setTimestamp(1, timestamp);
							ps.execute();
							System.out.println("-----USPENO JE PRIHVATIO PONUDU----- \n ID = " + offerId + " cena ="
									+ cena + " zarada = " + zarada);
							return true;
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Neuspesno je prihvatio ponudu!");
		return false;
	}

	@Override
	public boolean changeType(int packageId, int newType) {
		System.out.println("Usao u changeType packageId = " + packageId + " newType = " + newType);
		String smeLiDaMenja = "SELECT idP FROM Paket WHERE idP = " + packageId + " AND idKurir IS NULL";
		String novacena = "SELECT TipPaketa,TezinaPaketa,OpstinaOd,OpstinaDo FROM ZahtevZaPrevozom WHERE idZahteva = "
				+ packageId;		
		Connection connection = DB.getInstance().getConnection();
		try (Statement spom = connection.createStatement(); ResultSet rs = spom.executeQuery(smeLiDaMenja)) {
			if (rs.next()) {
				try (ResultSet rs2 = spom.executeQuery(novacena)) {
					int tipPaketa=0;
					double tezinaPaketa= 0;
					int OpstinaOd = 0;
					int OpstinaDo = 0;
					if(rs2.next()) {
						 tipPaketa = rs2.getInt(1); // ovo je nepotrebno
						 tezinaPaketa = rs2.getBigDecimal(2).doubleValue();
						 OpstinaOd = rs2.getInt(3);
						 OpstinaDo = rs2.getInt(4);
					}			
					else {
						System.out.println("NEUSPESNO NALAZENJE ZAHTEVAZAPREVOZOM");
						return false;
					}
					String checkDistrict1 = "SELECT X,Y FROM Opstina WHERE IdO = " + OpstinaOd;
					String checkDistrict2 = "SELECT X,Y FROM Opstina WHERE IdO = " + OpstinaDo;
					int x1 = 0;
					int x2 = 0;
					int y1 = 0;
					int y2 = 0;					
					try(ResultSet rsopst1 = spom.executeQuery(checkDistrict1)){
						if(rsopst1.next()) {
							x1 = rsopst1.getInt(1);
							y1 = rsopst1.getInt(2);
						}
					}
					try(ResultSet rsopst2 = spom.executeQuery(checkDistrict2)){
						if(rsopst2.next()) {
							x2 = rsopst2.getInt(1);
							y2 = rsopst2.getInt(2);
						}
					}
					double distance = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
					System.out.println("Distance:" + distance);
					double cena = (pocetnaCena[newType]
							+ (tezinaPaketa * tezinskiFaktor[newType]) * cenaPoKG[newType])
							* distance;
					System.out.println("Cena:" + cena + "\n");
					String sql = "UPDATE ZahtevZaPrevozom SET TipPaketa = " + newType + " WHERE idZahteva = " + packageId;
					String packageUpdate = "UPDATE Paket SET Cena = "+cena+" WHERE idP = "+packageId;
					int uspeo = spom.executeUpdate(sql);
					if (uspeo > 0) {
						System.out.println("Uspesno UPDEJT-ovao tip");
						spom.execute(packageUpdate);
						return true;
					}

				}
			} else {
				System.out.println("Paket je vec dodeljen kuriru, ne moze da menja tip");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println("Neuspesno UPDEJT-ovao tip");
		return false;
	}

	@Override
	public boolean changeWeight(int packageId, BigDecimal newWeight) {
		System.out.println("Usao u changeWeight packageId = " + packageId + " newWeight = " + newWeight);
		String smeLiDaMenja = "SELECT idP FROM Paket WHERE idP = " + packageId + " AND idKurir IS NULL";
		String novacena = "SELECT TipPaketa,TezinaPaketa,OpstinaOd,OpstinaDo FROM ZahtevZaPrevozom WHERE idZahteva = "
				+ packageId;		
		Connection connection = DB.getInstance().getConnection();
		try (Statement spom = connection.createStatement(); ResultSet rs = spom.executeQuery(smeLiDaMenja)) {
			if (rs.next()) {
				try (ResultSet rs2 = spom.executeQuery(novacena)) {
					int tipPaketa=0;
					double tezinaPaketa= 0;
					int OpstinaOd = 0;
					int OpstinaDo = 0;
					if(rs2.next()) {
						 tipPaketa = rs2.getInt(1); 
						 tezinaPaketa = rs2.getBigDecimal(2).doubleValue(); // ovo je nepotrebno
						 OpstinaOd = rs2.getInt(3);
						 OpstinaDo = rs2.getInt(4);
					}			
					else {
						System.out.println("NEUSPESNO NALAZENJE ZAHTEVAZAPREVOZOM");
						return false;
					}
					String checkDistrict1 = "SELECT X,Y FROM Opstina WHERE IdO = " + OpstinaOd;
					String checkDistrict2 = "SELECT X,Y FROM Opstina WHERE IdO = " + OpstinaDo;
					int x1 = 0;
					int x2 = 0;
					int y1 = 0;
					int y2 = 0;					
					try(ResultSet rsopst1 = spom.executeQuery(checkDistrict1)){
						if(rsopst1.next()) {
							x1 = rsopst1.getInt(1);
							y1 = rsopst1.getInt(2);
						}
					}
					try(ResultSet rsopst2 = spom.executeQuery(checkDistrict2)){
						if(rsopst2.next()) {
							x2 = rsopst2.getInt(1);
							y2 = rsopst2.getInt(2);
						}
					}
					double distance = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
					System.out.println("Distance:" + distance);
					double cena = (pocetnaCena[tipPaketa]
							+ (newWeight.doubleValue() * tezinskiFaktor[tipPaketa]) * cenaPoKG[tipPaketa])
							* distance;
					System.out.println("Cena:" + cena + "\n");
					String sql = "UPDATE ZahtevZaPrevozom SET TezinaPaketa = " + newWeight + " WHERE idZahteva = " + packageId;
					String packageUpdate = "UPDATE Paket SET Cena = "+cena+" WHERE idP = "+packageId;
					int uspeo = spom.executeUpdate(sql);
					if (uspeo > 0) {
						System.out.println("Uspesno UPDEJT-ovao tezinu");
						spom.execute(packageUpdate);
						return true;
					}

				}
			} else {
				System.out.println("Paket je vec dodeljen kuriru, ne moze da menja tezinu");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println("Neuspesno UPDEJT-ovao tezinu");
		return false;
	}

	@Override
	public boolean deletePackage(int packageId) {
		System.out.println("Usao u deletePackage packageId = " + packageId);
		Connection connection = DB.getInstance().getConnection();
		String kurirVozi = "SELECT idKurir FROM Paket WHERE idP = " + packageId;
		try (Statement s = connection.createStatement(); ResultSet rs = s.executeQuery(kurirVozi)) {
			if (rs.next()) {
				int idKurir = rs.getInt(1);
				String brojPaketaKurira = "SELECT COUNT(*) FROM Paket WHERE idKurir = " + idKurir;
				try (ResultSet rs2 = s.executeQuery(brojPaketaKurira)) {
					if (rs2.next()) {
						int count = rs2.getInt(1);
						if (count == 1) {
							s.execute("DELETE FROM Vozi WHERE idKurir = " + idKurir
									+ "; UPDATE Kurir SET Status=0 WHERE idKurir = " + idKurir);
						}
					}
				}
			}
			String sql = "DELETE FROM Paket WHERE idP = " + packageId;
			int deletedRows = s.executeUpdate(sql);
			if (deletedRows > 0) {
				s.execute("DELETE FROM Ponuda WHERE idZahteva = " + packageId
						+ "; DELETE FROM ZahtevZaPrevozom WHERE idZahteva = " + packageId);
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public int driveNextPackage(String courierUserName) {
		System.out.println("Usao u driveNextPackage courierUserName = " + courierUserName);
		Connection connection = DB.getInstance().getConnection();
		String getCourier = "SELECT IdK FROM Korisnik WHERE username = ? AND idK IN (SELECT idKurir FROM Kurir)";
		try (PreparedStatement ps = connection.prepareStatement(getCourier)) {
			ps.setString(1, courierUserName);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					int idKurir = rs.getInt(1);
					String sql = "SELECT idP,Status FROM Paket WHERE Status!=0 AND Status!=3 AND idKurir = " + idKurir
							+ " AND"
							+ " VremePrihvatanja = (SELECT COALESCE(MIN(VremePrihvatanja),0) FROM Paket WHERE Status!=0 AND Status!=3 AND idKurir  = "
							+ idKurir + ")";
					try (Statement s = connection.createStatement(); ResultSet rs2 = s.executeQuery(sql)) {
						if (rs2.next()) {
							int idP = rs2.getInt(1);
							int statusPaketa = rs2.getInt(2);
							String updatePackage = "UPDATE Paket SET Status = Status + 1 WHERE idP = " + idP;
							if (statusPaketa == 2) {
								updatePackage += "; UPDATE Kurir SET BrojIspPaketa = BrojIspPaketa + 1 WHERE idKurir = "
										+ idKurir;
							}
							s.execute(updatePackage);
							String opstinaOd = "SELECT X,Y FROM Opstina WHERE idO = (SELECT OpstinaOd FROM ZahtevZaPrevozom WHERE idZahteva = "
									+ idP + ")";
							String opstinaDo = "SELECT X,Y FROM Opstina WHERE idO = (SELECT OpstinaDo FROM ZahtevZaPrevozom WHERE idZahteva = "
									+ idP + ")";
							int x1 = 0;
							int y1 = 0;
							int x2 = 0;
							int y2 = 0;
							try (ResultSet rs4 = s.executeQuery(opstinaOd)) {
								if (rs4.next()) {
									x1 = rs4.getInt(1);
									y1 = rs4.getInt(2);
								} else {
									System.out.println("Problem sa nalazenjem koordinata opstineOd!");
									return -2;
								}
							}
							try (ResultSet rs4 = s.executeQuery(opstinaDo)) {
								if (rs4.next()) {
									x2 = rs4.getInt(1);
									y2 = rs4.getInt(2);
								} else {
									System.out.println("Problem sa nalazenjem koordinata opstineDo!");
									return -2;
								}
							}
							int tip;
							BigDecimal potrosnja = null;
							try (ResultSet rs4 = s.executeQuery(
									"SELECT TipGoriva,Potrosnja FROM Vozilo WHERE idV IN (SELECT idV FROM Kurir WHERE idKurir = "
											+ idKurir + ")")) {
								if (rs4.next()) {
									tip = rs4.getInt(1);
									potrosnja = rs4.getBigDecimal(2);

								} else {
									System.out.println("Nije uspeo da dohvati auto!");
									return -2;
								}
							}
							String driveExists = "SELECT idKurir,X,Y FROM Vozi WHERE idKurir = " + idKurir;
							try (ResultSet rs3 = s.executeQuery(driveExists)) {
								double gubitakNaGas = 0;
								if (!rs3.next()) {
									String createDrive = "INSERT INTO Vozi VALUES((SELECT idV FROM Kurir WHERE idKurir = "
											+ idKurir + ")," + idKurir + "," + x1 + "," + y1 + ");";
									String updatePackages = "UPDATE Paket SET Status = Status + 1 WHERE idKurir = "
											+ idKurir + ";";
									String statuskurira = "UPDATE Kurir SET Status = 1 WHERE idKurir = " + idKurir;
									if (statusPaketa == 1) { // ovaj deo mozda mora da se obrise
										statuskurira += "; UPDATE Kurir SET BrojIspPaketa = BrojIspPaketa + 1 WHERE idKurir = "
												+ idKurir;
									}
									s.execute(createDrive + updatePackages + statuskurira);
								} else {
									int x_old = rs3.getInt(2);
									int y_old = rs3.getInt(3);
									System.out.println(
											"x_old = " + x_old + " y_old = " + y_old + " x1 = " + x1 + " y1 = " + y1);
									double distance = Math
											.sqrt((x_old - x1) * (x_old - x1) + (y_old - y1) * (y_old - y1));
									gubitakNaGas += distance * potrosnja.doubleValue() * tipGorivaCene[tip];
								}
								System.out.println("x2 = " + x2 + " y2 = " + y2 + " x1 = " + x1 + " y1 = " + y1);
								double distance = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
								gubitakNaGas += distance * potrosnja.doubleValue() * tipGorivaCene[tip];
								System.out.println("IZGUBIO NA GAS = " + gubitakNaGas);
								String updateKurir = "UPDATE Kurir SET Profit = Profit - " + gubitakNaGas
										+ "+ (SELECT Cena FROM Paket WHERE IdP = " + idP + ") WHERE idKurir = "
										+ idKurir;
								String updateVoznja = ";UPDATE Vozi SET X = " + x2 + ", Y= " + y2 + "WHERE idKurir ="
										+ idKurir;
								s.execute(updateKurir + updateVoznja);
								try (ResultSet rs5 = s.executeQuery(sql)) {
									if (!rs5.next()) {
										s.execute("DELETE FROM Vozi WHERE idKurir =" + idKurir
												+ "; UPDATE Kurir SET Status = 0 WHERE idKurir =" + idKurir);
									}
								}
								System.out.println("Vozi paket sa ID-jem " + idP);
								return idP;
							}
						} else {
							System.out.println("Kurir nema sta da dostavlja!");
							return -1;
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Nepoznata greska!");
		return -2;
	}

	@Override
	public Date getAcceptanceTime(int packageId) {
		System.out.println("Usao u getAcceptanceTime packageId = " + packageId);
		Connection connection = DB.getInstance().getConnection();
		String sql = "SELECT VremePrihvatanja FROM Paket WHERE idP = " + packageId;
		try (Statement s = connection.createStatement(); ResultSet rs = s.executeQuery(sql)) {
			if (rs.next()) {
				Timestamp ts = rs.getTimestamp(1);
				System.out.println("Vreme paketa vraceno");
				return new Date(ts.getTime());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Ne postoji paket ili nije jos prihvacen");
		return null;
	}

	@Override
	public List<Integer> getAllOffers() {
		System.out.println("Usao u getAllOffers");
		String querry = "SELECT idPonuda FROM Ponuda";
		Connection connection = DB.getInstance().getConnection();
		List<Integer> allOffers = new LinkedList<Integer>();
		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(querry)) {
			while (resultSet.next()) {
				allOffers.add(resultSet.getInt("idPonuda"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
//		System.out.println("USAO");
//		for (int i = 0; i < allOffers.size(); ++i) {
//			System.out.println(allOffers.get(i));
//		}
		return allOffers;
	}

	@Override
	public List<Pair<Integer, BigDecimal>> getAllOffersForPackage(int packageId) {
		System.out.println("Usao u getAllOffersForPackage packageId = " + packageId);
		String query = "SELECT idZahteva,Procenat FROM Ponuda WHERE idZahteva = " + packageId;
		Connection connection = DB.getInstance().getConnection();
		List<Pair<Integer, BigDecimal>> allPackages = new LinkedList<Pair<Integer, BigDecimal>>();
		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
			while (resultSet.next()) {
				allPackages.add(new PairExtension(resultSet.getInt(1), resultSet.getBigDecimal(2)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
//		System.out.println("USAO");
//		for (int i = 0; i < allPackages.size(); ++i) {
//			System.out.println(allPackages.get(i));
//		}
		return allPackages;
	}

	@Override
	public List<Integer> getAllPackages() {
		System.out.println("Usao u getAllPackages");
		String query = "SELECT idZahteva FROM ZahtevZaPrevozom";
		Connection connection = DB.getInstance().getConnection();
		List<Integer> allPackages = new LinkedList<Integer>();
		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
			while (resultSet.next()) {
				allPackages.add(resultSet.getInt(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
//		System.out.println("USAO");
//		for (int i = 0; i < allPackages.size(); ++i) {
//			System.out.println(allPackages.get(i));
//		}
		return allPackages;
	}

	@Override
	public List<Integer> getAllPackagesWithSpecificType(int type) {
		System.out.println("Usao u getAllPackagesWithSpecificType type = " + type);
		String query = "SELECT idZahteva FROM ZahtevZaPrevozom WHERE TipPaketa = " + type;
		Connection connection = DB.getInstance().getConnection();
		List<Integer> allPackages = new LinkedList<Integer>();
		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
			while (resultSet.next()) {
				allPackages.add(resultSet.getInt(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
//		System.out.println("USAO");
//		for (int i = 0; i < allPackages.size(); ++i) {
//			System.out.println(allPackages.get(i));
//		}
		return allPackages;
	}

	@Override
	public Integer getDeliveryStatus(int packageId) {
		System.out.println("Usao u getDeliveryStatus packageId = " + packageId);
		Connection connection = DB.getInstance().getConnection();
		String sql = "SELECT Status FROM Paket WHERE idP = " + packageId;
		try (Statement s = connection.createStatement(); ResultSet rs = s.executeQuery(sql)) {
			if (rs.next()) {
				int status = rs.getInt(1);
				System.out.println("Uspesno nasao status = " + status);
				return status;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Ne postoji paket sa ovim ID-jem");
		return null;
	}

	@Override
	public List<Integer> getDrive(String courierUsername) {
		if(courierUsername==null) {
			System.out.println("CourierUsername is null");
			return null;
		}
		System.out.println("Usao u getDrive courierUsername = " + courierUsername);
		// ranije je bilo (Status = 1 OR Status = 2) a sada je samo Status = 2 sto znaci
		// da se voze
		String query = "SELECT idP FROM Paket WHERE Status = 2 AND idKurir = (SELECT idK FROM Korisnik WHERE username = ?)";
		Connection connection = DB.getInstance().getConnection();
		List<Integer> allDrives = null;
		try (PreparedStatement ps = connection.prepareStatement(query);) {
			ps.setString(1, courierUsername);
			try (ResultSet resultSet = ps.executeQuery(query)) {
				while (resultSet.next()) {
					if (allDrives == null)
						allDrives = new LinkedList<Integer>();
					allDrives.add(resultSet.getInt(1));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
//		System.out.println("USAO");
//		for (int i = 0; i < allDrives.size(); ++i) {
//			System.out.println(allDrives.get(i));
//		}
		if (allDrives == null) {
			System.out.println("Ne postoji voznja!");
		} else {
			System.out.println("Vozi se " + allDrives.size() + " paketa");
		}
		return allDrives;
	}

	@Override
	public BigDecimal getPriceOfDelivery(int packageId) {
		System.out.println("Usao u getPriceOfDelivery packageId = " + packageId);
		Connection connection = DB.getInstance().getConnection();
		String sql = "SELECT Cena FROM Paket WHERE idP = " + packageId;
		// zakomentarisati deo ispod ako se cena racuna i pre kurira
		sql += " AND idKurir IS NOT NULL";
		try (Statement s = connection.createStatement(); ResultSet rs = s.executeQuery(sql)) {
			if (rs.next()) {
				BigDecimal cena = rs.getBigDecimal(1);
				System.out.println("Uspesno nasao cenu = " + cena);
				return cena;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Ne postoji paket sa ovim ID-jem (ili jos nije kreirana cena)");
		return null;
	}

	@Override
	public int insertPackage(int districtFrom, int districtTo, String userName, int packageType, BigDecimal weight) {
		if(userName==null || weight==null) {
			System.out.println("Username ili weight su null,vratio -1");
			return -1;
		}
		weight = weight.setScale(3, RoundingMode.HALF_EVEN);
		System.out.println("Usao u insertPackage districtFrom = " + districtFrom + " districtTo = " + districtTo
				+ " userName = " + userName);
		Connection connection = DB.getInstance().getConnection();
		String checkDistrict1 = "SELECT IdO,X,Y FROM Opstina WHERE IdO = " + districtFrom;
		String checkDistrict2 = "SELECT IdO,X,Y FROM Opstina WHERE IdO = " + districtTo;
		ArrayList<Integer> idK = new ArrayList<Integer>();
		try (Statement s = connection.createStatement(); ResultSet rs = s.executeQuery(checkDistrict1);) {
			if (rs.next()) {
				int x1 = rs.getInt(2);
				int y1 = rs.getInt(3);
				try (ResultSet rs2 = s.executeQuery(checkDistrict2)) {
					if (rs2.next() && vn170094_UserOperations.userWithUsernameExists(userName, idK)) {
						int x2 = rs2.getInt(2);
						int y2 = rs2.getInt(3);					
						System.out.println("\nx1 = " + x1 + " x2 = " + x2 + " y1 = " + y1 + " y2 = " + y2);
						double distance = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
						System.out.println("Distance:" + distance);
						double cena = (pocetnaCena[packageType]
								+ (weight.doubleValue() * tezinskiFaktor[packageType]) * cenaPoKG[packageType])
								* distance;
						System.out.println("Cena:" + cena + "\n");
						String query = "INSERT INTO ZahtevZaPrevozom VALUES(" + idK.get(0) + "," + districtFrom + ","
								+ districtTo + "," + packageType + "," + weight + ")";
						int max = -1;
						try (PreparedStatement ps = connection.prepareStatement(query,
								PreparedStatement.RETURN_GENERATED_KEYS)) {
							ps.execute();
							try (ResultSet keys = ps.getGeneratedKeys()) {
								if (keys.next()) {
									max = keys.getInt(1);
								}
							}
							String paketQuery = "INSERT INTO Paket(Status,idP,Cena) VALUES(0," + max + "," + cena + ")";
							String updateBrojPoslatihPaketa = "; UPDATE Korisnik SET BrojPoslatihPaketa = BrojPoslatihPaketa + 1 WHERE idK = "
									+ idK.get(0);
							s.execute(paketQuery + updateBrojPoslatihPaketa);
							System.out.println("Uspesno kreirao paket sa ID-jem " + max);
							return max;
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Nije uspeo da doda paket");
		return -1;
	}

	@Override
	public int insertTransportOffer(String couriersUserName, int packageId, BigDecimal pricePercentage) {
		if (pricePercentage == null) {
			pricePercentage = BigDecimal.valueOf((Math.random() * 20 - 10));
		}
		if(couriersUserName==null) {
			System.out.println("couriersUserName je null, vratio -1");
			return -1;
		}
		pricePercentage = pricePercentage.setScale(3, RoundingMode.HALF_EVEN);
		System.out.println("Usao u insertTransportOffer couriersUserName = " + couriersUserName + " packageId = "
				+ packageId + " pricePercentage = " + pricePercentage);
		Connection connection = DB.getInstance().getConnection();
		try (Statement s = connection.createStatement();
				ResultSet rs = s.executeQuery(
						"SELECT z.idZahteva FROM ZahtevZaPrevozom z, Paket p WHERE z.idZahteva = p.idP AND p.Status = 0 AND z.idZahteva = "
								+ packageId)) {
			if (rs.next()) {
				ArrayList<Integer> idK = new ArrayList<Integer>();
				if (vn170094_UserOperations.userWithUsernameExists(couriersUserName, idK)) {
					String isKurir = "SELECT idKurir FROM Kurir WHERE idKurir = " + idK.get(0) + "AND Status = 0";
					try (ResultSet rs2 = s.executeQuery(isKurir)) {
						if (rs2.next()) {
							String insert = "INSERT INTO Ponuda(idKurir,idZahteva,Procenat,Accepted) VALUES("
									+ idK.get(0) + "," + packageId + "," + pricePercentage + "," + 0 + ")";
							try (PreparedStatement ps = connection.prepareStatement(insert,
									PreparedStatement.RETURN_GENERATED_KEYS)) {
								ps.execute();
								try (ResultSet keys = ps.getGeneratedKeys()) {
									if (keys.next()) {
										int max = keys.getInt(1);
										System.out.println("Uspesno kreirao ponudu za paket sa IDjem " + packageId
												+ " idPonude = " + max);
										return max;
									}
								}
							}
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Nije uspeo da doda ponudu");
		return -1;
	}

}
