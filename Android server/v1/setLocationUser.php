<?php
	require_once '../includes/DBOperations.php';
	$response = array();
	if($_SERVER['REQUEST_METHOD'] == 'POST'){
		if(
			isset($_POST['id']) and
			 isset($_POST['lat']) and
			  isset($_POST['lng']))
			  {
				$db = new DbOperations();
				$result= $db->storeLocation($_POST['id'],$_POST['lat'],$_POST['lng']);
				if($result ==1){
					  $response['error']=false;
					  $response['response']="Request sucessfully";
				}else if($result == 2){
					  $response['error']=true;
					  $response['response']="Something went wrong try again";
				}
				  
			}
			else{
				$response['error']=true;
				$response['response']="Required fields are missing";
			}
	}
	else{
		$response['error'] = true;
		$response['response']= "Invalid REQUEST";
	}
	echo json_encode($response);
	