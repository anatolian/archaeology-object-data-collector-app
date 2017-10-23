<?php
	header('Content-type: application/json');
	include('connect.php');


	$mode=$_REQUEST['mode']; // If user pass mode northing then area_northing data will be print else user have to pass area_northing
	$area_east=$_REQUEST['area_east'];
	$area_north=$_REQUEST['area_north'];
	$context_number=$_REQUEST['context_number'];
	$photograph_number=$_REQUEST['photograph_number'];
	if($mode =='delete' && $area_east != '' && $area_north != '' && $context_number != '' & photograph_number != ''){
		$d_query="delete FROM excavation.contexts_spatial_photographs where area_easting='".$area_east."' and area_northing='".$area_north."' and  context_number='".$context_number."' and photograph_number='". $photograph_number."'";
		$d_res=pg_query($d_query);
		$result['responseData']=array('result'=>'success');
	}
	else {
		$error="please insert required field";
		$result['responseData']=array('result'=>'failed', 'message'=>$error);		
	}
	$result=json_encode(array_unique($result));
	echo $result;
?>