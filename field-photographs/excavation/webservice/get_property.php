<?php
	error_reporting(0);
	header('Content-type: application/json');
	include('connect.php');

	
		$select_property="SELECT * FROM options.procedure_properties";
		$select_property_res=pg_query($select_property);
		$num= pg_num_rows($select_property_res);
		 if($num>0)
		 {
		 		$count=0;
		while($row_data = pg_fetch_array($select_property_res))
		{
			$count++;
			
			$result['responseData'][$count]=array($row_data['property']=>$row_data['property_value']);
			
		}
		}
		else
		{
		$result['responseData']=array('result'=>'failed', 'message'=>'not data found');
		}
		$result=json_encode($result);
	echo $result;
?>