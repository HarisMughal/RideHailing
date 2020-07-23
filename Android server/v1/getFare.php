<?php
	require_once '../includes/DBOperations.php';

$response = array(); 

if($_SERVER['REQUEST_METHOD']=='POST')
{
	if( isset($_POST['id']) and isset($_POST['promo']))
	{
		
		$db = new DbOperations();
		if($db->isRequest($_POST['id'])){
			$result = $db->computeFare($_POST['id'],$_POST['promo']);
			
			
			$response['error'] = false;
			$response['fare']= $result;
		}else{
			$response['error'] = true;
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