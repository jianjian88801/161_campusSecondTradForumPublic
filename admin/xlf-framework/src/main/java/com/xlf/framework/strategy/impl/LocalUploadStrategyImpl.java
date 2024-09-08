package com.xlf.framework.strategy.impl;


import com.xlf.common.exception.BizException;
import com.xlf.common.util.StringUtils;
import com.xlf.framework.strategy.mode.AbstractUploadStrategyImpl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * 本地上传策略(具体实现类）
 * 项目别放到中文目录下！
 *   String basePath = ResourceUtils.getURL("classpath:").getPath(); 得到编译后的路径 /D:/order_food/xlf_main/target/classes/
 *
 *
 */
@Service("localUploadStrategyImpl")
public class LocalUploadStrategyImpl extends AbstractUploadStrategyImpl {

    /**
     * 本地路径,资源存放的文件夹 最后不要加/
     */
    @Value("${upload.local.path}")
    private String localPath;

    /**
     * 访问url 最后不要加/
     */
    @Value("${upload.local.url}")
    private String localUrl;

    /**
     *
     * @param filePath 文件路径 xxx/xxx.jpg
     * @return
     * @throws FileNotFoundException
     */
    @Override
    public Boolean exists(String filePath) {
        return new File( localPath + File.separator + filePath).exists();
    }


    /**
     *
     * @param path  xxx
     * @param fileName  xxx.jpg
     * @param file
     * @throws IOException
     */
    @Override
    public void upload(String path, String fileName, MultipartFile file) throws IOException {

        String directoryPath;
        if(StringUtils.isEmpty(path))
            directoryPath =  localPath + File.separator;
        else
            directoryPath = localPath + File.separator + path +  File.separator;
        //  directoryPath eg:  D:/xx/xx/
        File directory = new File( directoryPath);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new BizException("创建目录失败");
            }
        }
        // 写入的文件
        InputStream inputStream =file.getInputStream();

        //写入的目标文件
        File file2 = new File(directoryPath + fileName);
        if (file2.createNewFile()) {
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file2));
            byte[] bytes = new byte[1024];
            int length;
            while ((length = bis.read(bytes)) != -1) {
                bos.write(bytes, 0, length);
            }
            bos.flush();
            inputStream.close();
            bis.close();
            bos.close();
        }
    }

    @Override
    public String getFileAccessUrl(String filePath) {
        return localUrl + File.separator + filePath;
    }

}
