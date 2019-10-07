		


# $1 = name
# $2 = tempDir
# $3 = searchTerm
# $4 = vidTime


# creates a video with blue background and resolution 480x360
		# video contains name of creation
frametime=$(echo "${4}/$(ls ${2}| cat | grep \.jpg | wc -l)" | bc -l)
framerate=$(echo "1/${frametime}" | bc -l)




cat ${2}/*.jpg |  ffmpeg  -loglevel panic -framerate $framerate -f  image2pipe -i - -vf "scale=480:360,format=yuv420p,drawtext=fontfile=myfont.ttf:fontsize=30: fontcolor=white:shadowx=2:x=(w-text_w)/2:y=(h-text_h)/2:text='$3'" -r 25  ${2}/video.mp4


ffmpeg -loglevel panic -i $2/video.mp4 -i $2/audio.wav -strict experimental "creations/$1.mp4" 
rm -fr $2


