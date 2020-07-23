<?php 

$sql = "SELECT id,lat,lng FROM request where driverid =0 limit 10";

require_once '../includes/DBOperations.php';

$r = mysqli_query($con,$sql);

$result = array();

while($row = mysqli_fetch_array($r)){
    array_push($result,array(
	'id'=>$row['id'],
	'lat'=>$row['lat'],
	'lng'=>$row['lng']
    ));
}

echo json_encode(array('result'=>$result));

mysqli_close($con);