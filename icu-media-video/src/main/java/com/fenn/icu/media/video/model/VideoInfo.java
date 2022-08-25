package com.fenn.icu.media.video.model;

import lombok.Data;

/**
 * @className: VideoInfo
 * @Description:
 * @version: v1.0.0
 * @author: fennzheng
 * @date: 2022-08-25 15:41
 */
@Data
public class VideoInfo {

    private String formatName;
    private double duration;
    private long size;
    private String videoCodec;
    private int videoWidth;
    private int videoHeight;
    private long videoBitRate;
    private String audioCodec;
    private long audioBitRate;
    private long audioSampleRate;
    private int audioChannels;
}
