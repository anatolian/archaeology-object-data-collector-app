<?php
	error_reporting(0);
	header('Content-type: application/json');
	include('connect.php');

	$area_easting=$_REQUEST['area_easting'];
	$area_northing=$_REQUEST['area_northing'];
	$context_number=$_REQUEST['context_number'];
	$sample_number=$_REQUEST['sample_number'];
	$sample_photo_type=$_REQUEST['sample_photo_type'];


	$sample_path=$_REQUEST['sample_subpath'];
	$base_path=$_REQUEST['base_image_path'];

	$local_path=$sample_path."/".$area_easting."/".$area_northing."/".$context_number."/";
	$upload_directory=$base_path.$local_path;
chdir($upload_directory);

	if($area_easting != '' && $area_northing != '' && $context_number!= '' && $sample_number!='' && $sample_photo_type != '')
	{
		//echo $upload_directory;
			
		    $images=glob($sample_number."_".$sample_photo_type."_[0-9]*",GLOB_NOSORT);

			$imageArray=array();
			//print_r($images);
			foreach($images as $image){
					$imageExplode1 =explode(".",$image);	
					$imageExplode2 = explode("_",$imageExplode1[0]);
					$imageNumber = intval($imageExplode2[2]);
					if (strval($imageNumber) == $imageExplode2[2])
					{

						$imageArray[$imageNumber]=$image;
					}
				}
			ksort($imageArray);
			$count=0;
			foreach($imageArray as $image) {
				$count++;
				$image_size=getimagesize($image);
				$image_width=$image_size[0];
				$image_height=$image_size[1];
			//echo $image;

		$result['responseData'][$count]=array("image_path"=>$SITE_URL.$local_path.$image,'area_easting'=>$area_easting,'area_northing'=>$area_northing, 'context_number'=>$context_number, 'sample_number'=>$sample_number,'image_width'=>$image_width,'image_height'=>$image_height);			
		}
	}
	else
	{
		$result['responseData']=array('result'=>'failed', 'message'=>'not data found');
	}	
$result=json_encode($result);
echo $result;
?>
