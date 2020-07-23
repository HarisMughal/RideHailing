<?php
	require_once '../includes/DBOperations.php';

$response = array(); 

if($_SERVER['REQUEST_METHOD']=='POST'){
	if(isset($_POST['promo']))
	{
		
		$db = new DbOperations(); 

			
			if($db->checkPromo($_POST['promo'])){
				$response['error'] = false; 
				$response['promo'] = true;	
				$response['response'] = "Promo Entered sucessfully";	
			
			}else{
				$response['promo'] = false;	
				$response['error'] = false; 
				$response['response'] = "No such Promo exsist";	
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