package com.fenn.icu.media.video.common;

/**
 * @className: VideoEncodeTemplateEnum
 * @Description:
 * @version: v1.0.0
 * @author: fennzheng
 * @date: 2022-08-25 15:41
 */
public enum VideoEncodeTemplateEnum {

    /**
     * video encode template
     */
    FLU_360P("360p","mp4", -1,480, 360, 400*1000, 25, "libx264", 64*1000, 44100, 2, "aac"),
    SD_480P("480p","mp4", -1, 640, 480, 900*1000, 25, "libx264", 64*1000, 44100, 2, "aac"),
    HD_720P("720p","mp4", -1, 960, 720, 1800*1000, 25, "libx264", 128*1000, 44100,2, "aac"),
    FHD_1080P("1080p","mp4", -1, 1440, 1080, 2500*1000, 25, "libx264", 128*1000, 44100,2, "aac"),

    ;

    VideoEncodeTemplateEnum(String key, String format, int duration, int resolutionWidth, int resolutionHeight, int videoBitRate, int videoFps, String videoCodec,
                            int audioBitRate, int audioSampleRate, int audioChannel, String audioCodec){
        this.key = key;
        this.format = format;
        this.duration = duration;
        this.resolutionWidth = resolutionWidth;
        this.resolutionHeight = resolutionHeight;
        this.videoBitRate = videoBitRate;
        this.videoFps = videoFps;
        this.videoCodec = videoCodec;
        this.audioBitRate = audioBitRate;
        this.audioSampleRate = audioSampleRate;
        this.audioChannel = audioChannel;
        this.audioCodec = audioCodec;
    }

    /**
     * key
     */
    private String key;
    /**
     * format:mp4，hls
     */
    private String format;
    /**
     * duration, -1:ignore
     */
    private int duration;
    private int resolutionWidth;
    private int resolutionHeight;
    private int videoBitRate;
    private int videoFps;
    private String videoCodec;
    private int audioBitRate;
    private int audioSampleRate;
    private int audioChannel;
    private String audioCodec;

    public String getKey() {
        return key;
    }

    public String getFormat() {
        return format;
    }

    public int getDuration() {
        return duration;
    }

    public int getResolutionWidth() {
        return resolutionWidth;
    }

    public int getResolutionHeight() {
        return resolutionHeight;
    }

    public int getVideoBitRate() {
        return videoBitRate;
    }

    public int getVideoFps() {
        return videoFps;
    }

    public String getVideoCodec() {
        return videoCodec;
    }

    public int getAudioBitRate() {
        return audioBitRate;
    }

    public int getAudioSampleRate() {
        return audioSampleRate;
    }

    public int getAudioChannel() {
        return audioChannel;
    }

    public String getAudioCodec() {
        return audioCodec;
    }}
