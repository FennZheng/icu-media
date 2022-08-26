package com.fenn.icu.media.video.support;

import com.fenn.icu.media.video.common.VideoEncodeTemplateEnum;
import com.fenn.icu.media.video.model.VideoInfo;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.builder.FFmpegOutputBuilder;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @className: VideoFFmpegCli
 * @Description:
 * @version: v1.0.0
 * @author: Fenn Zheng
 * @date: 2022-08-25 15:41
 */
@Slf4j
public class VideoFFmpegCli {


    private static final String PRESET_VERY_FAST = "veryfast";
    private static final String FIX_FMT_YUV420P = "yuv420p";

    /**
     * create video by images
     * @param videoNewPath
     * @param imgPaths
     * @return
     * @throws Exception
     */
    public static FFmpegProbeResult createVideoByImg(String videoNewPath, String... imgPaths) throws Exception {
        FFmpeg ffmpeg = new FFmpeg();
        FFprobe ffprobe = new FFprobe();

        VideoEncodeTemplateEnum videoEncodeTemplateEnum = VideoEncodeTemplateEnum.FLU_360P;

        FFmpegBuilder builder = new FFmpegBuilder();
        for (String imgPath : imgPaths){
            builder.addInput(imgPath);
        }
        builder.overrideOutputFiles(true)
                .addOutput(videoNewPath)
                //幻灯片方式
                .setVideoFrameRate(1)
                .setFormat(videoEncodeTemplateEnum.getFormat())
                .setAudioChannels(videoEncodeTemplateEnum.getAudioChannel())
                .setAudioCodec(videoEncodeTemplateEnum.getAudioCodec())
                .setAudioSampleRate(videoEncodeTemplateEnum.getAudioSampleRate())
                .setAudioBitRate(videoEncodeTemplateEnum.getAudioBitRate())
                .setVideoCodec(videoEncodeTemplateEnum.getVideoCodec())
                .setVideoFrameRate(videoEncodeTemplateEnum.getVideoFps(), 1)
                .setVideoResolution(videoEncodeTemplateEnum.getResolutionWidth(), videoEncodeTemplateEnum.getResolutionHeight())
                .setStrict(FFmpegBuilder.Strict.STRICT)
                .setVideoPixelFormat(FIX_FMT_YUV420P)
                .done();

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
        executor.createJob(builder).run();

        return getInfo(videoNewPath);
    }

    /**
     * convert video origin info into VideoInfo
     *
     * @param fFmpegProbeResult
     * @return
     * @throws IOException
     */
    public static VideoInfo convertInfo(FFmpegProbeResult fFmpegProbeResult) throws RuntimeException {

        if (fFmpegProbeResult == null || fFmpegProbeResult.hasError()){
            throw new RuntimeException("convert fail: fFmpegProbeResult is null or has error!");
        }

        FFmpegFormat format = fFmpegProbeResult.getFormat();

        VideoInfo videoInfo = new VideoInfo();
        videoInfo.setFormatName(format.format_name);
        videoInfo.setDuration(format.duration);
        videoInfo.setSize(format.size);

        for (FFmpegStream stream : fFmpegProbeResult.getStreams()){
            //非空才设置
            if (stream.codec_type == FFmpegStream.CodecType.VIDEO && StringUtils.isBlank(videoInfo.getVideoCodec())){
                videoInfo.setVideoCodec(stream.codec_name);
                videoInfo.setVideoBitRate(stream.bit_rate);
                videoInfo.setVideoHeight(stream.height);
                videoInfo.setVideoWidth(stream.width);
            }
            if (stream.codec_type == FFmpegStream.CodecType.AUDIO && StringUtils.isBlank(videoInfo.getAudioCodec())){
                videoInfo.setAudioCodec(stream.codec_name);
                videoInfo.setAudioBitRate(stream.bit_rate);
                videoInfo.setAudioSampleRate(stream.sample_rate);
                videoInfo.setAudioChannels(stream.channels);
            }
        }
        return videoInfo;
    }

    /**
     * get video info
     *
     * @param videoPath
     * @return
     * @throws IOException
     */
    public static FFmpegProbeResult getInfo(String videoPath) throws IOException, RuntimeException {

        FFprobe ffprobe = new FFprobe();
        FFmpegProbeResult probeResult = ffprobe.probe(videoPath);

        if (probeResult.hasError()){
            throw new RuntimeException("probe fail code:"+probeResult.getError().code+", error:"+probeResult.getError().string);
        }
        return probeResult;
    }

    /**
     * export first img from video
     * @param videoPath
     * @return
     * @throws IOException
     */
    public static boolean createFirstImg(String videoPath, String newImgPath) throws IOException, RuntimeException {

        if (StringUtils.isBlank(videoPath)) {
            return false;
        }
        if (StringUtils.isBlank(newImgPath)) {
            return false;
        }

        FFmpeg ffmpeg = new FFmpeg();
        FFprobe ffprobe = new FFprobe();

        FFmpegOutputBuilder fFmpegOutputBuilder = new FFmpegBuilder()
                .setInput(videoPath)
                .overrideOutputFiles(true)
                .addOutput(newImgPath)
                .setFormat("mjpeg")
                .setFrames(1)
                ;

        FFmpegBuilder builder = fFmpegOutputBuilder.done();

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

        executor.createJob(builder).run();
        return true;
    }

    /**
     * encode video by video template
     *
     * @param ffmpegProbeResult
     * @param newFilePath
     * @param videoEncodeTemplateEnum
     * @return
     * @throws Exception
     */
    public static boolean encode(FFmpegProbeResult ffmpegProbeResult, String newFilePath, VideoEncodeTemplateEnum videoEncodeTemplateEnum) throws Exception {

        if (ffmpegProbeResult == null) {
            return false;
        }
        if (StringUtils.isBlank(newFilePath)) {
            return false;
        }
        if (videoEncodeTemplateEnum == null) {
            return false;
        }

        FFmpeg ffmpeg = new FFmpeg();
        FFprobe ffprobe = new FFprobe();

        FFmpegOutputBuilder fFmpegOutputBuilder = new FFmpegBuilder()
                .setInput(ffmpegProbeResult)
                .overrideOutputFiles(true)
                .addOutput(newFilePath)
                .setFormat(videoEncodeTemplateEnum.getFormat())
                .setAudioChannels(videoEncodeTemplateEnum.getAudioChannel())
                .setAudioCodec(videoEncodeTemplateEnum.getAudioCodec())
                .setAudioSampleRate(videoEncodeTemplateEnum.getAudioSampleRate())
                .setAudioBitRate(videoEncodeTemplateEnum.getAudioBitRate())
                .setVideoCodec(videoEncodeTemplateEnum.getVideoCodec())
                .setVideoFrameRate(videoEncodeTemplateEnum.getVideoFps(), 1)
                .setVideoResolution(videoEncodeTemplateEnum.getResolutionWidth(), videoEncodeTemplateEnum.getResolutionHeight())
                .setStrict(FFmpegBuilder.Strict.STRICT)
                //加ultrafast、superfast、veryfast、faster、fast、medium、slow、slower、veryslow、placebo
                .setPreset(PRESET_VERY_FAST)
                //x264 default use 23. if video resolution is 1080p, prefer use 31.
                .setVideoPixelFormat(FIX_FMT_YUV420P)
                .setVideoWidth(videoEncodeTemplateEnum.getResolutionWidth())
                .setVideoHeight(videoEncodeTemplateEnum.getResolutionHeight())
                .setConstantRateFactor(23);

        // duration cut
        if (videoEncodeTemplateEnum.getDuration() > 0){
            fFmpegOutputBuilder.setDuration(videoEncodeTemplateEnum.getDuration(), TimeUnit.SECONDS);
        }

        FFmpegBuilder builder = fFmpegOutputBuilder.done();

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

        executor.createJob(builder).run();
        return true;
    }

    /**
     * concat video
     *
     * use concat protocol to merge multi videos
     *
     * tip: diff videos container may cause compatibility problems, please check it finally
     *
     * @param originVideoPaths
     * @param newVideoPath
     * @return
     * @throws Exception
     */
    public static boolean concat(List<String> originVideoPaths, String newVideoPath) throws Exception {

        // convert files info into one text
        Path listOfOriginVideoFile = null;

        try {
            FFmpeg ffmpeg = new FFmpeg();
            FFprobe ffprobe = new FFprobe();

            FFmpegBuilder fFmpegBuilder = new FFmpegBuilder();

            String filesStrings = originVideoPaths
                    .stream()
                    .map(p -> "file '" + p + "'")
                    .collect(Collectors.joining(System.getProperty("line.separator")));

            listOfOriginVideoFile = Files.createTempFile(Paths.get(newVideoPath).getParent(), "ffmpeg-list-", ".txt");

            Files.write(listOfOriginVideoFile, filesStrings.getBytes());

            FFmpegOutputBuilder fFmpegOutputBuilder = fFmpegBuilder
                    // overrideOutputFiles is conflict
                    // disable unsafe file check
                    .addExtraArgs("-safe", "0")
                    .setInput(listOfOriginVideoFile.toAbsolutePath().toString())
                    .setFormat("concat")
                    .addOutput(newVideoPath)
                    .addExtraArgs("-vcodec","copy")
                    .addExtraArgs("-acodec","copy")
                    ;

            FFmpegBuilder builder = fFmpegOutputBuilder.done();

            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

            executor.createJob(builder).run();
        } finally {
            if (listOfOriginVideoFile != null){
                Files.deleteIfExists(listOfOriginVideoFile);
            }
        }

        return true;
    }

    /**
     * export subtitle
     *
     * only export first subtitle if it contains multi subtitles
     *
     * @param videoPath
     * @return
     * @throws Exception if video not contain any subtitle
     */
    public static boolean exportSubtitle(String videoPath, String newSrtPath) throws Exception {

        FFmpeg ffmpeg = new FFmpeg();
        FFprobe ffprobe = new FFprobe();

        FFmpegBuilder fFmpegBuilder = new FFmpegBuilder();

        /**
         * 0:s:0：
         * 0 -> input_file_id= file index num
         * :s -> :stream_specifier, s means subtitle
         * :0 -> :stream_specifier multi subtitle, default use 0
         */
        FFmpegOutputBuilder fFmpegOutputBuilder = fFmpegBuilder
                .setInput(videoPath)
                .setVideoFilter("-map 0:s:0")
                .addOutput(newSrtPath);

        FFmpegBuilder builder = fFmpegOutputBuilder.done();

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

        executor.createJob(builder).run();

        return true;
    }

    /**
     * add soft subtitle
     *
     * only support mp4: mov_text
     * only support subtitle format: srt
     *
     * @param videoPath
     * @return
     * @throws Exception
     */
    public static boolean addSubtitle(String videoPath, String subtitlePath, String newVideoPath) throws Exception {

        FFmpeg ffmpeg = new FFmpeg();
        FFprobe ffprobe = new FFprobe();

        FFmpegBuilder fFmpegBuilder = new FFmpegBuilder();

        FFmpegOutputBuilder fFmpegOutputBuilder = fFmpegBuilder
                .setFormat("srt")
                .addInput(subtitlePath)
                .addInput(videoPath)
                .addOutput(newVideoPath)
                .addExtraArgs("-c:v","copy")
                .addExtraArgs("-c:a","copy")
                .addExtraArgs("-c:s","mov_text")
                ;

        FFmpegBuilder builder = fFmpegOutputBuilder.done();

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

        executor.createJob(builder).run();

        return true;
    }
}
