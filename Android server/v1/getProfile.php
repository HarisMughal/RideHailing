<?php
	require_once '../includes/DBOperations.php';

$response = array(); 

if($_SERVER['REQUEST_METHOD']=='POST')
{
	if( isset($_POST['email'])and isset($_POST['tabel']))
	{
		
		$db = new DbOperations(); 
			$user = $db->getUserByUsername($_POST['email'],$_POST['tabel']);
			$response['error'] = false; 
			if($_POST['tabel'] == 0)
			{
				$response['id'] = $user['UID'];
				$response['Package_No'] = $user['Package_No'];
			}else if($_POST['tabel'] == 1)
			{
				$response['id'] = $user['DID'];
				
			}
			
			$response['email'] = $user['Email'];
			$response['username'] = $user['Name'];
			$response['cnic'] = $user['CNIC'];
			$response['city'] = $user['City'];
			$response['Gender'] = $user['Gender'];
			$response['number'] = $user['Mobile_No'];
		

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