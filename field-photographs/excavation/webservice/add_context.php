<?php
	header('Content-type: application/json');
	include('connect.php');

	$area_easting=$_REQUEST['area_easting'];
	$area_northing=$_REQUEST['area_northing'];
	$context_number=$_REQUEST['context_number'];
	$photograph_number=$_REQUEST['photograph_number'];
	$date=date('Y-m-d');
	if($area_easting != '' && $area_northing != '' && $photograph_number != '' && $context_number != '' )
	{
		$query="INSERT INTO excavation.contexts_spatial_photographs(area_easting, area_northing, photograph_date, photograph_number, context_number)
    VALUES ('".$area_easting."','".$area_northing."','".$date."','".$photograph_number."','".$context_number."')";
			
		$result['responseData']=array('result'=>'success');
		pg_query($query);
		
	}else {
		$error="please insert required field";
		$result['responseData']=array('result'=>'failed', 'message'=>$error);		
	}
	$result=json_encode($result);
	echo $result;
?>