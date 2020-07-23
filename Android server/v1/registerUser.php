<?php
	require_once '../includes/DBOperations.php';
	$response = array();
	if($_SERVER['REQUEST_METHOD'] == 'POST'){
		if(
			isset($_POST['username']) and
			 isset($_POST['password']) and
			  isset($_POST['email']))
			  
			{
				$db = new DbOperations();
				$result= $db->createUser($_POST['username'],$_POST['password'],$_POST['email']);
				if($result ==1){
					  $response['error']=false;
					  $response['response']="Registered sucessfully";
				}else if($result == 2){
					  $response['error']=true;
					  $response['response']="Something went wrong try again";
				}else if($result == 0){
					 $response['error']=true;
					 $response['response']="User already regsitered please login";
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
	