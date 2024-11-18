package tgb.btc.rce.enums.update;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tgb.btc.library.constants.enums.bot.UserRole;

import java.util.Set;

@Getter
@AllArgsConstructor
public enum SlashCommand {
    START("/start", UserRole.USER_ACCESS),
    HELP("/help", UserRole.OPERATOR_ACCESS),
    CHAT_ID("/chatid", UserRole.USER_ACCESS);

    private final String text;
    private final Set<UserRole> roles;

    public boolean hasAccess(UserRole role) {
        return this.getRoles().contains(role);
    }
}
