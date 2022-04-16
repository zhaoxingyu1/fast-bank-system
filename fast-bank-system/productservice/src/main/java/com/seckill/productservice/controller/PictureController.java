//package com.seckill.productservice.controller;
//
//import com.netflix.ribbon.proxy.annotation.Http;
//import com.seckill.common.enums.CodeEnum;
//import com.seckill.common.response.DataFactory;
//import com.seckill.common.response.ListData;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.servlet.http.HttpServletRequest;
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.UUID;
//
///**
// * @Author: 七画一只妖
// * @Date: 2022-04-12 14:13
// */
//@RestController
//@RequestMapping("/product")
//public class PictureController {
//
//    @PostMapping("/uploads/{id}")
//    public Object uploads(@RequestParam("image") MultipartFile[] files,
//                          @PathVariable("id") String id,
//                          HttpServletRequest request) {
////        String realPath = request.getSession().getServletContext().getRealPath("/product_images/");
//        String realPath = System.getProperty("user.dir")+"\\productservice\\src\\main\\resources\\static\\";
//        System.out.println(realPath);
//        File folder = new File(realPath);
//        if (!folder.exists()) {
//            folder.mkdirs();
//        }
//
//
//        int i = 1;
//        ArrayList<String> urlList = new ArrayList<>();
//        for (MultipartFile file : files) {
//            String fileName = id + "-" + i + ".jpg";
//            try {
//                file.transferTo(new File(realPath + fileName));
//                String url = realPath + fileName;
//                urlList.add(url);
//                System.out.println(realPath + fileName);
//            } catch (Exception e) {
//                e.printStackTrace();
//                return DataFactory.fail(CodeEnum.UNAUTHORIZED, "上传失败");
//            }
//            i++;
//        }
//        System.out.println(urlList);
//        return DataFactory.success(ListData.class, "上传成功").parseData(urlList);
//    }
//}
