package test.fenn.icu.media.video;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fenn.icu.media.video.common.VideoEncodeTemplateEnum;
import com.fenn.icu.media.video.model.VideoInfo;
import com.fenn.icu.media.video.support.VideoFFmpegCli;
import com.google.common.collect.Lists;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @className: TestVideoFFmpegClient
 * @Description:
 * @version: v1.0.0
 * @author: afeng
 * @date: 2022-08-25 16:36
 *
 * Env init：Mac OS brew install ffmpeg
 *
 *
 */
public class TestVideoFFmpegCli {

    private static final String TEST_RESOURCE_ROOT = Paths.get("src","test","resources").toAbsolutePath().toString()+"/";

    private static final String TEST_VIDEO = TEST_RESOURCE_ROOT + "720p.mp4";

    private static final String TEST_VIDEO_WITH_SUBTITLE = TEST_RESOURCE_ROOT + "720p_with_subtitle.mp4";

    private static final String TEST_SRT = TEST_RESOURCE_ROOT + "720p.srt";

    private static final String TEST_CREATE_VIDEO_IMAGE = TEST_RESOURCE_ROOT + "test-create-video.jpg";

    private static final VideoEncodeTemplateEnum testVideoEncodeTemplateEnum = VideoEncodeTemplateEnum.SD_480P;


    /**
     * ==== output ====
     */
    private static final String OUTPUT_ENCODE_VIDEO_PATH = TEST_VIDEO + "_" + testVideoEncodeTemplateEnum.getKey() + "." + testVideoEncodeTemplateEnum.getFormat();
    private static final String OUTPUT_CERATE_VIDEO_BY_IMAGE = TEST_CREATE_VIDEO_IMAGE.replace(".jpg", ".mp4");
    private static final String OUTPUT_FIRST_IMAGE = TEST_VIDEO.replace(".mp4", ".jpg");
    private static final String OUTPUT_CONCAT_VIDEO = TEST_VIDEO.replace(".mp4", "-concat.mp4");
    private static final String OUTPUT_VIDEO_ADD_SUBTITLE = TEST_VIDEO.replace(".mp4", "_add_subtitle.mp4");;
    private static final String OUTPUT_VIDEO_EXPORT_SUBTITLE = TEST_VIDEO_WITH_SUBTITLE.replace(".mp4", "_export.srt");

    @BeforeClass
    public static void init() throws Exception {
        new TestVideoFFmpegCli().clear();
    }

    @Test
    public void clear() throws Exception {
        Files.deleteIfExists(Paths.get(OUTPUT_ENCODE_VIDEO_PATH));
        Files.deleteIfExists(Paths.get(OUTPUT_CERATE_VIDEO_BY_IMAGE));
        Files.deleteIfExists(Paths.get(OUTPUT_FIRST_IMAGE));
        Files.deleteIfExists(Paths.get(OUTPUT_CONCAT_VIDEO));
        Files.deleteIfExists(Paths.get(OUTPUT_VIDEO_ADD_SUBTITLE));
        Files.deleteIfExists(Paths.get(OUTPUT_VIDEO_EXPORT_SUBTITLE));
    }

    @Test
    public void testResolve() throws Exception {

        VideoInfo videoInfo = VideoFFmpegCli.convertInfo(VideoFFmpegCli.getInfo(TEST_VIDEO));

        System.out.println(JSON.toJSONString(videoInfo, SerializerFeature.PrettyFormat));
    }

    @Test
    public void testEncode() throws Exception {

        VideoFFmpegCli.encode(VideoFFmpegCli.getInfo(TEST_VIDEO), OUTPUT_ENCODE_VIDEO_PATH, testVideoEncodeTemplateEnum);

        System.out.println("testEncode from:"+TEST_VIDEO+", output video:"+OUTPUT_ENCODE_VIDEO_PATH);
    }

    @Test
    public void testCreateVideoByImg() throws Exception {

        VideoInfo videoInfo = VideoFFmpegCli.convertInfo(VideoFFmpegCli.createVideoByImg(OUTPUT_CERATE_VIDEO_BY_IMAGE, TEST_CREATE_VIDEO_IMAGE));

        System.out.println("testCreateVideoByImg result:"+JSON.toJSONString(videoInfo));
    }

    @Test
    public void testFirstImg() throws Exception {

        VideoFFmpegCli.createFirstImg(TEST_VIDEO, OUTPUT_FIRST_IMAGE);

        System.out.println("done");
    }

    @Test
    public void testConcat() throws Exception {

        long startTime = System.currentTimeMillis();

        VideoFFmpegCli.concat(
                Lists.newArrayList(TEST_VIDEO, TEST_VIDEO, TEST_VIDEO),
                OUTPUT_CONCAT_VIDEO
        );

        VideoInfo videoInfo = VideoFFmpegCli.convertInfo(VideoFFmpegCli.getInfo(OUTPUT_CONCAT_VIDEO));

        System.out.println(JSON.toJSONString("testConcat result:"+videoInfo, SerializerFeature.PrettyFormat));

        System.out.println("concat result concatVideoPath:"+OUTPUT_CONCAT_VIDEO+", cost:"+(System.currentTimeMillis() - startTime));
    }

    @Test
    public void testAddSubtitle() throws Exception {

        VideoFFmpegCli.addSubtitle(TEST_VIDEO, TEST_SRT, OUTPUT_VIDEO_ADD_SUBTITLE);

        VideoInfo videoInfo = VideoFFmpegCli.convertInfo(VideoFFmpegCli.getInfo(OUTPUT_VIDEO_ADD_SUBTITLE));

        System.out.println(JSON.toJSONString(" testAddSubtitle result:"+videoInfo, SerializerFeature.PrettyFormat));
    }

    @Test
    public void testExportSubtitle() throws Exception {

        String exportSrtPath = TEST_VIDEO_WITH_SUBTITLE.replace(".mp4", "_export.srt");

        VideoFFmpegCli.exportSubtitle(TEST_VIDEO_WITH_SUBTITLE, exportSrtPath);
    }
}
