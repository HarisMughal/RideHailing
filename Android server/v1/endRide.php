<?php
	require_once '../includes/DBOperations.php';

$response = array(); 

if($_SERVER['REQUEST_METHOD']=='POST')
{
	if( isset($_POST['id']) and  isset($_POST['driverid']))
	{
		
		$db = new DbOperations();
			$result=$db->endRide($_POST['id']);
			
			$result=$db->deleteRequest($_POST['driverid']);
			if($result == 1){
				$response['error'] = false;
				$response['response']= "Ride Ended";
			}
			
		

	}else
	{
		$response['error'] = true; 
		$response['response'] = "Required fields are missing";
	}
}else
{
		$response['error'] = true;
		$response['response']= "Invalid REQUEST";
}

echo json_encode($response);