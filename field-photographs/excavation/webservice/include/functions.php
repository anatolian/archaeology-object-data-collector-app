<?php

	function GetValue($table,$field,$where,$condition)
	{
		$qry="SELECT $field from $table where $where='".$condition."'";
		$res=pg_query($qry);
		$row1=pg_fetch_array($res);
		$count=count($row1);
		if($count>0)
		{
			$row1[$field];
			return $row1[$field];
		}
		else
		{
			return "";
		}
	}

	function custom_mkdir($directory){
		mkdir($directory, 0777, true);
	}
?>