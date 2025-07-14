package org.submerge.subaiagent.chatmemory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: FileBasedChatmemory
 * Package: org.submerge.subaiagent.chatmemory
 * Description: 对话记忆文件持久化
 *
 * @Author Submerge--WangDong
 * @Create 2025/7/14 19:59
 * @Version 1.0
 */
public class FileBasedChatmemory implements ChatMemory {


    private final File storageDir;
    private static Kryo kryo = new Kryo();


    static {
        kryo.setRegistrationRequired(false);
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
    }

    //    支持默认和自定义 Kryo 实例
    public FileBasedChatmemory(String storagePath) {
        this(storagePath, createDefaultKryo());
    }

    public FileBasedChatmemory(String storagePath, Kryo kryo) {
        this.storageDir = new File(storagePath);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
    }
    // Kryo 配置（默认）
    private static Kryo createDefaultKryo() {
        Kryo kryo = new Kryo();
        kryo.setReferences(true);
        kryo.register(ArrayList.class);
        return kryo;
    }


    private File getConversationFile(String conversationId) {
        return new File(storageDir, conversationId + ".bin");
    }

    private List<Message> readMessagesFromFile(File file) {
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        try (InputStream is = new FileInputStream(file);
             Input input = new Input(is)) {
            return kryo.readObject(input, ArrayList.class);
        } catch (Exception e) {
            throw new RuntimeException("Error reading messages from file: " + file.getAbsolutePath(), e);
        }
    }

    private void writeMessagesToFile(File file, List<Message> messages) {
        try (OutputStream os = new FileOutputStream(file);
             Output output = new Output(os)) {
            kryo.writeObject(output, messages);
        } catch (Exception e) {
            throw new RuntimeException("Error writing messages to file: " + file.getAbsolutePath(), e);
        }
    }


    @Override
    public void add(String conversationId, Message message) {
        File file = getConversationFile(conversationId);
        List<Message> messages = readMessagesFromFile(file);
        messages.add(message);
        writeMessagesToFile(file, messages);
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        File file = getConversationFile(conversationId);
        List<Message> existingMessages = readMessagesFromFile(file);
        existingMessages.addAll(messages);
        writeMessagesToFile(file, existingMessages);
    }


    @Override
    public List<Message> get(String conversationId, int lastN) {
        File file = getConversationFile(conversationId);
        List<Message> allMessages = readMessagesFromFile(file);
        int startIndex = Math.max(0, allMessages.size() - lastN);
        return new ArrayList<>(allMessages.subList(startIndex, allMessages.size()));
    }

    @Override
    public void clear(String conversationId) {
        File file = getConversationFile(conversationId);
        if (file.exists()) {
            file.delete();
        }
    }
}
