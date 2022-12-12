package com.lt.debate.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lt.debate.mapper.DebateContentInfoMapper;
import com.lt.debate.mapper.DebateContentMapper;
import com.lt.debate.mapper.DebateKindMapper;
import com.lt.debate.service.DebateContentService;
import com.lt.model.debate.common.HotContentConstants;
import com.lt.model.debate.common.PageResponseResult;
import com.lt.model.debate.dto.BehaviorConstants;
import com.lt.model.debate.dto.ContentVisitStreamMess;
import com.lt.model.debate.dto.DebateContentDto;
import com.lt.model.debate.dto.DebatePageDto;
import com.lt.model.debate.pojo.DebateContent;
import com.lt.model.debate.pojo.DebateContentES;
import com.lt.model.debate.pojo.DebateContentInfo;
import com.lt.model.debate.vo.DebateContentInfoVo;
import com.lt.model.debate.vo.DebateContentVo;
import com.lt.model.response.AppHttpCodeEnum;
import com.lt.model.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @Author WanXin
 * @Date 2022/11/15
 */
@Service
@Transactional
@Slf4j
public class DebateContentServiceImpl implements DebateContentService {

    @Autowired
    private DebateKindMapper kindMapper;

    @Override
    public Response getKinds() {
        return Response.okResult(kindMapper.getKinds());
    }

    @Autowired
    private DebateContentMapper contentMapper;

    @Autowired
    private RestHighLevelClient client;

    @Override
    public Response getByKind(DebatePageDto dto) {
        dto.checkParam();
        try {
            SearchRequest request = new SearchRequest("content");
            // 准备DSL
            buildBasicQuery(request, dto);

            Integer page = dto.getPage();
            Integer size = dto.getSize();
            // 高亮
            HighlightBuilder highlightBuilder = new HighlightBuilder().field("title").field("summary")
                    .requireFieldMatch(false);
            highlightBuilder.preTags("<font size=\"3\" color=\"red\">");
            highlightBuilder.postTags("</font>");
            request.source().highlighter(highlightBuilder);
            // 分页
            request.source().from((page - 1) * size).size(size).sort("createTime", SortOrder.DESC);

            // 发送请求
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);

            // 解析响应
            return handleResponse(response, dto);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    // 解析响应
    private Response handleResponse(SearchResponse response, DebatePageDto dto) {
        SearchHits searchHits = response.getHits();
        long total = searchHits.getTotalHits().value;
        SearchHit[] hits = searchHits.getHits();

        List<DebateContentVo> debateContentVos = new ArrayList<>();
        for (SearchHit hit : hits) {
            String json = hit.getSourceAsString();
            DebateContent debateContent = JSON.parseObject(json, DebateContent.class);
            DebateContentVo vo = new DebateContentVo();
            BeanUtils.copyProperties(debateContent, vo);
            // 获取高亮
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (!CollectionUtils.isEmpty(highlightFields)) {
                HighlightField highlightField1 = highlightFields.get("title");
                HighlightField highlightField2 = highlightFields.get("summary");

                if (highlightField1 != null) {
                    String title = highlightField1.getFragments()[0].toString();
                    vo.setTitle(title);
                }

                if (highlightField2 != null) {
                    String summary = highlightField2.getFragments()[0].toString();
                    vo.setSummary(summary);
                }
            }

            debateContentVos.add(vo);
        }

        Response res = new PageResponseResult(dto.getPage(), dto.getSize(), (int) total);
        res.setData(debateContentVos);

        return res;
    }

    // 准备DSL
    private void buildBasicQuery(SearchRequest request, DebatePageDto dto) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        String key = dto.getKey();
        if (StringUtils.isBlank(key)) {
            boolQueryBuilder.must(QueryBuilders.matchAllQuery());
        } else {
            boolQueryBuilder.must(QueryBuilders.matchQuery("all", key));
        }

        boolQueryBuilder.filter(QueryBuilders.termQuery("kindId", dto.getKindId()));

        request.source().query(boolQueryBuilder);
    }

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Response getHot() {
        String json = stringRedisTemplate.opsForValue().get(HotContentConstants.HOT_DATA_TODAY);
        if (StringUtils.isNotBlank(json)) {
            List<DebateContent> debateContents = JSON.parseArray(json, DebateContent.class);

            return getResponse(debateContents);
        }

        return Response.errorResult(AppHttpCodeEnum.SERVER_ERROR, "正在更新当日热点！请稍后");
    }

    @Override
    public Response getSuggestions(Map map) {
        String key = (String) map.get("key");
        if (StringUtils.isBlank(key)) {
            return Response.okResult(null);
        }

        try {
            SearchRequest request = new SearchRequest("content");
            request.source().suggest(new SuggestBuilder().addSuggestion(
                    "suggestions",
                    SuggestBuilders.completionSuggestion("suggestion")
                            .prefix(key)
                            .skipDuplicates(true)
                            .size(10)

            ));
            SearchResponse search = client.search(request, RequestOptions.DEFAULT);
            Suggest suggest = search.getSuggest();
            CompletionSuggestion suggestions = suggest.getSuggestion("suggestions");
            List<CompletionSuggestion.Entry.Option> options = suggestions.getOptions();
            List<String> list = new ArrayList<>(options.size());
            for (CompletionSuggestion.Entry.Option option : options) {
                String text = option.getText().toString();
                list.add(text);
            }

            return Response.okResult(list);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    private DebateContentInfoMapper debateContentInfoMapper;

    @Override
    public Response saveContent(DebateContentDto dto) {
        if (dto == null) {
            return Response.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        DebateContentInfo debateContentInfo = new DebateContentInfo();
        debateContentInfo.setContent(dto.getContent());
        debateContentInfo.setTitle(dto.getTitle());
        debateContentInfo.setStatus(1);
        if (!CollectionUtils.isEmpty(dto.getUrl())) {
            String image = JSON.toJSONString(dto.getUrl());
            debateContentInfo.setImage(image);
        }

        // 保存到info中
        debateContentInfoMapper.insert(debateContentInfo);

        DebateContent debateContent = new DebateContent();
        debateContent.setCreateTime(new Date());
        debateContent.setScore(0);
        debateContent.setSummary(dto.getSummary());
        debateContent.setTitle(dto.getTitle());
        debateContent.setRemarks(0);
        debateContent.setLikes(0);
        debateContent.setViews(0);
        debateContent.setState(1);
        debateContent.setKindId(1);
        debateContent.setInfoId(debateContentInfo.getId());
        debateContent.setOwnerId(Integer.valueOf(dto.getUsername()));

        // 保存到content中
        contentMapper.insert(debateContent);

        DebateContentES debateContentES = new DebateContentES();
        BeanUtils.copyProperties(debateContent, debateContentES);
        debateContentES.setSuggestion(Collections.singletonList(debateContent.getTitle()));

        String jsonString = JSON.toJSONString(debateContentES);

        IndexRequest request = new IndexRequest("content").id(debateContent.getId().toString());
        request.source(jsonString, XContentType.JSON);

        // 保存到ES
        try {
            client.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String json = (String) stringRedisTemplate.opsForHash().get(HotContentConstants.HOT_USER_CONTENT_TODAY, debateContent.getOwnerId().toString());
        if (StringUtils.isNotBlank(json)) {
            List<DebateContent> debateContents = JSON.parseArray(json, DebateContent.class);
            if (debateContents.size() < 5) {
                debateContents.add(debateContent);
                stringRedisTemplate.opsForHash().put(HotContentConstants.HOT_USER_CONTENT_TODAY, debateContent.getOwnerId().toString(), JSON.toJSONString(debateContents));
            }
        }

        return Response.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public Response getContentInfo(Map map) {
        String infoId = (String) map.get("infoId");
        String username = (String) map.get("username");
        String contentId = (String) map.get("contentId");

        if (StringUtils.isBlank(infoId)) {
            return Response.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        DebateContentInfo info = debateContentInfoMapper.selectById(Integer.valueOf(infoId));

        if (info == null || info.getStatus().equals(0)) {
            return Response.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        DebateContentInfoVo vo = new DebateContentInfoVo();
        BeanUtils.copyProperties(info, vo);
        if (StringUtils.isNotBlank(info.getImage())) {
            vo.setImage(JSON.parseArray(info.getImage(), String.class));
        }

        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(contentId)) {
            String json = (String) stringRedisTemplate.opsForHash().get(BehaviorConstants.LIKE_BEHAVIOR + contentId, username);
            if (StringUtils.isNotBlank(json)) {
                vo.setFlag(true);
            }
        }

        return Response.okResult(vo);
    }

    @Override
    public Response deleteById(Map map) {
        String id = (String) map.get("contentId");

        if (StringUtils.isBlank(id)) {
            return Response.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        DebateContent debateContent = contentMapper.selectById(Integer.valueOf(id));

        if (debateContent == null) {
            return Response.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        Integer infoId = debateContent.getInfoId();

        // 删除content
        debateContent.setState(0);
        contentMapper.updateById(debateContent);

        DebateContentInfo debateContentInfo = debateContentInfoMapper.selectById(infoId);
        if (debateContentInfo == null) {
            return Response.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        // 删除info
        debateContentInfo.setStatus(0);
        debateContentInfoMapper.updateById(debateContentInfo);

        DeleteRequest request = new DeleteRequest("content", id);
        // 删除es
        try {
            client.delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 检查帖子缓存中有没有 如果有 则删除
        String json = stringRedisTemplate.opsForValue().get(HotContentConstants.HOT_DATA_TODAY);
        if (StringUtils.isNotBlank(json)) {
            List<DebateContent> debateContents = JSON.parseArray(json, DebateContent.class);
            for (DebateContent content : debateContents) {
                if (content.getId().equals(Integer.valueOf(id))) {
                    debateContents.remove(content);
                    break;
                }
            }

            stringRedisTemplate.opsForValue().set(HotContentConstants.HOT_DATA_TODAY, JSON.toJSONString(debateContents));
        }

        // 检查用户热门帖子里有没有 如果有 删除
        json = (String) stringRedisTemplate.opsForHash()
                .get(HotContentConstants.HOT_USER_CONTENT_TODAY, debateContent.getOwnerId().toString());
        if (StringUtils.isNotBlank(json)) {
            LambdaQueryWrapper<DebateContent> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DebateContent::getOwnerId, debateContent.getOwnerId());
            lambdaQueryWrapper.eq(DebateContent::getState, 1);
            lambdaQueryWrapper.orderByDesc(DebateContent::getScore);
            List<DebateContent> debateContents = contentMapper.selectList(lambdaQueryWrapper);

            if (debateContents == null || debateContents.size() == 0) {
                stringRedisTemplate.opsForHash()
                        .delete(HotContentConstants.HOT_USER_CONTENT_TODAY, debateContent.getOwnerId().toString());
            } else {
                // 只保存前五条
                if (debateContents.size() > 5) {
                    debateContents = debateContents.subList(0, 5);
                }
                stringRedisTemplate.opsForHash()
                        .put(HotContentConstants.HOT_USER_CONTENT_TODAY, debateContent.getOwnerId().toString(), JSON.toJSONString(debateContents));
            }
        }

        return Response.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public Response updateInfo(Map map) {
        String infoId = (String) map.get("infoId");
        String content = (String) map.get("content");

        if (StringUtils.isBlank(infoId)) {
            return Response.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        DebateContentInfo debateContentInfo = debateContentInfoMapper.selectById(Integer.valueOf(infoId));

        if (debateContentInfo == null || debateContentInfo.getStatus() == 0) {
            return Response.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        debateContentInfo.setContent(content);
        debateContentInfoMapper.updateById(debateContentInfo);

        return Response.okResult(AppHttpCodeEnum.SUCCESS);
    }


    @Override
    public void updateScore(ContentVisitStreamMess mess) {
        DebateContent debateContent = updateContent(mess);

        replaceDataToRedis(debateContent, debateContent.getScore(), HotContentConstants.HOT_DATA_TODAY);
    }

    private void replaceDataToRedis(DebateContent debateContent, Integer score, String s) {
        String contentListStr = stringRedisTemplate.opsForValue().get(s);
        if (StringUtils.isNotBlank(contentListStr)) {
            List<DebateContent> debateContents = JSON.parseArray(contentListStr, DebateContent.class);
            boolean flag = true;

            for (DebateContent content : debateContents) {
                if (debateContent.getId().equals(content.getId())) {
                    content.setScore(score);
                    content.setLikes(debateContent.getLikes());
                    content.setRemarks(debateContent.getRemarks());
                    content.setViews(debateContent.getViews());
                    flag = false;
                    break;
                }
            }

            if (flag) {
                if (debateContents.size() >= 10) {
                    debateContents = debateContents.stream()
                            .sorted(Comparator.comparing(DebateContent::getScore).reversed())
                            .collect(Collectors.toList());
                    DebateContent last = debateContents.get(debateContents.size() - 1);
                    if (last.getScore() < score) {
                        debateContents.remove(last);
                        debateContents.add(debateContent);
                        // 替换 并 缓存 新的热点的所属用户的热点文章
                        cacheUserHot(last, debateContent);
                    }
                } else {
                    debateContents.add(debateContent);
                    cacheUserHot(null, debateContent);
                }
            }

            debateContents = debateContents.stream()
                    .sorted(Comparator.comparing(DebateContent::getScore).reversed())
                    .collect(Collectors.toList());
            stringRedisTemplate.opsForValue().set(s, JSON.toJSONString(debateContents));
        }
    }

    /**
     * 删除旧的 并 缓存 新的热点 的所属用户 的热点文章
     * @param old
     * @param n
     */
    private void cacheUserHot(DebateContent old, DebateContent n) {
        if (old != null && old.getOwnerId() != null) {
            stringRedisTemplate.opsForHash()
                    .delete(HotContentConstants.HOT_USER_CONTENT_TODAY, old.getOwnerId().toString());
        }

        saveUserHot(n);
    }

    /**
     * 计算文章分值
     * @param debateContent
     * @return
     */
    private Integer compute(DebateContent debateContent) {
        int score = 0;
        if (debateContent.getLikes() != null) {
            score += debateContent.getLikes() * HotContentConstants.HOT_CONTENT_LIKE_WEIGHT;
        }
        if (debateContent.getRemarks() != null) {
            score += debateContent.getRemarks() * HotContentConstants.HOT_CONTENT_COMMENT_WEIGHT;
        }
        if (debateContent.getViews() != null) {
            score += debateContent.getViews() * HotContentConstants.HOT_CONTENT_VIEW_WEIGHT;
        }

        return score;
    }

    /**
     * 更新文章行为数量
     * @param mess
     * @return
     */
    private DebateContent updateContent(ContentVisitStreamMess mess) {
        DebateContent debateContent = contentMapper.selectById(mess.getContentId());

        debateContent.setViews(debateContent.getViews() == null ? 0 : debateContent.getViews() + mess.getView());
        debateContent.setRemarks(debateContent.getRemarks() == null ? 0 : debateContent.getRemarks() + mess.getComment());
        debateContent.setLikes(debateContent.getLikes() == null ? 0 : debateContent.getLikes() + mess.getLike());

        Integer score = compute(debateContent);
        score = score * 3;
        debateContent.setScore(score);

        // es更新
        UpdateRequest request = new UpdateRequest("content", String.valueOf(debateContent.getId()));
        request.doc(
                "score", debateContent.getScore(),
                "views", debateContent.getViews(),
                "remarks", debateContent.getRemarks(),
                "likes", debateContent.getLikes()
        );
        try {
            client.update(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        contentMapper.updateById(debateContent);
        return debateContent;
    }

    @Override
    public void computeHotArticle() {
        Date date = DateTime.now().minusDays(5).toDate();
        List<DebateContent> contentList = contentMapper.findContentListByLast5days(date);

        cacheToRedis(contentList);
    }

    private void cacheToRedis(List<DebateContent> contentList) {
        contentList = contentList.stream()
                .sorted(Comparator.comparing(DebateContent::getScore).reversed()).collect(Collectors.toList());

        if (contentList.size() > 10) {
            contentList = contentList.subList(0, 10);
        }

        // 缓存热点文章 的所属用户的 热点文章（展示在info中的）
        for (DebateContent debateContent : contentList) {
            saveUserHot(debateContent);
        }

        stringRedisTemplate.opsForValue().set(HotContentConstants.HOT_DATA_TODAY, JSON.toJSONString(contentList));
    }

    private void saveUserHot(DebateContent debateContent) {
        String json = (String) stringRedisTemplate.opsForHash()
                .get(HotContentConstants.HOT_USER_CONTENT_TODAY, debateContent.getOwnerId().toString());
        if (StringUtils.isNotBlank(json)) {
            return;
        }

        LambdaQueryWrapper<DebateContent> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DebateContent::getOwnerId, debateContent.getOwnerId());
        lambdaQueryWrapper.eq(DebateContent::getState, 1);
        lambdaQueryWrapper.orderByDesc(DebateContent::getScore);
        List<DebateContent> debateContents = contentMapper.selectList(lambdaQueryWrapper);

        // 只保存前五条
        if (debateContents.size() > 5) {
            debateContents = debateContents.subList(0, 5);
        }

        stringRedisTemplate.opsForHash()
                .put(HotContentConstants.HOT_USER_CONTENT_TODAY, debateContent.getOwnerId().toString(), JSON.toJSONString(debateContents));
    }

    @Override
    public Response getHotByUser(Map map) {
        String username = (String) map.get("username");
        // 如果有缓存
        String json = (String) stringRedisTemplate.opsForHash().get(HotContentConstants.HOT_USER_CONTENT_TODAY, username);
        if (StringUtils.isNotBlank(json)) {
            List<DebateContent> debateContents = JSON.parseArray(json, DebateContent.class);
            return getResponse(debateContents);
        }

        // 没有缓存
        LambdaQueryWrapper<DebateContent> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DebateContent::getOwnerId, Integer.valueOf(username));
        lambdaQueryWrapper.eq(DebateContent::getState, 1);
        lambdaQueryWrapper.orderByDesc(DebateContent::getScore);
        List<DebateContent> debateContents = contentMapper.selectList(lambdaQueryWrapper);
        // 只需要5条
        if (debateContents.size() > 5) {
            debateContents = debateContents.subList(0, 5);
        }

        return getResponse(debateContents);
    }

    /**
     * pojo转化为vo
     * @param debateContents
     * @return
     */
    private Response getResponse(List<DebateContent> debateContents) {
        List<DebateContentVo> vos = new ArrayList<>();

        for (DebateContent debateContent : debateContents) {
            DebateContentVo vo = new DebateContentVo();
            BeanUtils.copyProperties(debateContent, vo);
            vos.add(vo);
        }

        return Response.okResult(vos);
    }

    @Override
    public Response getContentByUserId(Map map) {
        String username = (String) map.get("username");
        if (StringUtils.isBlank(username)) {
            return Response.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        LambdaQueryWrapper<DebateContent> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DebateContent::getOwnerId, Integer.valueOf(username));
        lambdaQueryWrapper.orderByDesc(DebateContent::getCreateTime);
        lambdaQueryWrapper.eq(DebateContent::getState, 1);
        List<DebateContent> debateContents = contentMapper.selectList(lambdaQueryWrapper);

        return getResponse(debateContents);
    }

    @Override
    public Response getContentById(Map map) {
        String  id = (String) map.get("id");

        if (StringUtils.isBlank(id)) {
            return Response.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        DebateContent debateContent = contentMapper.selectById(id);

        if (debateContent == null) {
            return Response.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        return Response.okResult(debateContent);
    }
}
