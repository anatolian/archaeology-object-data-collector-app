<?php
	//header('Content-type: application/json');

	include('connect.php');
	$area_easting=$_REQUEST['area_easting'];
	$area_northing=$_REQUEST['area_northing'];
	$image_path=basename($_FILES['image_path']['name']);

	$base_path=$_REQUEST['base_image_path'];
	$td_path=$_REQUEST['3d_subpath'];

  	$td_subpath=GetValue('options.procedure_properties','property_value','property','3d_subpath');



	$upload_directory=$base_path."/".$td_subpath;
	$area_easting_dir=$upload_directory."/".$area_easting;
	$area_northing_dir=$area_easting_dir."/".$area_northing;
	//echo date('Y-m-d H:i');
	$date=date('YmdHi');
	$date_dir=$area_northing_dir."/".$date."/";	
	$date_name=$date;

	if($area_easting !='' && $area_northing !=''){
		if (!file_exists($area_easting_dir)) {
			custom_mkdir($area_easting_dir);
		}
		if(!file_exists($area_northing_dir)) {
			custom_mkdir($area_northing_dir);
		}
		if(!file_exists($date_dir)) {
			custom_mkdir($date_dir);
		}

		$images = glob($date_dir."*.jpg");
		$count=1;
		foreach($images as $image) { $image; ++$count; }
		$fileName = basename($_FILES["image_path"]["name"]);
		$fileName=$count.".jpg";
		$target_path=$date_dir.$fileName;

		move_uploaded_file($_FILES['image_path']['tmp_name'],$target_path);			
		$result['responseData']=array('result'=>'success','date_name'=>$date_name);


	}else {
		$error="please insert required field";
		$result['responseData']=array('result'=>'failed', 'message'=>$error);		
	}
	$result=json_encode($result);
	echo $result;
?>