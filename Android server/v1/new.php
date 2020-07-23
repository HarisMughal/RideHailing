<?php

	 define ('DB_NAME','vms');
	 define ('DB_USER','root');
	 define ('DB_PASSWORD','');
	 define ('DB_HOST','localhost');

$con = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);

if(mysqli_connect_errno()){
	die('donot connect to database'.mysqli_connect_errno());
}

$stmt = $con->prepare("SELECT id,lat,lng FROM request where driverid is null limit 10");
				$stmt->execute(); 
				
				 $stmt->bind_result($id,$lat,$lng);
				
				//$r = $stmt->get_result();

				$result = array();
				
				
				while($stmt->fetch())
				{
					$temp = array();
					$temp['id'] = $id;
					$temp['lat'] = $lat;
					$temp['lng'] = $lng;
					
					array_push($result,$temp);
					
				}
				/*while($row = ($r)){
					array_push($result,array(
						'id'=>$row['id'],
						'lat'=>$row['lat'],
						'lng'=>$row['lng']
					));
				}*/
				
				$response['response'] = $result;
				
				echo json_encode($response);
				
?>