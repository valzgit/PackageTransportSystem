package rs.etf.sab.student;
//import java.math.BigDecimal;

import rs.etf.sab.operations.*;
import rs.etf.sab.tests.TestHandler;
import rs.etf.sab.tests.TestRunner;


public class StudentMain {
    public static void main(String[] args) {
        CityOperations cityOperations = new vn170094_CityOperations(); // Change this to your implementation.
        DistrictOperations districtOperations = new vn170094_DistrictOperations(); // Do it for all classes.
        CourierOperations courierOperations = new vn170094_CourierOperations(); // e.g. = new MyDistrictOperations();
        CourierRequestOperation courierRequestOperation = new vn170094_CourierRequestOperation();
        GeneralOperations generalOperations = new vn170094_GeneralOperations();
        UserOperations userOperations = new vn170094_UserOperations();
        VehicleOperations vehicleOperations = new vn170094_VehicleOperations();
        PackageOperations packageOperations = new vn170094_PackageOperations();
        
        TestHandler.createInstance(
                cityOperations,
                courierOperations,
                courierRequestOperation,
                districtOperations,
                generalOperations,
                userOperations,
                vehicleOperations,
                packageOperations);

        TestRunner.runTests();
//        generalOperations.eraseAll();
//        int cityId = cityOperations.insertCity("Beograd", "11000"); 
//        if (cityOperations.insertCity("Beograd", "11000") != -1 || cityOperations.insertCity("Nis", "11000")!= -1
//        		|| cityOperations.insertCity("Beograd", "44000")!= -1) {
//        	System.out.println("Greska 1 ");
//        	return;
//        }
//
//        int cityId2 = cityOperations.insertCity("Nis", "4000"); 
//        int cityId3 = cityOperations.insertCity("Kraljevo", "36000"); 
//        
//        if(cityOperations.getAllCities().size()!=3) {
//        	System.out.println("Greska 2 ");
//        	return;
//        }
//        int obrisan = cityOperations.deleteCity("Nis");
//        if(obrisan!=1) {
//        	System.out.println("Greska 3 ");
//        	return;
//        }
//        if(cityOperations.deleteCity(cityId2)) {
//        	System.out.println("Greska 4 ");
//        	return;
//        }
//        
//        int district1 = districtOperations.insertDistrict("Vracar", cityId, 0, 0); // OPSTINE U ISTOM GRADU DA SE ZOVU DRUGACIJE
//        int district2 = districtOperations.insertDistrict("Zvezdara", cityId, 11, 5);
//        int district3 = districtOperations.insertDistrict("Stari Grad", cityId, 3, 2);
//        int district4 = districtOperations.insertDistrict("Palilula", cityId, 5, 3);
//        int district5 = districtOperations.insertDistrict("Savski venac", cityId, 5, 17);
//        int district6 = districtOperations.insertDistrict("Zemun", cityId, 1, 9);
//        
//        if(districtOperations.getAllDistricts().size()!=6 || districtOperations.getAllDistrictsFromCity(cityId).size()!=6) {
//        	System.out.println("Greska 5 ");
//        	return;
//        }
//        if(!districtOperations.deleteDistrict(district1) || districtOperations.deleteDistricts("Zvezdara","Stari Grad")!=2 || districtOperations.deleteAllDistrictsFromCity("Beograd")!=3) {
//        	System.out.println("Greska 6 ");
//        	return;
//        }
//        
//         district1 = districtOperations.insertDistrict("Vracar", cityId, 0, 0); 
//         district2 = districtOperations.insertDistrict("Zvezdara", cityId, 11, 5);
//         district3 = districtOperations.insertDistrict("Stari Grad", cityId, 3, 2);
//         district4 = districtOperations.insertDistrict("Palilula", cityId, 5, 3);
//         district5 = districtOperations.insertDistrict("Savski venac", cityId, 5, 17);
//         district6 = districtOperations.insertDistrict("Zemun", cityId, 1, 9);
//         
//        userOperations.insertUser("petar", "Petar", "Peric", "123habanero");
//        userOperations.insertUser("milovan", "Milovan", "Peric", "123habanero");
//        userOperations.insertUser("grilovan", "Grilovan", "Peric", "123habanero");
//        
//        if(userOperations.declareAdmin("petar")!=0 || userOperations.declareAdmin("petar")!=1 || userOperations.declareAdmin("milivoje")!=2) {
//        	System.out.println("Greska 7 ");
//        	return;
//        }
//        userOperations.deleteUsers("petar");
//        int brojK1 = userOperations.getAllUsers().size();
//        userOperations.insertUser("petar", "Petar", "Peric", "123habanero");
//        int brojK2 = userOperations.getAllUsers().size();
//        if(brojK1+brojK2!=5) {
//        	System.out.println("Greska 8 ");
//        	return;
//        }
//        
//        vehicleOperations.insertVehicle("BG430DD", 0, new BigDecimal(8.0));
//        vehicleOperations.insertVehicle("BG500DD", 1, new BigDecimal(8.7));
//        vehicleOperations.insertVehicle("BG800DD", 2, new BigDecimal(9.2));
//        
//        courierOperations.insertCourier("milovan", "BG430DD");
//        courierOperations.insertCourier("grilovan", "BG500DD");
//        int packageId1 = packageOperations.insertPackage(district1, district2, "petar", 1, new BigDecimal(122));
//        int packageId2 = packageOperations.insertPackage(district1, district3, "petar", 2, new BigDecimal(52));
//        int packageId3 = packageOperations.insertPackage(district4, district6, "petar", 1, new BigDecimal(272));
//        int packageId4 = packageOperations.insertPackage(district2, district4, "petar", 2, new BigDecimal(122));
//        int packageId5 = packageOperations.insertPackage(district3, district5, "petar", 0, new BigDecimal(42));
//        int packageId6 = packageOperations.insertPackage(district1, district5, "petar", 2, new BigDecimal(58));
//        int packageId7 = packageOperations.insertPackage(district5, district2, "petar", 1, new BigDecimal(173));
//        int packageId8 = packageOperations.insertPackage(district2, district1, "petar", 1, new BigDecimal(412));
//        int packageId9 = packageOperations.insertPackage(district4, district3, "petar", 2, new BigDecimal(32));
//        
//        
//        int offer1 = packageOperations.insertTransportOffer("milovan", packageId1, new BigDecimal(5));
//        packageOperations.insertTransportOffer("grilovan", packageId1, new BigDecimal(5));
//        int offer2 = packageOperations.insertTransportOffer("milovan", packageId2, new BigDecimal(5));
//        packageOperations.insertTransportOffer("grilovan", packageId2, new BigDecimal(5));
//        int offer3 = packageOperations.insertTransportOffer("milovan", packageId3, new BigDecimal(5));
//        packageOperations.insertTransportOffer("grilovan", packageId3, new BigDecimal(5));
//        int offer4 = packageOperations.insertTransportOffer("milovan", packageId4, new BigDecimal(5));
//        int offer5 = packageOperations.insertTransportOffer("milovan", packageId5, new BigDecimal(5));
//        int offer6 = packageOperations.insertTransportOffer("milovan", packageId6, new BigDecimal(5));
//        int offer7 = packageOperations.insertTransportOffer("milovan", packageId7, new BigDecimal(5));
//        int offer8 = packageOperations.insertTransportOffer("milovan", packageId8, new BigDecimal(5));
//        int offer9 = packageOperations.insertTransportOffer("milovan", packageId9, new BigDecimal(5));    
//        packageOperations.changeType(packageId1, 0);
//        packageOperations.changeWeight(packageId1, new BigDecimal(346));
//        packageOperations.changeType(packageId1, 2);
//        if(packageOperations.getAllOffers().size()!=12) {
//        	System.out.println("Greska 9 ");
//        	return;
//        }
//        
//        packageOperations.acceptAnOffer(offer1);
//        packageOperations.acceptAnOffer(offer2);
//        packageOperations.acceptAnOffer(offer3);
//        packageOperations.acceptAnOffer(offer4);
//        packageOperations.acceptAnOffer(offer5);
//        packageOperations.acceptAnOffer(offer6);
//        packageOperations.acceptAnOffer(offer7);
//        packageOperations.acceptAnOffer(offer8);
//        packageOperations.acceptAnOffer(offer9);
//        
//        if(packageOperations.getAllOffers().size()!=0) {
//        	System.out.println("Greska 10 ");
//        	return;
//        }
//        
//        for(int i = 0;i<9;++i) packageOperations.driveNextPackage("milovan");
//        
//        if(courierOperations.getCouriersWithStatus(0).size()!=2) {
//        	System.out.println("Greska 11 ");
//        	return;
//        }
//        if(userOperations.getSentPackages("petar")!=9) {
//        	System.out.println("Greska 12 ");
//        	return;
//        }
//        System.out.println(courierOperations.getAverageCourierProfit(0));
//        System.out.println("Petar je posalo " + userOperations.getSentPackages("petar") + " paketa");
//        //generalOperations.eraseAll();
//        
//        
//        System.out.println("USPESNO PROSAO TEST!");
    }
}
