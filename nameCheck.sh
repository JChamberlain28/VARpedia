	

	
	if [[ $1 = *"/"* ]] || [[ $1 = *"\\"* ]] || [[ $1 = *"."* ]] || [[ $1 = "" ]] || [[ -z "${1// }" ]] # prevents names that will lead to hidden file creation
	then
		echo "Invalid Name"

	elif  ls creations | grep -qwi "$1.mp4"
	then
		echo "Exists";

	fi

