<?php
require_once '../includes/DBOperations.php';
	$response = array();
	if($_SERVER['REQUEST_METHOD'] == 'POST'){
		if(isset($_POST['userId']) and isset($_POST['driverId']))
			  
		{
				$db = new DbOperations();
				$result= $db->acceptRequest($_POST['userId'],$_POST['driverId']);
				if($result !=0){
					  $response['error']=false;
					  $response['id']=$result['max(rideId)'];
					  $response['response']="Ride Accepted";
				}else if($result == 0){
					  $response['error']=true;
					  $response['response']="Something went wrong try again";				  
			}
			else{
				$response['error']=true;
				$response['response']="Required fields are missing";
			}
		}
	}
	else{
		$response['error'] = true;
		$response['response']= "Invalid REQUEST";
	}
	echo json_encode($response);