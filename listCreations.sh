#!/bin/bash













	
	numOfCreations=$(ls -1 creations | grep .mp4 | wc -l)
	
	if [[ $numOfCreations = 0 ]]
	then
		echo "(No creations currently exist)"
	else
	

		ls creations | grep .mp4 | sed -e 's/\.[^.]*$//' | cat - # sed processing removes file name extensions
	fi













