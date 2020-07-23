<?php
	require_once '../includes/DBOperations.php';
	$response = array();
	if($_SERVER['REQUEST_METHOD'] == 'POST'){
		if(isset($_POST['driverId']) and isset($_POST['latitude']) and isset($_POST['longitude']))
			  
		{
				$db = new DbOperations();
				$result= $db->updateLocation($_POST['driverId'],$_POST['latitude'],$_POST['longitude']);
				if($result ==1){
					  $response['error']=false;
					  $response['response']="Location Updated";
				}else if($result == 2){
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
