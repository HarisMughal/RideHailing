package com.example.haris.mysqllogin;

/**
 * Created by Haris on 11/24/2018.
 */

public class Constants {
    private  static String URL = "http://192.168.1.2/Android/v1/";
    public  static String URL1 = URL+"registerUser.php";
    public   static String URL2  = URL+"loginUser.php";
    public   static String URLRequestRide  = URL+"setLocationUser.php";
    public   static String URLCancleRide  = URL+"deleteLocation.php";
    public   static String URLgetRequest = URL+"new.php";
    public   static String URLacceptRequest = URL+"acceptRequest.php";
    public   static String URLupdateLocation = URL+"updateDriver.php";
    public   static String URLfindDriver = URL+"checkDriver.php";
    public   static String URLgetProfile = URL+"getProfile.php";
    public   static String URLsubmitProfile = URL+"submitProfile.php";
    public   static String URLendRide = URL+"endRide.php";
    public   static String URLgetFare = URL+"getFare.php";
    public   static String URLpayment = "http://192.168.43.170/pproject/PaymentTransactions/authorize-credit-card.php";
    public   static String URLchcekPromo= URL+"checkPromo.php";

    public static void setURL(String ip){
        URL = "http://"+ip+"/Android/v1/";
          URL1 = URL+"registerUser.php";
          URL2  = URL+"loginUser.php";
          URLRequestRide  = URL+"setLocationUser.php";
          URLCancleRide  = URL+"deleteLocation.php";
          URLgetRequest = URL+"new.php";
          URLacceptRequest = URL+"acceptRequest.php";
          URLupdateLocation = URL+"updateDriver.php";
          URLfindDriver = URL+"checkDriver.php";
          URLgetProfile = URL+"getProfile.php";
          URLsubmitProfile = URL+"submitProfile.php";
          URLendRide = URL+"endRide.php";
          URLgetFare = URL+"getFare.php";
        URLpayment = "http://"+ip+"/pproject/PaymentTransactions/authorize-credit-card.php";
        URLchcekPromo= URL+"checkPromo.php";
    }

}
