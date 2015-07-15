<?php
require "config.php";
header('Content-Type: application/json');
$url = "http://" . $_SERVER['HTTP_HOST'] . $_SERVER['REQUEST_URI'];
$url = substr($url, 0, strrpos($url, "/", 0)) . "/";

$authKey = null;
$error = null;
$values = array ();
$args = array (
		"key" => null,
		
		"serverTps" => null,
		"uniquePlayers" => null,
		"playerJoins" => null,
		"playersKilled" => null,
		"mobsKilled" => null,
		"chatMessages" => null,
		"blocksBroken" => null,
		"blocksPlaced" => null,
		"blocksTraveled" => null,
		"itemsDropped" => null,
		"inventoriesOpened" => null,
		"chunksLoaded" => null,
		"secondsOnline" => null,
		"statsCollected" => null 
);

$jsonResponse = array ();
$htmlResponse = null;

// POSTING DATA
if (!empty($_GET["key"])) {
	$authKey = $_GET["key"];
	addJson("query", getQueryInfo());
	if (file_exists("keys/" . $_GET["key"] . ".key")) {
		foreach (array_keys($args) as $key) {
			if (!empty($_GET[$key])) {
				$values[$key] = $_GET[$key];
			} else {
				addError("Required parameters not specified! Could not find: " . $key);
				break;
			}
		}
		if ($error == null) {
			foreach (array_keys($values) as $key) {
				if ($key != "key") {
					if ((int) $values[$key] != -1) {
						$handle = fopen("data/" . $key . ".txt", "a+");
						$value = (int) fgets($handle);
						fclose($handle);
						
						$handle = fopen("data/" . $key . ".txt", "w+");
						if ($key != "uniquePlayers" && $key != "serverTps") {
							$value = $value + (int) $values[$key];
						} else {
							if ($key == "serverTps" && (int) $values[$key] < 14 && (int) $value < 14) {
								if ($email != null) {
									mail($email, "MCAnalytics Server TPS Alert", "The tick rate on the server dropped below 14! It is currently at " . $values[$key]);
								}
							}
							$value = (int) $values[$key];
						}
						fwrite($handle, $value);
						fclose($handle);
					}
				}
			}
		}
	} else {
		addError("Access denied");
	}
}  // GETTING DATA
else if (!empty($_GET["color"])) {
	header('Content-Type: text/html');
	$color = $_GET["color"];
	$prettyDisplay = false;
	if (!empty($_GET["pretty"])) {
		$prettyDisplay = filter_var($_GET["pretty"], FILTER_VALIDATE_BOOLEAN);
		if ($prettyDisplay) {
			addHtml("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js\"></script>");
			addHtml("<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\">");
			addHtml("<font size=\"" . $font_size . "\"><a href=\"\" class=\"refresh\">Refresh</font>");
		}
	}
	if ($color != "null" && !$prettyDisplay) {
		addHtml("<font color=\"" . $color . "\">");
	}
	foreach (getStatistics() as $key => $value) {
		if ($key != "key") {
			$mask = "ABCDEFGHJIJKLMNOPQRSTUVWXYZ";
			$pos = strcspn($key, $mask);
			$firstWord = substr($key, 0, $pos);
			$secondWord = substr($key, $pos);
			if ($secondWord == "Tps") {
				$firstWord = "TPS";
				$secondWord = "";
			}
			if ($prettyDisplay) {
				addHtml("<font color=\"" . $color . "\" size=\"" . $font_size . "\"><a href=\"#\" class=\"value\"> " . number_format($value) . "</a><div class=\"statistic\">  " . ucfirst($firstWord) . " " . $secondWord . "</div></font><br>");
			} else {
				addHtml(ucfirst($firstWord) . " " . $secondWord . ": " . number_format($value) . "<br>");
			}
		}
	}
} else {
	addError("No valid parameters specified");
}

// PRINT DATA
if ($htmlResponse == null) {
	if ($error == null) {
		$jsonResponse = array (
				'query' => getQueryInfo(),
				'statistics' => getStatistics() 
		);
	} else {
		$jsonResponse = array (
				'query' => getQueryInfo() 
		);
	}
	echo json_encode($jsonResponse, JSON_PRETTY_PRINT);
} else {
	echo $htmlResponse;
}

// FUNCTIONS
function getQueryInfo() {
	global $authKey, $url, $error;
	return array (
			"key" => $authKey,
			"url" => $url,
			"status" => get_headers($url)[0],
			"error" => $error 
	);
}

function getStatistics() {
	global $values;
	global $args;
	$statistics = array ();
	foreach (array_keys($args) as $key) {
		if ($key != "key") {
			$handle = fopen("data/" . $key . ".txt", "r");
			$statistics[$key] = (int) fgets($handle);
			fclose($handle);
		}
	}
	return $statistics;
}

function addError($err) {
	global $error;
	if (!empty($error)) {
		$error = $error . ", " . $err;
	} else {
		$error = $err;
	}
}

function addJson($key, $array) {
	global $jsonResponse;
	array_push($jsonResponse, array (
			$key => $array 
	));
}

function addHtml($html) {
	global $htmlResponse;
	$htmlResponse = $htmlResponse . $html;
}
?>