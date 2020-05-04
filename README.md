# ARSpeechBubble
Augmented Reality with Speech to Text features for Android platform

I used GRPC to livestream Google Text to Speech API.
I then used Google facial recoginition API to identify position of the face. 
In order to obtain the position of the mouth I used Euler's coordinates from Google Facial recognition
to calculate the conture/angle of the face, which I then used to mark the moouth region.

Should be able to start up by cloning the repo and running it through Android Studio.

No prereqs other than installing Android Studio

Simply point the camera toward your face and there will be a blue dot following your mouth. Then speak to the device
and your speech will be translated to text shown on your mouth. 
(Works better in quiet areas with stable internet connections)


I am open to any suggestions, optimizations, bugs, or issues with Google credentials. Feel free to play around.

3D graphics will be uploaded and intergrated once I stabilize mouth detection abit more. Stay Tuned!
