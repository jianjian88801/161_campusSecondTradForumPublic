package com.xlf.framework.strategy.mode;

import com.xlf.common.exception.BizException;
import com.xlf.common.util.FileUtil;

import com.xlf.common.util.StringUtils;
import com.xlf.framework.strategy.UploadStrategy;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 抽象上传模板
 *
 */
@Service
public abstract class AbstractUploadStrategyImpl implements UploadStrategy {

    /**
     *
     * @param file 文件
     * @param path 上传路径 即在基础路径下的文件夹  eg:   xxx  前后都不要加/
     * @return
     */
    @Override
    public String uploadFile(MultipartFile file, String path) {
        try {
            // 获取文件md5值
            String md5 = FileUtil.getMd5(file.getInputStream());
            // 获取文件扩展名
            String extName = FileUtil.getExtNameTo(file.getOriginalFilename());
            // 重新生成文件名  eg : xxxx.jpg
            String fileName = md5 + extName;

            String _fileName;
            if(StringUtils.isEmpty(path)) //_fileName  eg: md5.jpg
                _fileName = fileName;
            else
                _fileName = path + File.separator + fileName; //_fileName  eg:  path/md5.jpg
            // 判断文件是否已存在  eg: path/md5.jpg
            if (!exists(_fileName)) {
                // 不存在则继续上传
                upload(path, fileName, file);
            }
            // 返回文件访问路径
            return getFileAccessUrl(_fileName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizException("文件上传失败");
        }
    }

    /**
     *
     * @param file 文件
     * @return
     */
    @Override
    public String uploadFile(MultipartFile file) {
        return uploadFile(file,null);
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath 文件路径
     * @return {@link Boolean}
     */
    public abstract Boolean exists(String filePath) throws FileNotFoundException;

    /**
     * 上传
     * @param path
     * @param fileName
     * @param file
     * @throws IOException
     */
    public abstract void upload(String path, String fileName, MultipartFile file) throws IOException;

    /**
     * 获取文件访问url
     *
     * @param filePath 文件路径
     * @return {@link String}
     */
    public abstract String getFileAccessUrl(String filePath);

}
