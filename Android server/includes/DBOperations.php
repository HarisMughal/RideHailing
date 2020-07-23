<?php
	class DbOperations{
		private $con;
		
		function __construct(){
			require_once dirname(__FILE__).'/DBConnect.php';
			$db = new DbConnect();
			$this->con = $db->connect();
		}
		public function createUser($username , $pass, $email){
			if($this->isUserExist($email)){
				return 0;
			}
			else{
				$password = md5($pass);
				$stmt = $this->con->prepare("INSERT INTO `users`(`UID`, `Name`, `Mobile_No`, `Email`, `Password`, `CNIC`, `Package_No`, `City`, `Gender`) VALUES (NULL, ?,NULL, ?, ?,NULL,NULL,NULL,NULL);");
				$stmt->bind_param("sss",$username,$email,$password);
				if($stmt->execute()){
					return 1;
				}else{
					return 2;
				}
			}
		}
		private function isUserExist($email){
			$stmt = $this->con->prepare("SELECT UID FROM users WHERE Email=?");
			$stmt->bind_param("s",$email);
			$stmt->execute(); 
			$stmt->store_result(); 
			return $stmt->num_rows > 0; 
		}
		public function userLogin($email, $pass, $table) {
			if($table == 0){
				$password = md5($pass); 
				$stmt = $this->con->prepare("SELECT UID FROM users WHERE Email=? AND Password=?");
				$stmt->bind_param("ss",$email,$password);
			}else if($table == 1){
				$stmt = $this->con->prepare("SELECT DID FROM driver WHERE Email=? AND Password=?");
				$stmt->bind_param("ss",$email,$pass);
			}
			$stmt->execute();
			$stmt->store_result(); 
			return $stmt->num_rows > 0; 
		}

		public function getUserByUsername($email,$table){
			if($table == 0){
				$stmt = $this->con->prepare("SELECT * FROM users WHERE Email = ?");
				$stmt->bind_param("s",$email);
			}else if($table == 1){
				$stmt = $this->con->prepare("SELECT * FROM driver WHERE Email = ?");
				$stmt->bind_param("s",$email);
			}
			$stmt->execute();
			return $stmt->get_result()->fetch_assoc();
		}
		public function storeLocation($id ,$lat , $lng){
			$stmt = $this->con->prepare("INSERT INTO `request` (`id`, `lat`, `lng`,	driverid)	VALUES (?, ?, ?,NULL);");
				$stmt->bind_param("idd",$id ,$lat , $lng);
				if($stmt->execute()){
					return 1;
				}else{
					return 2;
				}
		}
		public function deleteLocation($id){
			if($this->isLocationExist($id)){
			$stmt = $this->con->prepare("DELETE FROM `request` WHERE `id` =? and driverid is null;");
				$stmt->bind_param("i",$id);
				if($stmt->execute()){
					return 1;
				}else{
					return 2;
				}
			}else{
				return 3;
			}
		}
		private function isLocationExist($id){
			$stmt = $this->con->prepare("SELECT * FROM request WHERE id=?");
			$stmt->bind_param("i",$id);
			$stmt->execute(); 
			$stmt->store_result(); 
			return $stmt->num_rows > 0; 
		}
		
/*		public function getUserLocations(){
				$stmt = $this->con->prepare("SELECT id,lat,lng FROM request where driverid = null limit 10");

				$r = $stmt->get_result();

				$result = array();

				while($row = mysqli_fetch_array($r)){
					array_push($result,array(
						'id'=>$row['id'],
						'lat'=>$row['lat'],
						'lng'=>$row['lng']
					));
				}

				echo json_encode(array('result'=>$result));

		}
		*/
		public function acceptRequest($userId,$driverId){
			$stmt = $this->con->prepare("UPDATE `request` SET `driverid`=? WHERE id = ?");
				$stmt->bind_param("ii",$driverId,$userId);
				if($stmt->execute()){
					return $this->rideTriger($userId,$driverId);
					
				}else{
					return 0;
				}
		}
		
		public function rideTriger($userId,$driverId){
			
			$stmt = $this->con->prepare("INSERT INTO `ride` (`User_ID`, `Driver_ID`, `Date`, `Time`) 
			VALUES (?, ?,CURDATE(),NOW());");
			$stmt->bind_param("ii",$userId ,$driverId);
			$stmt->execute();
			$stmt = $this->con->prepare("SELECT max(rideId) FROM ride");
			$stmt->execute();
			return $stmt->get_result()->fetch_assoc();
			
		}
		
		
		public function updateLocation($driverId ,$latittude , $longitude){
			$stmt = $this->con->prepare("UPDATE `driver` SET `lat`=? , lng=? WHERE DID = ?");
				$stmt->bind_param("ddi",$latittude , $longitude,$driverId);
				if($stmt->execute()){
					return 1;
				}else{
					return 2;
				}
		}
		
		public function checkDriver($userId){
			$stmt = $this->con->prepare("SELECT driverid from request WHERE id = ?");
				$stmt->bind_param("i",$userId);
				$stmt->execute();
				return $stmt->get_result()->fetch_assoc();
		}
		public function findDriver($driverId){
			$stmt = $this->con->prepare("SELECT lat,lng from driver WHERE DID = ?");
				$stmt->bind_param("i",$driverId);
				$stmt->execute();
				return $stmt->get_result()->fetch_assoc();
		}
		
		public function submitProfile($username ,$email,
			  $cnic,
			  $pkg,
			  $number,
			  $city,
			  $gender){
				  
			if($this->checkPackage($pkg)){
			$stmt = $this->con->prepare("UPDATE  `users` set `Name`=?, `Mobile_No`=?, `CNIC`=?, `Package_No`=?, `City`=?, `Gender`=? where `Email` =?");
				$stmt->bind_param("sssisss",$username,$number,$cnic,$pkg,$city,$gender,$email);
				if($stmt->execute()){
					return 1;
				}else{
					return 2;
				}
			}else{
				return 0;
			}
		}
		private function checkPackage($pkg){
			$stmt = $this->con->prepare("SELECT * FROM package WHERE Package_No=?");
			$stmt->bind_param("i",$pkg);
			$stmt->execute(); 
			$stmt->store_result(); 
			return $stmt->num_rows > 0; 
		}
		public function deleteRequest($id){
			$stmt = $this->con->prepare("DELETE FROM `request` WHERE `driverid` =? ;");
				$stmt->bind_param("i",$id);
				if($stmt->execute()){
					return 1;
				}else{
					return 2;
				}

		}
		
		public function endRide($id){
			$stmt = $this->con->prepare("UPDATE `ride` SET `Duration`=TIMESTAMPDIFF(second,Time,CURRENT_TIME())/60 where rideId = ? ");
				$stmt->bind_param("i",$id);
				if($stmt->execute()){
					return 1;
				}else{
					return 2;
				}
			
		}
		public function fareTriger($userId,$promo){
			$stmt = $this->con->prepare("INSERT INTO `fare`(`Package_No`)
			SELECT Package_No from users where UID=?");
			$stmt->bind_param("i",$userId );
			$stmt->execute();
			$stmt = $this->con->prepare("SELECT max(Fare_No) FROM fare");
			$stmt->execute();			
			return $stmt->get_result()->fetch_assoc();
		}
		public function computeFare($userId,$promo){
			$result = $this->fareTriger($userId,$promo);
			$stmt = $this->con->prepare("UPDATE `fare` SET `Promo_No`=? where Fare_No = ? ");
			$stmt->bind_param("ii",$promo,$result['max(Fare_No)']);
			$stmt->execute();
			$stmt = $this->con->prepare("UPDATE `ride` SET `Fare_No`= ? where User_ID = ? and Fare_No is NULL ");
			$stmt->bind_param("ii",$result['max(Fare_No)'],$userId);
			$stmt->execute();
			$stmt = $this->con->prepare("SELECT `Duration` from ride where Fare_No = ?");
			$stmt->bind_param("i",$result['max(Fare_No)']);
			$stmt->execute();			
			$duration = $stmt->get_result()->fetch_assoc();
			
			$stmt = $this->con->prepare("SELECT `Discount` from promocode where Promo_No = ?");
			$stmt->bind_param("i",$promo);
			$stmt->execute();			
			$promoDiscount = $stmt->get_result()->fetch_assoc();
			
			$durationFinal= $duration['Duration']*10 - $promoDiscount['Discount'];
			$stmt = $this->con->prepare("UPDATE `fare` SET `Amount_after_promo_code`=?,`Amount_before_promo_code`=? where Fare_No = ? ");
			$initial = $duration['Duration']*10;
			$stmt->bind_param("iii",$durationFinal,$initial,$result['max(Fare_No)']);
			$stmt->execute();
			return $durationFinal;
		}
			public function isRequest($id){
			$stmt = $this->con->prepare("SELECT * FROM request WHERE id=?");
			$stmt->bind_param("i",$id);
			$stmt->execute(); 
			$stmt->store_result(); 
			return $stmt->num_rows == 0; 
		}
		
		public function getHistorey($id){
			$stmt = $this->con->prepare("SELECT Duration,Date,Time,Fare_No FROM ride WHERE User_ID=?");
			$stmt->bind_param("i",$id);
			$stmt->execute(); 
			return $stmt->get_result()->fetch_assoc();
		}
		public function getFare($id){
			$stmt = $this->con->prepare("SELECT Amount_after_promo_code from fare  WHERE Fare_No=?");
			$stmt->bind_param("i",$id);
			$stmt->execute(); 
			return $stmt->get_result()->fetch_assoc();
		}
		
		public function checkPromo($promoNumber){
			$stmt = $this->con->prepare("SELECT Promo_No FROM promocode WHERE Promo_No=?");
			$stmt->bind_param("i",$promoNumber);
			$stmt->execute(); 
			$stmt->store_result(); 
			return $stmt->num_rows > 0; 
		}
		
		

		
	}