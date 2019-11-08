#!/bin/bash

	if  ls creations | grep -qwi "$1.mp4"
	then
		echo "Exists";

	fi

