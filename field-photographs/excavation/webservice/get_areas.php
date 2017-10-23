<?php
	header('Content-type: application/json');
	include('connect.php');

	$mode=$_REQUEST['mode']; // If user pass mode northing then area_northing data will be print else user have to pass area_northing
	$area_easting_name=$_REQUEST['area_easting_name'];
	if($mode =='area_northing' && $area_easting_name !=''){
		$select_easting_list="SELECT area_northing FROM sde.excavation_areas where area_easting=".$area_easting_name ."  AND status='active' ORDER BY area_northing";
		$select_easting_res=pg_query($select_easting_list);
		$count=0;
		while($row_data = pg_fetch_array($select_easting_res))
		{
			$count++;
			$result['responseData'][$count]=array('area_northing'=>$row_data['area_northing']);
		}
	}
	else if($mode =='area_easting'){
		$select_easting_list="SELECT DISTINCT area_easting FROM sde.excavation_areas WHERE status='active' ORDER BY area_easting";
		$select_easting_res=pg_query($select_easting_list);
		$count=0;
		while($row_data = pg_fetch_array($select_easting_res))
		{
			$count++;
			$result['responseData'][$count]=array('area_easting'=>$row_data['area_easting']);
		}
	}
	else {
		$error="please insert required field";
		$result['responseData']=array('result'=>'failed', 'message'=>$error);
	}
	$result=json_encode(array_unique($result));
	echo $result;
?>