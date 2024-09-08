package com.xlf.system.service;

public interface RedisCacheBuilder {

     void buildFollowCache(Long userId, String key);

     void buildFansCache(Long userId, String key);

     void buildUserDropBox(Long userId, String key);

     void buildTalkThumbCache(Long talkId, String key);

     void buildTalkStarCollectionCache(Long userId, String key);

     void buildTopicCollectionCache(Long userId,String key);

      void buildTopicThumbCache(Long talkId, String key);
}
