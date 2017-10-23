<?php
	header('Content-type: application/json');
	include('connect.php');

	$mode=$_REQUEST['mode']; // If user pass mode northing then area_northing data will be print else user have to pass area_northing
	$area_east=$_REQUEST['area_east'];
	$area_north=$_REQUEST['area_north'];
	$context_number=$_REQUEST['context_number'];
	$photograph_number=$_REQUEST['photograph_number'];
	$samba_share_path=GetValue('options.procedure_properties','property_value','property','base_image_path');
	$context_subpath=GetValue('options.procedure_properties','property_value','property','context_subpath');
	$upload_directory=$samba_share_path."/".$context_subpath;
	$area_easting_dir=$upload_directory."/".$area_east;
	$area_northing_dir=$area_easting_dir."/".$area_north;
	$date=date('Y-m-d');
	$date_dir=$area_northing_dir."/".$date."/";	
	$images = glob($date_dir."*.jpg");
	$count=1;
	foreach($images as $image){ $image; $count++; }
	$fileName = basename($_FILES["image_path"]["name"]);
	$fileName=$photograph_number.".jpg";
	$target_path=$date_dir.$fileName;
	if($area_east !='' && $area_north !='' && $photograph_number != ''){
		if (!file_exists($area_easting_dir)) {
			custom_mkdir($area_easting_dir);
		}
		if(!file_exists($area_northing_dir)) {
			custom_mkdir($area_northing_dir);
		}
		if(!file_exists($date_dir)) {
			custom_mkdir($date_dir);
		}
			if(photo_number != ''){
			//echo $target_path;
			//echo $_FILES['image_path']['name'];
			move_uploaded_file($_FILES['image_path']['tmp_name'],$target_path);
 /*
		echo $d_query="update FROM excavation.contexts_spatial_photographs set  area_easting='".$area_east."', area_northing='".$area_north."'  where area_easting='".$area_east."' and area_northing='".$area_north."',  photograph_number='". $photograph_number."' and  context_number='".$context_number."' and photograph_number='". $photograph_number."'"; */
		$d_res=pg_query($d_query);
				$result['responseData']=array('result'=>'success');
			}
	}
	else {
		$error="please insert required field";
		$result['responseData']=array('result'=>'failed', 'message'=>$error);		
	}
	$result=json_encode(array_unique($result));
	echo $result;
?>