<?php
	session_start();
	include ("include/config.inc.php");
	include ("include/error.php");
	include ("include/functions.php");
	$ADMIN_MOUSEHOUR_COLOUR="#cccccc";
    $ADMIN_MOUSEOUT_COLOUR="#FFFFFF";
    $ADMIN_TOP_BGCOLOUR="#FFFFFF"; 
	$link = pg_connect($conn_string) or die(pg_last_error());   
?>
