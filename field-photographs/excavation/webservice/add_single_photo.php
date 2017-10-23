<?php
	header('Content-type: application/json');
	include('connect.php');
	$area_easting=$_REQUEST['area_easting'];
	$area_northing=$_REQUEST['area_northing'];
	$image_path=basename($_FILES['image_path']['name']);
	//$context_number=$_REQUEST['context_number'];
	//$photograph_number=$_REQUEST['photograph_number'];
	$samba_share_path=GetValue('options.procedure_properties','property_value','property','base_image_path');
	$context_subpath=GetValue('options.procedure_properties','property_value','property','context_subpath');
	$upload_directory=$samba_share_path."/".$context_subpath;
	$top_path=$area_easting."/".$area_northing."/".date('Y-m-d')."/";
	$date_dir=$upload_directory."/".$top_path;

		if (!file_exists($date_dir)) {
			custom_mkdir($date_dir);
		}

	$images = glob($date_dir."*.jpg");
	$count=1;
	foreach($images as $image){ $image; $count++; }
	$fileName = basename($_FILES["image_path"]["name"]);
	$fileName=$count.".jpg";
	$target_path=$date_dir.$fileName;

	if($area_easting !='' && $area_northing !=''){
		if(!file_exists($date_dir)) {
			custom_mkdir($date_dir);
		}

		$end_result = move_uploaded_file($_FILES['image_path']['tmp_name'],$target_path);

		$result['responseData']=array('result'=>'success', 'photo_number'=>$count, 'image_path'=>$SITE_URL.$context_subpath.$top_path.$fileName);
		
	}else {
		$error="please insert required field";
		$result['responseData']=array('result'=>'failed', 'message'=>$error);		
	}
	$result=json_encode($result);
	echo $result;
?>