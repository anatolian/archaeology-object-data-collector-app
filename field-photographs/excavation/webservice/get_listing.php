<?php
	//header('Content-type: application/json');
	include('connect.php');
	$mode=$_REQUEST['mode']; // User can pass context, sample, material in the arguments.
	$listing_type=$_REQUEST['listing_type'];
	//$result='not found';
	$area_east=$_REQUEST['area_east'];
	$area_north=$_REQUEST['area_north'];
	$context_number=$_REQUEST['context_number'];
	$photograph_number=$_REQUEST['photograph_number'];
	$sample_number=$_REQUEST['sample_number'];
	if($listing_type=='photograph' && $mode =='list' && $listing_type !='' && $mode != ''){
		$select_listing_type='SELECT "File types" FROM options.finds_file_types;';
		$select_listing_type_res=pg_query($select_listing_type);
		//print_r($select_listing_type_res);
		$count=0;
		while($row_data = pg_fetch_array($select_listing_type_res))
		{
			$count++;
			//echo $row_data['File types'];
			//$result['responseData'][$count]=array('photograph_file_type'=>$row_data['file_types']);
			$result['responseData'][$count]=array('file_type'=>$row_data['File types']);
		}
	}
	if($listing_type=='context' && $mode =='list' && $listing_type !='' && $mode != '' && $area_east != '' && $area_north != '' ){
		$date=date('Y-m-d');
		$select_listing_type="select context_number  FROM excavation.contexts_spatial where area_easting=".$area_east." and area_northing=".$area_north." ORDER BY context_number";
		//select context_number from excavation.contexts_spatial_photographs  where area_easting='1' and area_northing='1'
		if($photograph_number != '')
		{
			$select_listing_type="SELECT context_number FROM excavation.contexts_spatial where area_easting=".$area_east." and area_northing=".$area_north." and context_number not in(select context_number from excavation.contexts_spatial_photographs where area_easting='".$area_east."' and area_northing='".$area_north."' and photograph_date='".$date."' and photograph_number='".$photograph_number."') ORDER BY context_number";
		}
		$select_listing_type_res=pg_query($select_listing_type);
		$count1=0;
		if(pg_num_rows($select_listing_type_res)>0){
		while($row_data = pg_fetch_array($select_listing_type_res))
		{
			$count1++;
			//echo $row_data['context_number'];
			$result['responseData'][$count1]=array('context_number'=>$row_data['context_number']);
		}
		}
		else
		{
			$result['responseData']="No record found";
		}
	}
	if($listing_type=='sample' && $mode =='list' && $area_east != '' && $area_north != '' && $context_number != ''){
		$select_listing_type_sample="select sample_number from samples.samples where area_easting=".$area_east." and area_northing=".$area_north." and context_number=".$context_number." ORDER by sample_number";
		$select_listing_type_sample_res=pg_query($select_listing_type_sample);
		$count=0;
		while($row_data_sample = pg_fetch_array($select_listing_type_sample_res))
		{
			$count++;
			$result['responseData'][$count]=array('sample_number'=>$row_data_sample['sample_number']);
		}
	}
	
	if($listing_type=='material' && $mode =='list' && $listing_type !='' && $mode != '' && $area_east != '' && $area_north != '' && $context_number != '' && sample_number !='' ){
		$select_listing_type_sample="SELECT material FROM samples.samples where area_easting=".$area_east." and area_northing=".$area_north." and context_number=".$context_number." and sample_number=".$sample_number;
		$select_listing_type_sample_res=pg_query($select_listing_type_sample);
		$count=0;
		while($row_data_sample = pg_fetch_array($select_listing_type_sample_res))
		{
			$count++;
			$result['responseData'][$count]=array('material'=>$row_data_sample['material']);
		}
	}
	$result=json_encode($result);
	echo $result;
?>