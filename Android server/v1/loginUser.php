<?php
	require_once '../includes/DBOperations.php';

$response = array(); 

if($_SERVER['REQUEST_METHOD']=='POST'){
	if(isset($_POST['email']) and isset($_POST['password'])and isset($_POST['tabel']))
	{
		
		$db = new DbOperations(); 

		if($db->userLogin($_POST['email'], $_POST['password'],$_POST['tabel'])){
			$user = $db->getUserByUsername($_POST['email'],$_POST['tabel']);
			$response['error'] = false; 
			if($_POST['tabel'] == 0){
				$response['id'] = $user['UID'];
			}else if($_POST['tabel'] == 1){
				$response['id'] = $user['DID'];
				
			}
			
			$response['email'] = $user['Email'];
			$response['username'] = $user['Name'];
			$response['response'] = "LogedIn Sucessfull";	
		}else{
			$response['error'] = true; 
			$response['response'] = "Invalid username or password";			
		}

	}else
	{
		$response['error'] = true; 
		$response['response'] = "Required fields are missing";
	}
}else{
		$response['error'] = true;
		$response['response']= "Invalid REQUEST";
	}

echo json_encode($response);