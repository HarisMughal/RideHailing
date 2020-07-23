<?php
	require_once '../includes/DBOperations.php';
	$response = array();
	if($_SERVER['REQUEST_METHOD'] == 'POST'){
		if(
			isset($_POST['username']) and
			 isset($_POST['email']) and
			  isset($_POST['cnic'])and
			  isset($_POST['pkg'])and
			  isset($_POST['number'])and
			  isset($_POST['city'])and
			  isset($_POST['gender'])and isset($_POST['tabel']))
			  
			{
				$db = new DbOperations();
				$result= $db->submitProfile($_POST['username'] ,
			  $_POST['email'] ,
			  $_POST['cnic'],
			  $_POST['pkg'],
			  $_POST['number'],
			  $_POST['city'],
			  $_POST['gender'],$_POST['tabel']);
				if($result ==1){
					  $response['error']=false;
					  $response['response']="Data Submited sucessfully";
				}else if($result == 2){
					  $response['error']=true;
					  $response['response']="Something went wrong try again";
				}else if($result == 0){
					$response['error']=true;
					$response['response']="No such package exsist";
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
	