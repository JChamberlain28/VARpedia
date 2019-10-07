



# $1 = noLine
# $2 = tempDir





		
		sed -n "1,${1}p" $2/description.txt > $2/descriptionSnip.txt
		cat $2/descriptionSnip.txt | text2wave -o $2/audio.mp3 &> /dev/null
        	echo $(soxi -D $2/audio.mp3)




 








