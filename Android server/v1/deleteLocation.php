<?php
	require_once '../includes/DBOperations.php';
	$response = array();
	if($_SERVER['REQUEST_METHOD'] == 'POST'){
		if(isset($_POST['id'])){
				$db = new DbOperations();
				$result= $db->deleteLocation($_POST['id']);
				if($result ==1){
					  $response['error']=false;
					  $response['response']="Canceled sucessfully";
				}else if($result == 2){
					  $response['error']=true;
					  $response['response']="Something went wrong try again";
				}
				else if($result == 3){
				  $response['error']=true;
				  $response['response']="No such request";
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