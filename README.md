android-heart-rate-monitor
==========================

Android heart rate monitor

## Introduction

Android based heart rate monitor which uses the camera and its flash to determine the users heart rate in beats per minute.

* Created by Justin Wetherell
* Google: http://code.google.com/p/android-heart-rate-monitor
* Github: http://github.com/phishman3579/android-heart-rate-monitor
* LinkedIn: http://www.linkedin.com/in/phishman3579
* E-mail: phishman3579@gmail.com
* Twitter: http://twitter.com/phishman3579

## Details
The App uses the PreviewCallback mechanism to grab the latest image from the preview frame. It then processes the YUV420SP data and pulls out all the red pixel values.

It uses data smoothing in a Integer array to figure out the average red pixel value in the image. Once it figures out the average it determines a heart beat when the average red pixel value in the latest image is greater than the smoothed average.

The App will collect data in ten second chunks and add the beets per minute to another Integer array which is used to smooth the beats per minute data.

## How To

All you have to do is open the HeartRateMonitor App and then hold the tip of your index finger over the camera lens of your phone. The entire camera preview image should be red with a lighter area where the tip of your finger is touching. Do not press too hard or you will cut off circulation which will result in an inaccurate reading.  

After a second or two, you should see the Android icon on the top of the screen start to flash red when it senses a heart beat. After ten seconds it will compute your heart rate and update the number next to the Android icon. It'll take between ten and thirty seconds to get an accurate heart rate.

