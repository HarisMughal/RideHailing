<?php
require_once '../includes/DBOperations.php';
	$response = array();
	if($_SERVER['REQUEST_METHOD'] == 'POST')
	{
		if(isset($_POST['userId']))
			  
		{
				$db = new DbOperations();
				$result= $db->checkDriver($_POST['userId']);
				if($result['driverid'] == NULL){
					  $response['book']=false;
					  $response['error'] = true;
				}else {
					$driver = $db->findDriver($result['driverid']);
						$response['book']=true;
						$response['lat']=$driver['lat'];
						$response['lng']=$driver['lng'];
						$response['error'] = false;
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