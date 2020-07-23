<?php
	require_once '../includes/DBOperations.php';

$response = array(); 

if($_SERVER['REQUEST_METHOD']=='POST')
{
	if( isset($_POST['id'])  )
	{
		
		$db = new DbOperations(); 
			$user = $db->getHistorey($_POST['id']);
			echo "Time ".$user['Time']."\n";
			echo "Duration ".$user['Duration'];
			echo "Date ".$user['Date'];
			$fare=$db->getFare($user['Fare_No']);
			echo "Fare ".$fare['Amount_after_promo_code'];

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