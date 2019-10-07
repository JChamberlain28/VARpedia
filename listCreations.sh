#!/bin/bash













	
	numOfCreations=$(ls -1 creations | wc -l)
	
	if [[ $numOfCreations = 0 ]]
	then
		echo "(No creations currently exist)"
	else
	

		ls creations | sed -e 's/\.[^.]*$//' | cat - # sed processing removes file name extensions
	fi













