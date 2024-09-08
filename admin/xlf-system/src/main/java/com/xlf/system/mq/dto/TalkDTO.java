package com.xlf.system.mq.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TalkDTO implements Serializable {

    Long id;
    Long userId;
    Date createTime;
}
