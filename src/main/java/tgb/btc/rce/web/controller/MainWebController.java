package tgb.btc.rce.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tgb.btc.rce.bean.Role;
import tgb.btc.rce.bean.WebUser;
import tgb.btc.rce.repository.WebUserRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Controller
@RequestMapping("/web")
@Slf4j
public class MainWebController {

    public static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper();

    public final static Map<Long, String> AVAILABLE_TOKENS = new HashMap<>();

    private WebUserRepository webUserRepository;

    @Autowired
    public void setWebUserRepository(WebUserRepository webUserRepository) {
        this.webUserRepository = webUserRepository;
    }

    @GetMapping("/main")
    public String web(HttpServletRequest request,
                      @RequestParam(required = false) Long chatId,
                      @RequestParam(required = false) String username,
                      @RequestParam(required = false) String token) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            if (Objects.nonNull(chatId) && StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(token)
                    && Objects.nonNull(AVAILABLE_TOKENS.get(chatId)) && AVAILABLE_TOKENS.get(chatId).equals(token)) {

                WebUser webUser = webUserRepository.getByUsername(username);
                String[] roles = new String[webUser.getRoles().size()];
                int i = 0;
                for (Role role : webUser.getRoles()) {
                    roles[i] = role.getName();
                }
                auth = new UsernamePasswordAuthenticationToken(webUserRepository.getByUsername(username), null,
                        AuthorityUtils.createAuthorityList(roles)
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
                HttpSession session = request.getSession(true);
                SecurityContext sc = SecurityContextHolder.getContext();
                session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);
                return "main";
            }
            return "login";
        };
        return "main";
    }
}
