package tgb.btc.rce.service.handler.impl;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.service.handler.IUpdateHandler;
import tgb.btc.rce.service.process.IUserProcessService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MyChatMemberHandler implements IUpdateHandler {

    private static final String PRIVATE_CHAT_TYPE = "private";

    private static final String MEMBER_STATUS = "member";

    private final ConcurrentHashMap<Long, CompletableFuture<Void>> futures = new ConcurrentHashMap<>();

    private final IUserProcessService userProcessService;

    public MyChatMemberHandler(IUserProcessService userProcessService) {
        this.userProcessService = userProcessService;
    }

    @Override
    public boolean handle(Update update) {
        if (isStartedBot(update.getMyChatMember())) {
            Long chatId = update.getMyChatMember().getChat().getId();
            CompletableFuture<Void> future = new CompletableFuture<>();
            if (futures.putIfAbsent(chatId, future) != null) {
                return false;
            }
            try {
                userProcessService.registerIfNotExists(update);
            } catch (Exception e) {
                future.completeExceptionally(e);
            } finally {
                futures.remove(chatId);
            }
        }
        return true;
    }

    private boolean isStartedBot(ChatMemberUpdated chatMemberUpdated) {
        return PRIVATE_CHAT_TYPE.equals(chatMemberUpdated.getChat().getType())
                && MEMBER_STATUS.equals(chatMemberUpdated.getNewChatMember().getStatus())
                && !MEMBER_STATUS.equals(chatMemberUpdated.getOldChatMember().getStatus());
    }

    @Override
    public UpdateType getUpdateType() {
        return UpdateType.MY_CHAT_MEMBER;
    }

    public CompletableFuture<Void> getRegistering(Long chatId) {
        return futures.get(chatId);
    }
}
