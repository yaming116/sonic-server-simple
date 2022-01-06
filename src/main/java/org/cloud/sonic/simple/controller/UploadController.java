package org.cloud.sonic.simple.controller;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FrameRecorder;
import org.cloud.sonic.simple.config.WebAspect;
import org.cloud.sonic.simple.cv.*;
import org.cloud.sonic.simple.models.base.FindResult;
import org.cloud.sonic.simple.models.http.RespEnum;
import org.cloud.sonic.simple.models.http.RespModel;
import org.cloud.sonic.simple.tools.FileTool;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;
import java.util.UUID;

@Api(tags = "文件上传")
@RestController
@RequestMapping("/api/folder/upload")
@Slf4j
public class UploadController {
    @Autowired
    private FileTool fileTool;
    @Autowired
    private RecordHandler recordHandler;
    @Autowired
    private AKAZEFinder akazeFinder;
    @Autowired
    private SIFTFinder siftFinder;
    @Autowired
    private SimilarityChecker similarityChecker;
    @Autowired
    private TemMatcher temMatcher;
    @Autowired
    private TextReader textReader;

    @WebAspect
    @ApiOperation(value = "上传文件", notes = "上传文件到服务器")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "file", value = "文件", dataTypeClass = MultipartFile.class),
            @ApiImplicitParam(name = "type", value = "文件类型(只能为keepFiles、imageFiles、recordFiles、logFiles、packageFiles)", dataTypeClass = String.class),
    })
    @PostMapping
    public RespModel<String> uploadFiles(@RequestParam(name = "file") MultipartFile file,
                                         @RequestParam(name = "type") String type) throws IOException {
        String url = fileTool.upload(type, file);
        if (url != null) {
            return new RespModel<>(RespEnum.UPLOAD_OK, url);
        } else {
            return new RespModel<>(RespEnum.UPLOAD_ERROR);
        }
    }

    @WebAspect
    @ApiOperation(value = "上传文件（录像分段上传）", notes = "上传文件到服务器")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "file", value = "文件", dataTypeClass = MultipartFile.class),
            @ApiImplicitParam(name = "uuid", value = "文件uuid", dataTypeClass = String.class),
            @ApiImplicitParam(name = "index", value = "当前index", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "total", value = "index总数", dataTypeClass = Integer.class),
    })
    @PostMapping("/recordFiles")
    public RespModel<String> uploadRecord(@RequestParam(name = "file") MultipartFile file,
                                          @RequestParam(name = "uuid") String uuid,
                                          @RequestParam(name = "index") int index,
                                          @RequestParam(name = "total") int total) throws IOException {
        //先创建对应uuid的文件夹
        File uuidFolder = new File("recordFiles" + File.separator + uuid);
        if (!uuidFolder.exists()) {
            uuidFolder.mkdirs();
        }
        String fileName = file.getOriginalFilename();
        String newName = fileName.substring(0, fileName.indexOf(".mp4")) + "-" + index + ".mp4";
        File local = new File(uuidFolder.getPath() + File.separator + newName);
        RespModel<String> responseModel;
        try {
            file.transferTo(local.getAbsoluteFile());
            responseModel = new RespModel<>(RespEnum.UPLOAD_OK);
        } catch (FileAlreadyExistsException e) {
            responseModel = new RespModel<>(RespEnum.UPLOAD_ERROR);
        }
        //如果当前是最后一个，就开始合并录像文件
        if (index == total - 1) {
            responseModel.setData(fileTool.merge(uuid, file.getOriginalFilename(), total));
        }
        return responseModel;
    }

    @WebAspect
    @ApiOperation(value = "cv定位", notes = "三种定位方式")
    @ApiImplicitParams(value = {
//            @ApiImplicitParam(name = "file", value = "文件", dataTypeClass = MultipartFile.class),
//            @ApiImplicitParam(name = "type", value = "文件类型(只能为keepFiles、imageFiles、recordFiles、logFiles、packageFiles)", dataTypeClass = String.class),
    })
    @PostMapping("/cv/finder")
    public RespModel<FindResult> finder(@RequestParam(name = "file1") MultipartFile file1,
                                        @RequestParam(name = "file2") MultipartFile file2,
                                        @RequestParam(name = "type") String type) throws IOException {
        File local1 = new File("temp" + File.separator +
                UUID.randomUUID() + file1.getOriginalFilename()
                .substring(file1.getOriginalFilename().lastIndexOf(".")));
        try {
            file1.transferTo(local1.getAbsoluteFile());
        } catch (FileAlreadyExistsException e) {
            log.error(e.getMessage());
        }
        File local2 = new File("temp" + File.separator +
                UUID.randomUUID() + file2.getOriginalFilename()
                .substring(file2.getOriginalFilename().lastIndexOf(".")));
        try {
            file2.transferTo(local2.getAbsoluteFile());
        } catch (FileAlreadyExistsException e) {
            log.error(e.getMessage());
        }
        FindResult findResult = null;
        switch (type) {
            case "akaze":
                findResult = akazeFinder.getAKAZEFindResult(local1, local2);
                break;
            case "sift":
                findResult = siftFinder.getSIFTFindResult(local1, local2);
                break;
            case "tem":
                findResult = temMatcher.getTemMatchResult(local1, local2);
                break;
        }
        if (findResult != null) {
            return new RespModel(RespEnum.HANDLE_OK, findResult);
        } else {
            return new RespModel<>(RespEnum.UNKNOWN_ERROR);
        }
    }
}
