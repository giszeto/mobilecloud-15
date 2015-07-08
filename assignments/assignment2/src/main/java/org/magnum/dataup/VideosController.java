package org.magnum.dataup;

import org.apache.commons.lang3.ObjectUtils;
import org.magnum.dataup.model.Video;
import org.magnum.dataup.model.VideoRepository;
import org.magnum.dataup.model.VideoStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.mime.TypedFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

@Controller
public class VideosController {
    @Autowired
    private VideoRepository mVideos;

    @Autowired
    private VideoFileManager mVideoFileManager;

    /**
     * @return
     */
    @RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH, method = RequestMethod.GET)
    public
    @ResponseBody
    Collection<Video> getVideoList() {
        return mVideos.getVideos();
    }

    /**
     * @param v
     * @return
     */
    @RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH, method = RequestMethod.POST)
    public
    @ResponseBody
    Video addVideo(@RequestBody Video v) {
        return mVideos.addVideo(v) ? v : new Video();
    }

    /**
     * @param id
     * @param videoData
     * @return
     */
    @RequestMapping(value = VideoSvcApi.VIDEO_DATA_PATH, method = RequestMethod.POST)
    public
    @ResponseBody
    VideoStatus setVideoData(@PathVariable(VideoSvcApi.ID_PARAMETER) long id,
                             @RequestPart(VideoSvcApi.DATA_PARAMETER)
                             MultipartFile videoData,
                             HttpServletResponse response)
        throws IOException {
        VideoStatus status = new VideoStatus(VideoStatus.VideoState.READY);
        try {
            mVideoFileManager.saveVideoData(mVideos.getVideo(id), videoData.getInputStream());
        } catch (NullPointerException npe) {
            response.setStatus(404);
        }

        return status;
    }

    /**
     * @param id
     * @return
     */
    @RequestMapping(value = VideoSvcApi.VIDEO_DATA_PATH, method = RequestMethod.GET)
    public void getData(@PathVariable(VideoSvcApi.ID_PARAMETER) long id,
                        HttpServletResponse response) throws IOException{
        try {
            mVideoFileManager.copyVideoData(mVideos.getVideo(id), response.getOutputStream());
        } catch (NullPointerException npe) {
            response.setStatus(404);
        }
    }

}
