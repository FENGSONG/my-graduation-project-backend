package com.xfs.base.file;

import com.xfs.base.response.JsonResult;
import com.xfs.base.auth.AuthGuard;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Tag(name = "图片上传模块")
@Slf4j
@RestController
@RequestMapping("/v1/file")
public class UploadController {
    @Autowired
    private AuthGuard authGuard;

    @Operation(summary = "图片上传功能")
    @ApiOperationSupport(order = 10)
    @PostMapping("upload")
    public JsonResult upload(MultipartFile file) throws IOException {
        authGuard.requireLoginUser();
        log.debug("图片上传参数:{}",file);
        //1.获取原始文件名
        String fileName = file.getOriginalFilename();
        //2.获取文件后缀名
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //3.起一个不重复的新名字
        fileName = UUID.randomUUID()+suffix;
        //4.指定一个图片文件存放的位置 后面这里会换成对象存储服务器
        String dirPath = "d:/files";
        //5.为了提高图片检索效率,可以创建日期路径多层文件夹作为图片的存储路径
        SimpleDateFormat sdf = new SimpleDateFormat("/yyyy/MM/dd/");
        //6.将当前上传图片时的真实时间转为上面的日期格式
        String datePath = sdf.format(new Date());
        //7.将上面的文件夹路径进行封装,利用File类方法创建多层文件夹
        File dirFile = new File(dirPath+datePath);
        if(!dirFile.exists()){
            dirFile.mkdirs();
        }
        //8.拼接完整的图片路径,将图片存入此路径下
        String filePath = dirPath+datePath+fileName;
        //9.将图片上传到指定位置
        file.transferTo(new File(filePath));
        //10.将日期路径与图片名返回给前端
        return JsonResult.ok(datePath+fileName);
    }

    @Operation(summary = "图片删除功能")
    @ApiOperationSupport(order = 20)
    @PostMapping("remove")
    public JsonResult remove(String imgUrl){
        authGuard.requireLoginUser();
        log.debug("图片删除参数:{}",imgUrl);
        new File("d:/files"+imgUrl).delete();
        return JsonResult.ok();
    }



}
