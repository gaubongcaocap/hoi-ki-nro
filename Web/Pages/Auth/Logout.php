<?php
session_start(); // Start the session

// Destroy all session variables
session_unset();
session_destroy();

// Redirect to the root directory
header("Location: /");
exit();