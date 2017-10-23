<?php
	header('Content-type: application/json');
	include('connect.php');
	$font_path = "times.ttf";
	$valid_formats = array("jpg",  "bmp","jpeg");
	$font_size = $_REQUEST['sample_label_font_size'];
	$area_easting=$_REQUEST['area_easting'];
	$area_northing=$_REQUEST['area_northing'];
	$context_number=$_REQUEST['context_number'];
	$sample_number=$_REQUEST['sample_number'];
	$sample_photo_type=$_REQUEST['sample_photo_type'];
	$label_placement=$_REQUEST['sample_label_placement'];
	$sample_label_area_divider=$_REQUEST['sample_label_area_divider'];
	$sample_label_context_divider=$_REQUEST['sample_label_context_divider'];
	$sample_label_sample_divider=$_REQUEST['sample_label_sample_divider'];
	$image_path=basename($_FILES['image_path']['name']);
	$water_mark_text_2=$area_easting.$sample_label_area_divider. $area_northing.$sample_label_context_divider. $context_number .$sample_label_sample_divider. $sample_number;
	

	$base_path=$_REQUEST['base_image_path'];
	$sample_path=$base_path."/".$_REQUEST['sample_subpath'];

	
	$area_easting_dir=$sample_path."/".$area_easting;

	$area_northing_dir=$area_easting_dir."/".$area_northing;
	
    	$context_number_dir=$area_northing_dir."/".$context_number."/";
	
	$sample_number_name=$sample_number."_".$sample_photo_type."_";
		
	$fileName = basename($_FILES["image_path"]["name"]);

	if($area_easting !='' && $area_northing !='' && $context_number != '' && $sample_number !='' && $sample_photo_type != ''){
		if(!file_exists($sample_path)) {
			
			custom_mkdir($sample_path);
		}
		if (!file_exists($area_easting_dir)) {
			
	
			custom_mkdir($area_easting_dir);
		}
		if(!file_exists($area_northing_dir)) {
			custom_mkdir($area_northing_dir);
		}
		if(!file_exists($context_number_dir)) {
			custom_mkdir($context_number_dir);
		}
		
chdir($context_number_dir);
		$images=glob($sample_number_name."*");
		$count=1;
		$target_path=$sample_number."_".$sample_photo_type."_".$count.".jpg";

		foreach($images as $image){ 

		if(in_array($target_path,$images)){
			//echo "hello";
			$count++;
			$target_path=$sample_number."_".$sample_photo_type."_".$count.".jpg";
		
		}else{ 
			break;
		}
		}
		$target_path=$context_number_dir.$target_path;
		$type_space = imagettfbbox($font_size, 0, $font_path, $water_mark_text_2);
		$image_width = abs($type_space[4] - $type_space[0]);
		$image_height = abs($type_space[5] - $type_space[1]);
		
	
		move_uploaded_file($_FILES['image_path']['tmp_name'],$target_path."-tmp");		
		watermark_text($target_path."-tmp", $target_path);
        //       echo  $demo_image = $new_name;
		//move_uploaded_file($_FILES['image_path']['tmp_name'],$target_path);			
		$result['responseData']=array('result'=>'success');
	}else {
		$error="please insert required field";
		$result['responseData']=array('result'=>'failed', 'message'=>$error);		
	}
	function watermark_text($oldimage_name, $new_image_name){
	//echo $oldimage_name;
	//echo "<br>";
	//echo $new_image_name;
    global $font_path, $font_size, $water_mark_text_2,$label_placement,$image_width,$image_height,$text_height,$text_width,$main_height,$main_width,$posx_text,$posy_text,$posx_image,$posy_image,$image_main;
   list($owidth,$oheight) = getimagesize($oldimage_name);
  
    $width = $owidth;
	$height = $oheight;  
	//$font_size=25; 
	//$label_placement="right-bottom";
	if($label_placement=="top-left")
	{
	$text_width=$width;
	$text_height=$image_height+10;	
	$posx=10; 
	$posy=$image_height+5;
	$angel=0;
	$posx_text=0;
	$posy_text=0;
	$posx_image=0;
	$posy_image=$text_height;
	$image_main = imagecreatetruecolor($width,$text_height+$height);
	
	
	}
	elseif($label_placement=="top-center")
	{
		$text_width=$width;
	    $text_height=$image_height+10;
		$posx=($width/2)-abs($image_width/2); 
		$posy=$image_height+5;
		$angel=0;
		$posx_text=0;
		$posy_text=0;
		$posx_image=0;
		$posy_image=$text_height;
		//$image_main = imagecreate($width,$text_height+$height);
		$image_main = imagecreatetruecolor($width,$text_height+$height);
	}
	elseif($label_placement=="top-right")
	{
		$text_width=$width;
	    $text_height=$image_height+10;
		$posx=$width-$image_width-10; 
		$posy=$image_height+5;
		$angel=0;
		$posx_text=0;
		$posy_text=0;
		$posx_image=0;
		$posy_image=$text_height;
		$image_main = imagecreatetruecolor($width,$text_height+$height);
	}
	elseif($label_placement=="bottom-left")
	{
		$text_width=$width;
	    $text_height=$image_height+10;
		$posx=10; 
		$posy=$image_height+5;
		$angel=0;
		$posx_text=0;
		$posy_text=$height;
		$posx_image=0;
		$posy_image=0;
		$image_main = imagecreatetruecolor($width,$text_height+$height);
	}
	elseif($label_placement=="bottom-center")
	{
		$text_width=$width;
	    $text_height=$image_height+10;
		$posx=($width/2)-abs($image_width/2);
		$posy=$image_height+5;
		$angel=0;
		$posx_text=0;
		$posy_text=$height;
		$posx_image=0;
		$posy_image=0;
		$image_main = imagecreatetruecolor($width,$text_height+$height);
	}
	elseif($label_placement=="bottom-right")
	{
		$text_width=$width;
	    $text_height=$image_height+10;
		$posx=$width-$image_width-10; 
		$posy=$image_height+5;
		$angel=0;
		$posx_text=0;
		$posy_text=$height;
		$posx_image=0;
		$posy_image=0;
		$image_main = imagecreatetruecolor($width,$text_height+$height);
	}
	elseif($label_placement=="left-top")
	{
		$text_width=$image_height+10;
	    $text_height=$height;
		$posx=$image_height+5; 
		$posy=$image_width+10;
		$angel=90;
		$posx_text=0;
		$posy_text=0;
		$posx_image=$text_width;
		$posy_image=0;
		$image_main = imagecreatetruecolor($width+$text_width,$height);
	}
	elseif($label_placement=="left-center")
	{
		$text_width=$image_height+10;
	    $text_height=$height;
		$posx=$image_height+5; 
		$posy=($height/2)+abs($image_width/2);
		$angel=90;
		$posx_text=0;
		$posy_text=0;
		$posx_image=$text_width;
		$posy_image=0;
		$image_main = imagecreatetruecolor($width+$text_width,$height);
	}
	elseif($label_placement=="left-bottom")
	{
		$text_width=$image_height+10;
	    $text_height=$height;
		$posx=$image_height+5; 
		$posy=$height-10;
		$angel=90;
		$posx_text=0;
		$posy_text=0;
		$posx_image=$text_width;
		$posy_image=0;
		$image_main = imagecreatetruecolor($width+$text_width,$height);
	}
	elseif($label_placement=="right-top")
	{
		$text_width=$image_height+10;
	    $text_height=$height;
		$posx=$image_height+5; 
		$posy=$image_width+10;
		$angel=90;
		$posx_text=$width;
		$posy_text=0;
		$posx_image=0;
		$posy_image=0;
		$image_main = imagecreatetruecolor($width+$text_width,$height);
	}
	elseif($label_placement=="right-center")
	{
		$text_width=$image_height+10;
	    $text_height=$height;
		$posx=$image_height+5; 
		$posy=($height/2)+abs($image_width/2);
		$angel=90;
		$posx_text=$width;
		$posy_text=0;
		$posx_image=0;
		$posy_image=0;
		$image_main = imagecreatetruecolor($width+$text_width,$height);
	}
	elseif($label_placement=="right-bottom")
	{
		$text_width=$image_height+10;
	    $text_height=$height;
		$posx=$image_height+5; 
		$posy=$height-10;
		$angel=90;
		$posx_text=$width;
		$posy_text=0;
		$posx_image=0;
		$posy_image=0;
		$image_main = imagecreatetruecolor($width+$text_width,$height);
	}
	else
	{
	$text_width=$width;
	$text_height=$image_height+10;	
	$posx=10; 
	$posy=$image_height+5;
	$angel=0;
	$posx_text=0;
	$posy_text=0;
	$posx_image=0;
	$posy_image=$text_height;
	$image_main = imagecreatetruecolor($width,$text_height+$height);
	}
	
    $image = imagecreatetruecolor($width, $height);
	$info   = getimagesize($oldimage_name);
	$type   = $info['mime'];
	
	if($type=='image/png')
	{
		$image_src = imagecreatefrompng($oldimage_name);
	}
	else
	{
    $image_src = imagecreatefromjpeg($oldimage_name);
	}
	$image1 = imagecreate($text_width,$text_height);
	$black1 = imagecolorallocate($image1, 255, 255, 255);
    $oranage1 = imagecolorallocate($image1, 0, 0, 0);
	 imagettftext($image1, $font_size, $angel, $posx,$posy, $oranage1, $font_path, $water_mark_text_2);
	
	//imagejpeg($image1, 'aaa1.jpg', 100);
    //imagedestroy($image1);
	
	//$image_src = imagecreatefromjpeg($oldimage_name);
   // imagecopyresampled($image, $image_src, 0, 0, 0, 0, $width, $height, $owidth, $oheight);
	imagecopymerge($image_main, $image1, $posx_text, $posy_text, 0, 0,$text_width,$text_height, 100);
	imagecopymerge($image_main, $image_src, $posx_image, $posy_image, 0, 0, $width, $height, 100);
	// imagejpeg($image_main, 'aaa11.jpg', 100);
	 
	//imagedestroy($image_main);
   	//$black = imagecolorallocate($image, 0, 0, 0);
    //$oranage = imagecolorallocate($image, 255, 102, 0);
	//echo $water_mark_text_2;
   // imagettftext($image, $font_size, 0, 30, 190, $black, $font_path, $water_mark_text_1);
   //imagettftext($image, $font_size, $angel, $posx , $posy, $oranage, $font_path, $water_mark_text_2);
    imagejpeg($image_main, $new_image_name, 100);
	
	//imagejpeg($image_main, 'aaa1.jpg', 100);
	imagedestroy($image_src);
	imagedestroy($image1);
    imagedestroy($image_main);
    unlink($oldimage_name);
    return true;
}
	$result=json_encode($result);
	echo $result;
?>