package org.submerge.subaiagent.advisor;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.*;
import reactor.core.publisher.Flux;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
public class WebBlocklistAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {

    // 使用原子引用保证线程安全更新
    private volatile AtomicReference<Set<String>> blocklist = new AtomicReference<>(
            Collections.newSetFromMap(new ConcurrentHashMap<>()));

    // 默认本地备份文件路径
    private static final String LOCAL_BACKUP_PATH = "static/SensitiveLexicon.json";
    private static final long REFRESH_INTERVAL = TimeUnit.HOURS.toMillis(24);  // 每天刷新一次

    private ScheduledFuture<?> refreshTask;
    private ScheduledExecutorService scheduler;

    // 初始化加载违禁词
    public WebBlocklistAdvisor() {
        init();
    }

    private void init() {
        // 创建守护线程池
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);  // 设置为守护线程，避免阻塞JVM退出
            t.setName("Blocklist-Refresh-Thread");
            return t;
        });

        // 首次加载
        if (!loadBlocklist()) {
            log.error(" 初始违禁词加载失败，系统可能处于不安全状态");
            // 可以选择抛出异常或采取其他措施
        }

        // 启动定时刷新任务
        refreshTask = scheduler.scheduleAtFixedRate(this::loadBlocklist,
                REFRESH_INTERVAL,
                REFRESH_INTERVAL,
                TimeUnit.MILLISECONDS);
    }

    private boolean loadBlocklist() {
        try {
            Set<String> newBlocklist = readFromLocal();
            if (newBlocklist == null || newBlocklist.isEmpty()) {
                log.warn(" 加载到空违禁词表");
                return false;
            }

            // 原子更新：确保加载过程的原子性
            blocklist.getAndUpdate(old -> {
                old.clear();
                old.addAll(newBlocklist);
                return old;
            });

            log.info(" 成功刷新违禁词库，当前数量：{}", blocklist.get().size());
            return true;
        } catch (Exception e) {
            log.error(" 加载违禁词失败", e);
            return false; // 返回失败状态
        }
    }

    private Set<String> readFromLocal() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getClassLoader().getResourceAsStream(LOCAL_BACKUP_PATH);

        if (is == null) {
            throw new IOException("敏感词文件不存在: " + LOCAL_BACKUP_PATH);
        }

        try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            // 读取JSON并映射到BlocklistData对象
            BlocklistData data = mapper.readValue(reader, BlocklistData.class);

            // 从对象中获取words数组并处理
            if (data.getWords() == null) {
                throw new IOException("敏感词文件格式错误: 缺少words字段");
            }

            // 转换为小写并过滤空值
            return Arrays.stream(data.getWords())
                    .filter(Objects::nonNull)
                    .map(String::toLowerCase)
                    .collect(Collectors.toCollection(ConcurrentSkipListSet::new));  // 有序且线程安全
        }
    }

    @Override
    public String getName() {
        return "WebBlocklistAdvisor";
    }

    @Override
    public int getOrder() {
        return 0; // 最高优先级
    }

    // 请求前检查
    private AdvisedRequest before(AdvisedRequest request) {
        String userInput = request.userText().toLowerCase();   // 一次性转换为小写

        // 快速检查（使用并行流加速）
        List<String> found = blocklist.get().parallelStream()
                .filter(word -> word.length() > 0 && userInput.contains(word))
                .limit(10) // 限制最多返回10个敏感词
                .collect(Collectors.toList());

        if (!found.isEmpty()) {
            String warnMsg = String.format(" 检测到敏感内容（%d项）：%s", found.size(), String.join(",  ", found));
            log.warn(warnMsg);
            throw new SecurityException(warnMsg);
        }

        return request;
    }

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        return chain.nextAroundCall(before(advisedRequest));
    }

    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        return chain.nextAroundStream(before(advisedRequest));
    }

    // 添加优雅关闭方法
    public void destroy() {
        if (refreshTask != null) {
            refreshTask.cancel(true);
        }
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }

    // 添加内部类来匹配JSON结构
    @Getter
    @Setter
    private static class BlocklistData {
        private String lastUpdateDate;
        private String[] words;
    }
}