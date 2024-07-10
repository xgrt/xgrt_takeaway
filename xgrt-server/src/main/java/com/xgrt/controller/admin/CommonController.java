package com.xgrt.controller.admin;

import com.xgrt.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.lang.annotation.Retention;

/**
 * 通用接口
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
public class CommonController {

    /**
     * 文件上传
     * @param file
     * @return
     */
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) {
        //将 文件 上传到 阿里云服务器

        return null;
    }
}
