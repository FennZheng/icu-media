# icu-media
Icu tools for media, as a wrapper for ffmpeg and graphicmagick and etc.

In this version (20220825):

(1) You can use VideoFFmpegCli API implementation as following functions：

+ Get Video Info
+ Convert images into video
+ Create first image from video
+ Encode video by video template
+ Concat videos
+ Export subtitle from video(only supports mp4)
+ Add subtitle into video(only supports mp4/srt)


Exmaple details: [TestVideoFFmpegCli.java](https://github.com/FennZheng/icu-media/blob/master/icu-media-video/src/test/java/test/fenn/icu/media/video/TestVideoFFmpegCli.java)